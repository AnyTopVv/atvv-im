package com.atvv.im.common.common.gateway.config;

import java.util.Collections;
import java.util.List;

import com.atvv.im.common.common.gateway.handler.GlobalExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

@Configuration
@EnableConfigurationProperties({ServerProperties.class, WebProperties.class})
public class ErrorHandlerConfig{

    private final ServerProperties serverProperties;

    private final ApplicationContext applicationContext;

    private final WebProperties webProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    public ErrorHandlerConfig(ServerProperties serverProperties,
                                     WebProperties resourceProperties,
                                     ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                     ServerCodecConfigurer serverCodecConfigurer,
                                     ApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.webProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorWebExceptionHandler.class,
            search = SearchStrategy.CURRENT)
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(
                errorAttributes,
                this.webProperties.getResources(),
                this.serverProperties.getError(),
                this.applicationContext);
        globalExceptionHandler.setViewResolvers(this.viewResolvers);
        globalExceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        globalExceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return globalExceptionHandler;
    }

}