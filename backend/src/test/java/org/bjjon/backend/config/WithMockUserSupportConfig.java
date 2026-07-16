package org.bjjon.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

@TestConfiguration
public class WithMockUserSupportConfig {

    @Bean
    MockMvcBuilderCustomizer securityMockMvcBuilderCustomizer() {
        return builder -> builder.apply(SecurityMockMvcConfigurers.springSecurity());
    }
}
