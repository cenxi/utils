package common.utils.email;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.UserPassAuthenticator;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * EmailUtil
 *
 * @author gaoweicong
 */
@Slf4j
public final class EmailUtil {

    public static final int PROXY_RETRY_TIMES = 5;

    /**
     * 发邮件
     *
     * @param to 收件人
     * @param title 标题
     * @param content 内容
     * @param attach 附件
     * @return boolean
     */
    public static boolean sendMsg(
            List<String> to,
            String title,
            String content,
            List<String> attach,
            EmailProperties config) {

        // 创建一个配置文件并保存
        Properties properties = new Properties();
        properties.put("mail.smtp.host", config.getHost());
        properties.put("mail.smtp.port", config.getPort());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.mime.encodeparameters", "true");
        properties.put("mail.mime.splitlongparameters", "true");
        properties.put("mail.smtp.timeout", "30000");
        properties.put("mail.smtp.connectiontimeout", "10000");
        if (null != config.getProperties()){
            properties.putAll(config.getProperties());
        }

        try {
            // 获得邮件会话对象
            Session session =
                    Session.getInstance(properties, new UserPassAuthenticator(config.getFrom(), config.getPassword()));
            // 创建邮件对象
            MimeMessage mimeMessage = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 发件人
            helper.setFrom(new InternetAddress(config.getFrom(), config.getUser()));
            // 收件人
            helper.setTo(listToAddrArray(to));
            // 主题
            helper.setSubject(title);
            helper.setText(content, true);
            // 附件
            if (attach != null){
                attach.forEach(
                        url -> {
                            if (ObjectUtil.isNotEmpty(url)){
                                try {
                                    FileSystemResource resource = new FileSystemResource(url);
                                    helper.addAttachment(url, resource);
                                } catch (Exception e) {
                                    log.error("邮件添加附件失败");
                                }
                            }
                        });
            }
            // 传输
            Exception exp = new Exception();
            Integer retryTimes = config.getRetryTimes();
            if (null == retryTimes || retryTimes < 0){
                retryTimes = PROXY_RETRY_TIMES;
            }
            for (int i = 0; i < retryTimes; i++) {
                try{
                    Transport.send(helper.getMimeMessage());
                    exp = null;
                    break;
                }catch (Exception e){
                    log.info("Send times:{}    Exception: {} ", i + 1, e.getMessage());
                    exp = e;
                }
            }
            if (null != exp){
                log.error("当发送邮件时，重试{}次后，仍然失败", retryTimes);
                throw exp;
            }
            log.info("发送邮件成功,to:{}", JSONUtil.toJsonStr(to));
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败,to:{}", JSONUtil.toJsonStr(to), e);
            return false;
        }
    }

    /**
     * 把收件人地址List转换数组类型
     *
     * @param list
     * @return
     * @throws AddressException
     */
    private static InternetAddress[] listToAddrArray(List<String> list) throws AddressException {
        if (list == null) {
            return null;
        }
        List<InternetAddress> addressArray = new ArrayList<>(list.size());
        for (String ip : list) {
            if (ObjectUtil.isNotEmpty(ip)) {
                InternetAddress internetAddress = new InternetAddress(ip.trim());
                addressArray.add(internetAddress);
            }
        }
        InternetAddress[] addresses = new InternetAddress[addressArray.size()];
        return addressArray.toArray(addresses);
    }

}
