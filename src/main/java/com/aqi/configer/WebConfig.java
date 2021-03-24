package com.aqi.configer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 扩展SpringMVC
 * SpringBoot2使用的Spring5，因此将WebMvcConfigurerAdapter改为WebMvcConfigurer
 * 使用WebMvcConfigurer扩展SpringMVC好处既保留了SpringBoot的自动配置，又能用到我们自己的配置
 */
//@EnableWebMvc //如果我们需要全面接管SpringBoot中的SpringMVC配置则开启此注解，
//开启后，SpringMVC的自动配置将会失效。
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public IPInterceptor ipInterceptor() {
        return new IPInterceptor();
    }
    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipInterceptor()).addPathPatterns("/**");
    }
}
