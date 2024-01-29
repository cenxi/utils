package common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author ：fengxi
 * @date ：Created in 2020-06-12 17:00
 * @description：
 * @modified By：
 */
public class FileUtils {

    /**
     * 递归遍历目录下所有文件(非递归方式)
     * @param dirPath
     * @return
     */
    public static List<File> listAllFile(String dirPath) {
        LinkedList<File> allFiles = new LinkedList<>();
        if ("".equals(dirPath) || null == dirPath) {
            return allFiles;
        }
        File file = new File(dirPath);
        if (file.isFile()) {
            return allFiles;
        }

        File[] fileList = file.listFiles((subFile)->{

            return !subFile.isDirectory();
        });
        File[] dirList=file.listFiles((subFile)->{

            return subFile.isDirectory();
        });
        //将第一次获取到的文件和目录放入总集合中
        allFiles.addAll(Arrays.asList(fileList));
        if (dirList == null || dirList.length == 0) {
            return allFiles;
        }

        //声明存放目录的集合
        LinkedList<File> dirCheckList = new LinkedList<>();
        //处理第一层目录

        dirCheckList.addAll(Arrays.asList(dirList));

        while (!dirCheckList.isEmpty()) {
            File removeDir = dirCheckList.removeFirst();//移除首位的目录
            File[] removeFileList = removeDir.listFiles((subFile->{
                return !subFile.isDirectory();
            }));
            File[] removeDirList = removeDir.listFiles((subFile->{
                return subFile.isDirectory();
            }));
            if (removeFileList == null&&removeDirList==null) {
                continue;
            }
            //将找出的所有的文件和目录加入到总集合中
            allFiles.addAll(Arrays.asList(removeFileList));
            dirCheckList.addAll(Arrays.asList(removeDirList));
        }
        return allFiles;
    }

    /**
     * 获取带单位的文件名，单位会自动显示为合适的值，如B、KB、MB等
     * @param size 文件字节大小
     */
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 文件重命名
     * @param filePath
     * @return
     */
    public static String renameFilePath(String filePath) {
        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }

        File file = new File(filePath);
        String fileName = file.getName();
        String namePrefix = fileName;
        String nameSuffix = "";
        if (fileName.indexOf(".") > 1) {
            namePrefix = fileName.substring(0, fileName.lastIndexOf("."));
            nameSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        }

        String fileDir = file.getParent();
        int count = 1;
        String finalPath = filePath;
        while (file.exists()) {
            finalPath = fileDir + File.separator + namePrefix +"("+ count+")"+nameSuffix;
            count++;
            file = new File(finalPath);
        }
        return finalPath;
    }

    /**
     * 获取当前jar包所在路径
     * @return
     */
    public static String getJarDir() {
        String path = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
        }
        path = path.replace("file:", "");
        return path;
    }

    /**
     * 设置java设置Linux文件的权限
     * @param file 文件路径
     * @param permissions 例如:rwxrwxrwx
     */
    public static void setFilePermissions(String file,String permissions) {

        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
        try {
            Files.setPosixFilePermissions(Paths.get(file), perms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println( readableFileSize(268435456l));
    }
}
