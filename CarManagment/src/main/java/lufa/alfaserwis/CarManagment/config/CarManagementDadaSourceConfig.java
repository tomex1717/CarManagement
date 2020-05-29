package lufa.alfaserwis.CarManagment.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "carManagementEM",
        transactionManagerRef = "carManagementTM",
        basePackages = {
        "lufa.alfaserwis.CarManagment.dao.carmanagement" })
public class CarManagementDadaSourceConfig {




    @Bean(name = "carManagementDataSource")
    @Primary
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://127.0.0.1:3306/car_management?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
        dataSourceBuilder.username("hbstudent");
        dataSourceBuilder.password("hbstudent");
        return dataSourceBuilder.build();
    }

    @Primary
    @Bean(name = "carManagementEM")
    public LocalContainerEntityManagerFactoryBean storingEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("carManagementDataSource") DataSource ds) {
        return builder
                .dataSource(ds)
                .packages("lufa.alfaserwis.CarManagment.entity.carmanagement")
                .persistenceUnit("CarManagementPU")
                .build();
    }
    @Primary
    @Bean(name = "carManagementTM")
    public PlatformTransactionManager transactionManager(
            @Qualifier("carManagementEM") EntityManagerFactory db1EntityManagerFactory) {
        return new JpaTransactionManager(db1EntityManagerFactory);
    }


}
