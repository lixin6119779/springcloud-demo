package org.springcloud.eureka.client1;


//import org.mybatis.spring.annotation.MapperScan;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * 
* 应用启动入口
* @Author: mjh
* @Date: 2018-03-19 12:19:21
 */  

@Controller
@EnableWebMvc
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.springcloud.eureka.client1"})
@EnableEurekaClient
//@EnableDiscoveryClient
@EnableFeignClients
//@MapperScan(basePackages = {"com.pactera.saas.module.system.mapper","com.pactera.saas.module.system.exMapper"})
public class EurekaClientApplication extends WebMvcConfigurerAdapter implements CommandLineRunner {
	
	private Logger logger = LoggerFactory.getLogger(EurekaClientApplication.class);
	
    public static void main(String[] args) {
       System.setProperty("svnkit.http.methods", "Basic,Digest,NTLM"); 
        SpringApplication.run(EurekaClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("服务启动完成!");
    }

}

