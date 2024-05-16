package common.utils.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Properties;

/**
 * EmailConfig
 *
 * @author gaoweicong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EmailProperties {

    private String host;

    private Integer port;

    private String from;

    private String user;

    private String password;

    private Integer retryTimes;

    private Properties properties;
}
