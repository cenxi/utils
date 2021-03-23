package common.utils.ftp;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Data
@Configuration
@Slf4j
public class NetgateConfig {

    /**************** 连接池相关配置 **********************/
    //最大连接数
    @Value("${connection.pool.max.total:30}")
    private int maxTotal;
    //最大空闲连接数
    @Value("${connection.pool.max.idle:20}")
    private int maxIdle;
    //最小空闲连接数
    @Value("${connection.pool.min.idle:10}")
    private int minIdle;
    //连接池建立连接等待时间
    @Value("${connection.pool.max.wait.millis:10000}")
    private long maxWaitMillis;
    //连接池连接空闲时间
    @Value("${connection.pool.min.evictable.idle.time.millis:3600000}")
    private long minEvictableIdleTimeMillis;
    //连接池轮询验活时间
    @Value("${connection.pool.time.between.eviction.runs.millis:60000}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${defaultTimeout:60000}")
    private int defaultTimeout;

    @Value("${connectTimeout:60000}")
    private int connectTimeout;

    //设置从数据连接读取时使用的超时（以毫秒为单位）。
    @Value("${dataTimeout:60000}")
    private int dataTimeout;

    //设置当前打开的连接的超时（以毫秒为单位）
    @Value("${soTimeout:60000}")
    private int soTimeout;


    //#########ftp连接配置##############
    @Value("${obtain.ftp.username:uftp}")
    private String obtainFtpUsername;
    @Value("${obtain.ftp.password:uftp}")
    private String obtainFtpPassword;
    @Value("${obtain.ftp.addr:10.231.50.177:10023}")
    private String obtainFtpAddr;
    @Value("${obtain.ftp.path:/}")
    private String obtainFtpPath;
    /**
     * 是否启用未上传成功标识。after:后缀标识，before：前缀标识，no：不启用
     */
    @Value("${wait.file.prefix.enabled:ing}")
    private String waitFilePrefixEnabled="no";
    /**
     * 文件未上传完毕，文件名前缀
     */
    @Value("${wait.file.prefix.name:#}")
    private String waitFilePrefixName="#";
    /**
     * 是否启用文件后缀名。true:启用。false：不启用。
     */
    @Value("${file.suffix.enabled:true}")
    private String fileSuffixEnabled="true";
    /**
     * 是否启用文件前缀名。true:启用。false：不启用。
     */
    @Value("${file.prefix.enabled:true}")
    private String filePrefixEnabled="true";
    /**
     * 延时处理策略
     */
    @Value("${cached.data.number:50}")
    private int cachedDataNumber = 50;
    @Value("${fileDelayDeal:false}")
    private boolean fileDelayDeal = false;
    /**
     * 删除不完整超时文件  超时时间 ,单位：秒
     */
    @Value("${wait.timeout.seconds:60}")
    private String waitTimeoutSeconds;


    @Value("${ftp.mode:PASS}")
    private FtpMode ftpMode;

    @PostConstruct
    public void init(){
        log.info("maxTotal->{}", maxTotal);
        log.info("maxIdle->{}", maxIdle);
        log.info("minIdle->{}", minIdle);
        log.info("ftpUsername->{}", obtainFtpUsername);
        log.info("ftpPassword->{}", obtainFtpPassword);
        log.info("ftpAddr->{}", obtainFtpAddr);
    }
}
