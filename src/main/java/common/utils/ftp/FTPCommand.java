package common.utils.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * FTP的命令控制类
 *
 * <p>
 * ftp命令控制类
 * </p>
 *
 * @see
 */
@Component("ftpCommand")
@Lazy
@Slf4j
public class FTPCommand {

    @Autowired
    private GenericObjectPool<FTPClient> ftpPool;

    /**
     * 创建指定目录
     *
     * @param path 要创建的目录
     */
    public void mkdir(String path) throws Exception {
        FTPClient ftpClient = ftpPool.borrowObject();
        try {
            String[] subPaths = path.split("/");
            String pathDir = "";
            for (String subpath : subPaths) {
                if (!subpath.isEmpty()) {
                    ftpClient.makeDirectory(subpath);
                    pathDir += ("/" + subpath);
                    boolean changeWorkingDirectory = ftpClient.changeWorkingDirectory(pathDir);
                    if (!changeWorkingDirectory) {
                        throw new RuntimeException("directory not found , create directory error !");
                    }
                    log.info("mkdir directory " + pathDir + " success!");
                }
            }
        } finally {
            try {
                ftpClient.changeWorkingDirectory("/");
            } catch (IOException e) {
                log.info("exception:", e);
            }
            ftpPool.returnObject(ftpClient);
        }
    }


    /**
     * 文件扫描读入
     */
    long p = 0;

    public FTPFile[] listDirs(String path) throws Exception {

        FTPClient ftpClient = null;
        boolean b = p++ % 10 == 0;
        try {
            long start = System.currentTimeMillis();
            if (b) {
                log.info("threadName {} listDirs borrowObject begin...", Thread.currentThread().getName());
            }
            ftpClient = ftpPool.borrowObject();
            if (b) {
                log.info("threadName {} listDirs borrowObject end...", Thread.currentThread().getName());
            }
            boolean sendNoOp = ftpClient.sendNoOp();
            if (!sendNoOp) {
                ftpPool.invalidateObject(ftpClient);
                return null;
            }
            if (b) {
                log.info("threadName {} listDirectories begin...", Thread.currentThread().getName());
            }
            FTPFile[] listFiles = ftpClient.listFiles(path);
            if (b) {
                log.info("threadName {} listDirectories end... use time {} ms", Thread.currentThread().getName(),
                        (System.currentTimeMillis() - start));
            }
            return listFiles;
        } finally {
            ftpPool.returnObject(ftpClient);
        }
    }

    /**
     * 扫描指定的路径
     *
     * @param path          ftp中的路径
     * @param ftpFileFilter ftp文件过滤器
     * @return 文件目录
     */
    public List<FTPFile> scanLocation(String path, FTPFileFilter ftpFileFilter) throws Exception {
        String location = path + "/";
        List<FTPFile> list = new ArrayList<>();
        FTPClient client = null;
        try {
            client = ftpPool.borrowObject();
            if (!client.sendNoOp()) {
                ftpPool.invalidateObject(client);
                return null;
            }
            FTPFile[] ftpFiles;
            log.info("主机ip：{}，端口：{}", client.getLocalAddress().getHostName(), client.getLocalPort());
            if (ftpFileFilter != null) {
                ftpFiles = client.listFiles(location, ftpFileFilter);
                log.info("扫描路径：{}下文件个数：{}", location, ftpFiles.length);
            } else {
                ftpFiles = client.listFiles(location);
            }

            return Arrays.asList(ftpFiles);
        } finally {
            ftpPool.returnObject(client);
        }
    }

    /*public FTPFile[] getFTPDirectoryFiles(String path, FTPFileFilter filter) {
        FTPFile[] files = null;
        try {
            FTPClient ftpClient = ftpPool.borrowObject();
            files = ftpClient.listFiles(path, filter);
        } catch (Exception e) {
            log.error("FTP读取数据错误:" + e);
        }

        return files;
    }*/


    /**
     * 下载文件
     *
     * @param ftpFileName 要下载的文件名
     * @return 下载到的文件
     * @throws Exception
     */
    public Boolean downLoadFile(String path, String ftpFileName, String localPath) {
        String remoteFilePath = path + "/" + ftpFileName;
        long start = System.currentTimeMillis();
        FTPClient ftpClient = null;
        int byteLength = -1;
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            log.info("readFile {} begin...", remoteFilePath);
            ftpClient = ftpPool.borrowObject();
            if (!ftpClient.sendNoOp()) {
                log.warn("readFile {} stop...", remoteFilePath);
                ftpPool.invalidateObject(ftpClient);
                return false;
            }
            ftpClient.changeWorkingDirectory(path);
            boolean b = fileUploadSuccess(remoteFilePath);
            if (!b) {
                log.warn("计算文件长度失败！");
                return false;
            }
            FileOutputStream out = null;
            InputStream in = null;
            try {
                log.info("retrieveFileStream {} begin...", remoteFilePath);
                in = ftpClient.retrieveFileStream(remoteFilePath);
                byte[] bytes = IOUtils.toByteArray(in);
                File localFile = new File(localPath + File.separator + ftpFileName);
                ByteBuffer bb = ByteBuffer.wrap(bytes);
                out = new FileOutputStream(localFile);
                FileChannel fc = out.getChannel();
                fc.write(bb);
                fc.close();
                out.close();
                in.close();
                ftpClient.completePendingCommand();
            } catch (Exception e) {
                log.error("ftpClient.retrieveFileStream error " + remoteFilePath, e);
                return false;
            }

            long timeConsume = (System.currentTimeMillis() - start);
            log.info("readFile {} AsBytes used {} ms with {} bytes", remoteFilePath, timeConsume, byteLength);
        } catch (Exception e) {
            log.error("retriveFileAsBytes " + remoteFilePath + "with " + byteLength, e);
            return false;
        } finally {
            ftpPool.returnObject(ftpClient);
        }
        return true;
    }

    public String readFtpFile(String path, String ftpFileName) {
        String remoteFilePath = path + "/" + ftpFileName;
        long start = System.currentTimeMillis();
        FTPClient ftpClient = null;
        int byteLength = -1;
        String res = "";
        try {
            log.info("readFile {} begin...", remoteFilePath);
            ftpClient = ftpPool.borrowObject();
            if (!ftpClient.sendNoOp()) {
                log.warn("readFile {} stop...", remoteFilePath);
                ftpPool.invalidateObject(ftpClient);
                return "";
            }
            ftpClient.changeWorkingDirectory(path);
            boolean b = fileUploadSuccess(remoteFilePath);
            if (!b) {
                log.warn("计算文件长度失败！");
                return "";
            }
            InputStream in = null;
            try {
                log.info("retrieveFileStream {} begin...", remoteFilePath);
                in = ftpClient.retrieveFileStream(remoteFilePath);
                byte[] bytes = IOUtils.toByteArray(in);
                ftpClient.completePendingCommand();
                res = new String(bytes, "utf-8");
            } catch (Exception e) {
                log.error("ftpClient.retrieveFileStream error " + remoteFilePath, e);
                return "";
            }

            long timeConsume = (System.currentTimeMillis() - start);
            log.info("readFile {} AsBytes used {} ms with {} bytes", remoteFilePath, timeConsume, byteLength);
        } catch (Exception e) {
            log.error("retriveFileAsBytes " + remoteFilePath + "with " + byteLength, e);
            return "";
        } finally {
            ftpPool.returnObject(ftpClient);
        }
        return res;
    }

    public Boolean reName(String fromPath, String toPath) throws Exception {
        FTPClient ftpClient = ftpPool.borrowObject();
        boolean isSuccess = false;
        try {
            isSuccess = ftpClient.rename(fromPath, toPath);
            /*if (isSuccess) {
                log.info("rename file {} to {} success", fromPath, toPath);
            }*/
        } finally {
            try {
                ftpPool.returnObject(ftpClient);
            } catch (Exception e) {
                log.error("reName stream close failed", e);
            }
        }
        return isSuccess;
    }

    public void uploadFile(String path, String fileName,
                           InputStream stream) throws Exception {
        FTPClient client = null;
        try {
            client = ftpPool.borrowObject();
            if (!client.changeWorkingDirectory(path)) {
                String[] dirs = path.split("/");
                String tempPath = "/";
                //进入并创建目录
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    tempPath += "/" + dir;
                    if (!client.changeWorkingDirectory(tempPath)) {
                        client.makeDirectory(tempPath);
                    }
                    client.changeWorkingDirectory(tempPath);
                }

            }
            client.setFileType(FTP.LOCAL_FILE_TYPE);
            boolean flag = client.storeFile(path + "/" + fileName, stream);
            if (flag) {
                stream.close();
            } else {
//                throw new RuntimeException("文件上传失败");
                log.error("文件上传失败");
            }
        } finally {
            try {
                ftpPool.returnObject(client);
                stream.close();
            } catch (Exception e) {
                log.warn("uploadFile stream close failed", e);
            }
        }
    }


    /**
     * 删除指定的文件
     *
     * @param ftpFileName ftp中的文件名
     */
    public Boolean deleteFile(String path, String ftpFileName) {
        String remoteFilePath = path + "/" + ftpFileName;
        FTPClient ftpClient = null;
        boolean deleteFile = false;
        try {
            ftpClient = ftpPool.borrowObject();
            ftpClient.changeWorkingDirectory(path);
            deleteFile = ftpClient.deleteFile(remoteFilePath);
            log.info("delete file " + remoteFilePath + " - " + deleteFile);
        } catch (Exception e) {
            log.error("删除Ftp文件失败.ftpFile->{}", remoteFilePath);
        } finally {
            ftpPool.returnObject(ftpClient);
        }
        return deleteFile;
    }

    public Map<String, Object> getGenericObjectPoolParam() {

        Map<String, Object> map = new HashMap<>();
        try {
            //
            long createdCount = ftpPool.getCreatedCount();
            // 空闲数
            int numIdle = ftpPool.getNumIdle();
            // 活跃数
            int numActive = ftpPool.getNumActive();
            // 返回池数
            long returnedCount = ftpPool.getReturnedCount();
            // 销毁数
            long destroyedCount = ftpPool.getDestroyedCount();
            //
            long borrowedCount = ftpPool.getBorrowedCount();
            //
            int numWaiters = ftpPool.getNumWaiters();
            //
            map.put("createdCount", createdCount);
            map.put("numIdle", numIdle);
            map.put("numActive", numActive);
            map.put("returnedCount", returnedCount);
            map.put("destroyedCount", destroyedCount);
            map.put("borrowedCount", borrowedCount);
            map.put("numWaiters", numWaiters);
        } catch (Exception e) {
            log.error("getGenericObjectPool param error", e);
        }
        return map;
    }

    /**
     * 间隔一段时间去计算文件的长度来判断文件是否写入完成
     */
    public Boolean fileUploadSuccess(String remoteFilePath) {
        FTPFile mdtmFile = null;
        try {
            mdtmFile = mlistFileCommand(remoteFilePath);
            if (mdtmFile == null) {
                return false;
            }
            long len1 = 0, len2 = 0;
            len2 = mdtmFile.getSize();
            do {
                log.info("fileUploadSuccess do while ing...len2 {}", len2);
                len1 = len2;
                // 线程休息1s
                Thread.sleep(100);
                mdtmFile = mlistFileCommand(remoteFilePath);
                if (mdtmFile == null) {
                    Thread.sleep(1000);
                    mdtmFile = mlistFileCommand(remoteFilePath);
                }
                len2 = mdtmFile.getSize();
            } while (len1 < len2);
            return true;
        } catch (Exception e) {
            log.error("fileUploadSuccess error", e);
            return false;
        }
    }

    public FTPFile mlistFileCommand(String remoteFilePath) {

        File file = new File(remoteFilePath);
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpPool.borrowObject();

            boolean sendNoOp = ftpClient.sendNoOp();
            if (!sendNoOp) {
                ftpPool.invalidateObject(ftpClient);
                return null;
            }
            FTPFile[] listFiles = ftpClient.listFiles(file.getParent());
            if (null == listFiles || listFiles.length == 0) {
                return null;
            }
            for (FTPFile ftpFile : listFiles) {
                if (ftpFile.getName().equals(file.getName())) {
                    return ftpFile;
                }
            }
        } catch (Exception e) {
            log.error("mlistFile error " + remoteFilePath, e);
        } finally {
            ftpPool.returnObject(ftpClient);
        }
        return null;
    }

    int i = 0;

    public String test2(String path) {
        ++i;
        if (i < 5) {
            throw new RuntimeException("ftp测试异常");
        }
        log.info(path);
        return "success";

    }

}
