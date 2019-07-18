package org.springcloud.eureka.client1.filter;
import java.io.IOException;



import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
* Cors过滤器
* @Author: mjh
* @Date: 2018-03-19 15:59:34
*/
public class CorsFilter implements Filter {
	 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
 
    @Override
    public void doFilter(ServletRequest argo, ServletResponse arg1, FilterChain filterChain) throws IOException, ServletException {
		argo.setCharacterEncoding("utf-8");            
		arg1.setCharacterEncoding("utf-8");     
        HttpServletResponse httpResponse = (HttpServletResponse) arg1;
        
        String originHeader=((HttpServletRequest) argo).getHeader("Origin");
        httpResponse.addHeader("Access-Control-Allow-Origin", originHeader);
        httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authentication,Origin, X-Requested-With, Content-Type, Accept,Token");  
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");		
 		
        filterChain.doFilter(argo, arg1);
    }
 
    @Override
    public void destroy() {
 
    }
}