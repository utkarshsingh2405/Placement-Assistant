package com.placementassist.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        exclude = {UserDetailsServiceAutoConfiguration.class},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@ComponentScan(
        basePackages = {
                "com.placementassist.shared",
                "com.placementassist.portal",
                "com.placementassist.institute",
                "com.placementassist.placement"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EntityScan(basePackages = {
        "com.placementassist.shared",
        "com.placementassist.portal",
        "com.placementassist.institute",
        "com.placementassist.placement"
})
@EnableJpaRepositories(
        basePackages = {
                "com.placementassist.shared",
                "com.placementassist.portal",
                "com.placementassist.institute",
                "com.placementassist.placement"
        }
)
@EnableScheduling
@EnableAsync
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }
}
