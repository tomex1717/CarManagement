package lufa.alfaserwis.CarManagment.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@PropertySource("classpath:application.properties")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "userEM",
        transactionManagerRef = "userTM",
        basePackages = {
                "lufa.alfaserwis.CarManagment.dao.user"})
public class UserDataSourceConfig {

    @Value("${user.datasource.url}")
    private String userDataSourceURL;
    @Value("${user.datasource.username}")
    private String userDataSourceUsername;
    @Value("${user.datasource.password}")
    private String userDataSourcePassword;


    @Bean(name = "usersDataSource")
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(userDataSourceURL);
        dataSourceBuilder.username(userDataSourceUsername);
        dataSourceBuilder.password(userDataSourcePassword);
        return dataSourceBuilder.build();
    }


    @Bean(name = "userEM")
    public LocalContainerEntityManagerFactoryBean storingEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("usersDataSource") DataSource ds) {
        return builder
                .dataSource(ds)
                .packages("lufa.alfaserwis.CarManagment.entity.user")
                .persistenceUnit("userPU")
                .build();
    }

    @Bean(name = "userTM")
    public PlatformTransactionManager transactionManager(
            @Qualifier("userEM") EntityManagerFactory db2EntityManagerFactory) {
        return new JpaTransactionManager(db2EntityManagerFactory);
    }


}
