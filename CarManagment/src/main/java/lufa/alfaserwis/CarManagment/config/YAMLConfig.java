package lufa.alfaserwis.CarManagment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Getter
@Setter
public class YAMLConfig {

    private String name;
    private String environment;

    private String userDataSourceUrl;
    private String userDataSourceUsername;
    private String userDataSourcePassword;

    private String carDataSourceUrl;
    private String carDataSourceUsername;
    private String carDataSourcePassword;

    private String googleMapsApiKey;

    private String loggingFileName;
}
