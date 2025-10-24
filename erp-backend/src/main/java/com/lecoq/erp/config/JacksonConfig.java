package com.lecoq.erp.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Module hibernateModule() {
        Hibernate5JakartaModule m = new Hibernate5JakartaModule();
        m.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);

        return m;
    }
}
