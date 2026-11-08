package com.placementassist.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PortalWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/institute", HandlerTypePredicate.forBasePackage("com.placementassist.institute"));
        configurer.addPathPrefix("/placement", HandlerTypePredicate.forBasePackage("com.placementassist.placement"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Forward /pages/** to Institute's static pages folder (preserving old Gateway behavior)
        registry.addResourceHandler("/pages/**")
                .addResourceLocations("classpath:/static/institute/pages/");

        // Portal static root resources (index, login, register, css, js, icons, textures, etc.)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
