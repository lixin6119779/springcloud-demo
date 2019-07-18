package org.springcloud.eureka2;

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
@SpringBootApplication(scanBasePackages = {"org.springcloud.eureka2"})
@EnableEurekaServer
//@MapperScan(basePackages = {"com.pactera.saas.module.system.mapper","com.pactera.saas.module.system.exMapper"})
public class SpringCloudEureka2Application extends WebMvcConfigurerAdapter implements CommandLineRunner {
	
	private Logger logger = LoggerFactory.getLogger(SpringCloudEureka2Application.class);
	
	
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEureka2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        logger.info("服务启动完成!");
    }

}