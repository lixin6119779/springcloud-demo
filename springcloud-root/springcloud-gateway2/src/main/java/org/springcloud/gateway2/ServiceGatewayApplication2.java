package org.springcloud.gateway2;



import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springcloud.gateway2.filter.CorsFilter;
import org.springcloud.gateway2.filter.TokenFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;



/**
 * 
* 应用启动入口
* @Author: mjh
* @Date: 2018-03-19 12:19:21
 */  

@Controller
@EnableWebMvc
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.springcloud.gateway"})
@EnableZuulProxy
//@MapperScan(basePackages = {"com.pactera.saas.module.system.mapper","com.pactera.saas.module.system.exMapper"})
public class ServiceGatewayApplication2  implements CommandLineRunner {
	
	private Logger logger = LoggerFactory.getLogger(ServiceGatewayApplication2.class);
	
	@Bean
	public TokenFilter tokenFilter() {
		return new TokenFilter();
	}
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter();
	}
    public static void main(String[] args) {
       System.setProperty("svnkit.http.methods", "Basic,Digest,NTLM"); 
        SpringApplication.run(ServiceGatewayApplication2.class, args);
    }

    public void run(String... args) throws Exception {

    	//项目启动时执行一次同步任务;
    	/*List<ExUserInfo> userInfos = synchroDataService.askForUserList();
    	if(userInfos!=null && userInfos.size()>0) {
    		Integer n = synchroDataService.SynchroSysUserInfo(userInfos);
    	   logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"共"+n+"条记录受到影响");
    	}else {
    		logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" 没有获取到用户信息");
    	}
    	scheduleConfigService.setCron1(requestConfig.getUserSynchronizationTime());
    	//每隔一秒执行一次  0/1 * * * * ?
    	scheduleConfigService.setCron2("0 3 13 * * ?"); */
        logger.info("服务启动完成!");
    }

}

/*
@Controller
@EnableWebMvc
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.pactera.saas"})
@MapperScan(basePackages = {"com.pactera.saas.module.system.mapper","com.pactera.saas.module.system.exMapper"})
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    ScheduleConfigService scheduleConfigService;
    @Autowired
    SynchroDataService synchroDataService;
    @Autowired
    ExRequestConfig requestConfig;

    private Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        System.setProperty("svnkit.http.methods", "Basic,Digest,NTLM"); 
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
      //项目启动时执行一次同步任务;
        List<ExUserInfo> userInfos = synchroDataService.askForUserList();
        if(userInfos!=null && userInfos.size()>0) {
            Integer n = synchroDataService.SynchroSysUserInfo(userInfos);
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"共"+n+"条记录受到影响");
        }else {
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" 没有获取到用户信息");
        }
        scheduleConfigService.setCron(requestConfig.getUserSynchronizationTime());
        logger.info("服务启动完成!");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
*/