package org.springcloud.eureka.conf;

import javax.servlet.Filter;


import org.springcloud.eureka.filter.CheckTokenFilter;
import org.springcloud.eureka.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;



/**
* 过滤器配置
* @Author: mjh
* @Date: 2018-03-19 15:57:31
*/

//@Configuration
public class FilterConfiguration {
    @Bean
    public FilterRegistrationBean corsFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public Filter corsFilter() {
        return new CorsFilter();
    }
    
    @Bean
    public FilterRegistrationBean authFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(authFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    } 

    @Bean
    public Filter authFilter() {
        return new CheckTokenFilter();
    }



}