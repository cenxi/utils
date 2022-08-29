package common.utils.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * FTP连接工厂
 *
 * <p>
 * FTP连接工厂
 * </p>
 * 
 */
public abstract class FTPConnectionFactory implements PooledObjectFactory<FTPClient> {
  private Logger logger = LoggerFactory.getLogger(FTPConnectionFactory.class);

  public FTPConnectionFactory() {

  }

  /**
   * 向连接池中添加一个sftp连接通道
   */
  @Override
  public PooledObject<FTPClient> makeObject() throws Exception {
    FTPClient ftpClient = this.connection();
    return new DefaultPooledObject<>(ftpClient);
  }

  /**
   * 清理方法
   */
  @Override
  public void destroyObject(PooledObject<FTPClient> pooledObject) throws Exception {
    FTPClient ftpClient = pooledObject.getObject();
    logout(ftpClient);
  }

  public void logout(FTPClient ftpClient) {
    try {
      if (ftpClient != null && ftpClient.isConnected()) {
        logger.info("logout begin...");
        ftpClient.logout();
      }
      logger.info("...ftpClient is logout...");
    } catch (Exception e) {
      logger.warn("关闭连接-进行logout出现异常处理",e);
    } finally {
      try {
        if (ftpClient!=null) {
          ftpClient.disconnect();
        }
      } catch (Exception e) {
        logger.warn("关闭连接-进行disconnect出现异常:{}",e.getMessage());
      }
    }
  }

  /**
   * 判断链接是否正常方法
   */
  @Override
  public boolean validateObject(PooledObject<FTPClient> pooledObject) {
    try {
      FTPClient ftpClient = pooledObject.getObject();
      return ftpClient.sendNoOp();
    } catch (Exception e) {
      logger.error("Failed to validate client:{}",e);
      return false;
    }

  }

  /**
   * 启用一个sftp链接会话
   */
  @Override
  public void activateObject(PooledObject<FTPClient> pooledObject) throws Exception {
    try {
      FTPClient ftpClient = pooledObject.getObject();
      int pwd = ftpClient.pwd();
    } catch (Exception e) {
      this.connection();
    }
  }

  /**
   * 钝化 相关操作
   */
  @Override
  public void passivateObject(PooledObject<FTPClient> pooledObject) throws Exception {

  }

  /**
   * 连接sftp
   * 
   * @return 连接的会话
   * @throws Exception
   */
  protected abstract FTPClient connection() throws Exception;
}
