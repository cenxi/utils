package common.utils.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 获取ftp数据连接池
 *
 **/
@Component("FTPObtainConnectionPool")
@Lazy
@Slf4j
public class FTPObtainConnectionPool extends ConnectionPool<FTPClient> {

    private FTPConnectionFactory ftpConnectionFactory = new FTPConnectionFactory() {
        @Override
        protected FTPClient connection() throws Exception {
            long start = System.currentTimeMillis();
            FTPClient client = new FTPClient();
            client.setControlEncoding("UTF-8");
            client.setDefaultTimeout(netgateConfig.getDefaultTimeout());
            // 连接超时为60秒
            client.setConnectTimeout(netgateConfig.getConnectTimeout());
            client.setDataTimeout(netgateConfig.getDataTimeout());

            String[] split = netgateConfig.getObtainFtpAddr().split(":");
            client.connect(split[0], Integer.valueOf(split[1]));
            // 设置传输超时时间为60秒
            client.setSoTimeout(netgateConfig.getSoTimeout());
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                log.error("FTPServer refused connection...");
                return null;
            }
            boolean result = client.login(netgateConfig.getObtainFtpUsername(), netgateConfig.getObtainFtpPassword());
            if (!result) {
                log.error("ftpClient  login fail! userName:" + netgateConfig.getObtainFtpUsername() + " ; password:" + netgateConfig.getObtainFtpPassword());
                throw new Exception("login failed with FTP server:" + split[0] + " may user name and password is wrong");
            }
            //
            client.configure(new FTPClientConfig(client.getSystemType()));
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            client.setBufferSize(1024 * 1024);

            if(netgateConfig.getFtpMode()== FtpMode.PASS){
                log.info("ftp设置被动模式..........................");
                client.enterLocalPassiveMode();
            }else{
                log.info("ftp设置主动模式..........................");
                client.enterLocalActiveMode();
            }

            log.info("connect connection {} successfully with {} ms", netgateConfig.getObtainFtpAddr(), (System.currentTimeMillis() - start));
            return client;
        }
    };

    // 创建连接的工厂
    @Override
    public GenericObjectPool<FTPClient> createGenericObjectPool(GenericObjectPoolConfig config) {

        return new GenericObjectPool<>(ftpConnectionFactory, config);
    }
}
