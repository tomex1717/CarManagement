package lufa.alfaserwis.CarManagment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


    @Bean(name = "database")
    @Primary
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/car_management?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
        dataSourceBuilder.username("hbstudent");
        dataSourceBuilder.password("hbstudent");
        return dataSourceBuilder.build();
    }

    @Bean(name = "usersDataSource")
    @ConfigurationProperties("user.datasource")
    public DataSource dataSource2(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://users:3306/car_management?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
        dataSourceBuilder.username("hbstudent");
        dataSourceBuilder.password("hbstudent");
        return DataSourceBuilder.create().build();
    }
}
