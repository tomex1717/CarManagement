package lufa.alfaserwis.CarManagment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {

    private YAMLConfig config;

    @Autowired
    public AppConfig(YAMLConfig config) {
        this.config = config;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/resources/static/**").addResourceLocations("/resources/static/");
        registry.addResourceHandler("/imgs/**").addResourceLocations("file:///" + config.getPhotosPath(), "/static/images/");
        registry.addResourceHandler("/invoices/**").addResourceLocations("file:///" + config.getInvoicesPath());


    }
}
