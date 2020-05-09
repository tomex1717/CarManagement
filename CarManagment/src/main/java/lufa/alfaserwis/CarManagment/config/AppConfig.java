package lufa.alfaserwis.CarManagment.config;

import lufa.alfaserwis.utils.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/resources/static/**").addResourceLocations("/resources/static/");
        registry.addResourceHandler("/imgs/**").addResourceLocations("file:///" + Paths.PHOTO_PATH,"static/images/");


    }
}
