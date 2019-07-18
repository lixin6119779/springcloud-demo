package org.springcloud.eureka;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Hello world!
 *
 */
@Controller
@EnableWebMvc
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.springcloud.eureka"})
@EnableEurekaServer
//@MapperScan(basePackages = {"com.pactera.saas.module.system.mapper","com.pactera.saas.module.system.exMapper"})
public class SpringCloudEurekaApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {
	
	private Logger logger = LoggerFactory.getLogger(SpringCloudEurekaApplication.class);
	
	
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEurekaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        logger.info("服务启动完成!");
    }

}