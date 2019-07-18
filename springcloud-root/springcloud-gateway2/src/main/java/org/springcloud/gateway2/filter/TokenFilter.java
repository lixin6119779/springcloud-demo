package org.springcloud.gateway2.filter;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcloud.gateway2.jwt.Jwt;
import org.springcloud.gateway2.jwt.TokenState;

import net.minidev.json.JSONObject;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class TokenFilter extends ZuulFilter{
	private Logger logger = LoggerFactory.getLogger(TokenFilter.class);
	/**
	 * 不走过滤，尾部匹配
	 */
	private static final Map<String, String> NOT_FILTER_URLS_ENDS_WITH = new HashMap<String, String>(16);
	
	/**
	 * 不走过滤，包含匹配
	 */
	private static final Map<String, String> NOT_FILTER_URLS_CONTAINS = new HashMap<String, String>(16);
	
	static {
		NOT_FILTER_URLS_ENDS_WITH.put("/", "根路径");
		NOT_FILTER_URLS_ENDS_WITH.put("/login", "登录");
		NOT_FILTER_URLS_ENDS_WITH.put("/register", "注册");
		NOT_FILTER_URLS_ENDS_WITH.put("/comfirmLogin", "");
		NOT_FILTER_URLS_ENDS_WITH.put("/forgetpwd", "忘记密码");
		NOT_FILTER_URLS_ENDS_WITH.put("/initSMSToken", "初始化短信token");
		NOT_FILTER_URLS_ENDS_WITH.put("sendSMS", "发送短信");
		NOT_FILTER_URLS_ENDS_WITH.put(".html", "html页面");
		NOT_FILTER_URLS_ENDS_WITH.put(".js", "js");
		NOT_FILTER_URLS_ENDS_WITH.put(".png", "png");
		NOT_FILTER_URLS_ENDS_WITH.put(".jpg", "jpg");

		NOT_FILTER_URLS_CONTAINS.put("attachment", "附件");
		NOT_FILTER_URLS_CONTAINS.put("swagger", "在线api");
		NOT_FILTER_URLS_CONTAINS.put("api-", "在线api"); 
		NOT_FILTER_URLS_CONTAINS.put("/api/", "外部系统调用的接口");
		NOT_FILTER_URLS_CONTAINS.put("static", "静态资源");
		NOT_FILTER_URLS_CONTAINS.put("upload", "上传");
		NOT_FILTER_URLS_CONTAINS.put("websocket", "与C端通讯");
		NOT_FILTER_URLS_CONTAINS.put("eureka-server", "eureka-server");
		NOT_FILTER_URLS_CONTAINS.put("eureka-client", "eureka-client");
		NOT_FILTER_URLS_CONTAINS.put("eureka-client2", "eureka-client2");
		//NOT_FILTER_URLS_CONTAINS.put("springcloud-eureka-client1", "springcloud-eureka-client1");
		NOT_FILTER_URLS_CONTAINS.put("test", "test");
		NOT_FILTER_URLS_CONTAINS.put("hello", "hello");
		NOT_FILTER_URLS_CONTAINS.put("hystrix", "hystrix");
		NOT_FILTER_URLS_CONTAINS.put("config-client1", "config-client1");
	}
	@Override
	public Object run() throws ZuulException {
		//filter需要执行的具体操作
		RequestContext ctx = RequestContext.getCurrentContext(); 
		HttpServletRequest request  = ctx.getRequest();
		HttpServletResponse response  = ctx.getResponse();
		String uri = request.getRequestURI();
 		//尾部匹配，放行
		for (Map.Entry<String, String> entry : NOT_FILTER_URLS_ENDS_WITH.entrySet()) {
			 String key = entry.getKey();
			 if(uri.endsWith(key)){
				return null;
			 }
		}
 		//包含匹配放行
		for (Map.Entry<String, String> entry : NOT_FILTER_URLS_CONTAINS.entrySet()) {
			 String key = entry.getKey();
			 if(uri.contains(key)){
				return null;
			 }
		}
		String token = request.getHeader("token");
		Map<String, Object> resultMap=Jwt.validToken(token);
		TokenState state=TokenState.getTokenState((String)resultMap.get("state"));
		switch (state) {
			case VALID:
				Map dataMap = (Map)resultMap.get("data");
				//取出payload中数据,放入到request作用域中
				request.setAttribute("token", dataMap);
				request.setAttribute("user_id", dataMap.get("uid"));
				//System.out.println("user_id============="+dataMap);
				//放行			
				break;
			case EXPIRED:
				String newToken = Jwt.createToken(Jwt.getUserId(token),Jwt.getTenantId(token));
				JSONObject outputMsg1=new JSONObject();
				outputMsg1.put("newToken", newToken);
				output(outputMsg1.toJSONString(), response);
	           /* ctx.setSendZuulResponse(false); //不对其进行路由
	            ctx.setResponseStatusCode(400);
	            ctx.setResponseBody("token is empty");
	            ctx.set("isSuccess", false);*/
				break;
	
			case INVALID:
				logger.debug("无效token");
				//token过期或者无效，则输出错误信息返回给ajax
				JSONObject outputMsg2=new JSONObject();
				outputMsg2.put("retCode", -1);
				outputMsg2.put("msg", "您的token不合法或者过期了，请重新登陆");
				output(outputMsg2.toJSONString(), response);
				/*ctx.setSendZuulResponse(false); //不对其进行路由
	            ctx.setResponseStatusCode(400);
	            ctx.setResponseBody("您的token不合法或者过期了，请重新登陆");
	            ctx.set("isSuccess", false);*/
				break;
				
			default:
				logger.debug("无效token");
				//token过期或者无效，则输出错误信息返回给ajax
				JSONObject outputMsg3=new JSONObject();
				outputMsg3.put("retCode", -1);
				outputMsg3.put("msg", "您的token不合法或者过期了，请重新登陆");
				output(outputMsg3.toJSONString(), response);
				/*ctx.setSendZuulResponse(false); //不对其进行路由
	            ctx.setResponseStatusCode(400);
	            ctx.setResponseBody("您的token不合法或者过期了，请重新登陆");
	            ctx.set("isSuccess", false);*/
				break;
		} 
		return null;
	}
	public void output(String jsonStr,HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8;");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    if(out!=null) {
		    	out.flush();
				out.close();
		    }	
		}
	}

	@Override
	public boolean shouldFilter() {
		//表示是否需要执行该filter，true表示执行，false表示不执行
		return true;
	}

	@Override
	public int filterOrder() {
		//定义filter的顺序，数字越小表示顺序越高，越先执行
		return 0;
	}

	@Override
	public String filterType() {
		//定义filter的类型，有pre、route、post、error四种
		return "pre";
	}

}
