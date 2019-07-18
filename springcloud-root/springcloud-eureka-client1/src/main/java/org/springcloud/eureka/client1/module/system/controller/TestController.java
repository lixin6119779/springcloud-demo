package org.springcloud.eureka.client1.module.system.controller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




@RequestMapping("/test")
@RestController
@RefreshScope
public class TestController {

	private Logger logger = LoggerFactory.getLogger(TestController.class);

	 

	    private String profile;
	    @RequestMapping("/test1")
	    public String getProperties() {
	    	return "中文:"+profile;
	    }
	    
	    @Value("${eureka.server.port}")
	    private String port;
	    @Value("${spring.application.name}")
	    private String hostname;

	    @RequestMapping("/hello")
	    public String hello(@RequestParam(name = "name") String name) throws UnsupportedEncodingException {
	    	String str = "你好，" + name + "。我是" + hostname + ",端口是" + port;
	    	System.out.println("str==="+str);
	        return str;
	    }
	    
	  
	  
	   
	    @FeignClient(value="eureka-client2")
	    public interface FeginToClient2 {
	    	@RequestMapping("/test/receiveClient1")
	    	String hello(@RequestParam("name")String name);
	    }
	    @Component
	    public class HelloError implements FeginToClient2 {

			@Override
			public String hello(String name) {
				// TODO Auto-generated method stub
				return "hello"+name+"! sorry, error!";
			}
	    }


}
