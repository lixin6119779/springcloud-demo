package org.springcloud.gateway2.jwt;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcloud.gateway2.filter.CheckTokenFilter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import net.minidev.json.JSONObject;


/**
* JSON Web Token
* @Author: mjh
* @Date: 2018-03-19 16:00:59
*/
public class Jwt {
	
	private static final Logger logger = LoggerFactory.getLogger(CheckTokenFilter.class);
    
    /**
     * 秘钥
     */
    private static final byte[] SECRET="3d990d2276917dfac04467df11fff333".getBytes();
    
    /**
     * 初始化head部分的数据为
     * {
     * 		"alg":"HS256",
     * 		"type":"JWT"
     * }
     */
    private static final JWSHeader HEADER=new JWSHeader(JWSAlgorithm.HS256, JOSEObjectType.JWT, null, null, null, null, null, null, null, null, null, null, null);
    
    /**
     * 用户id
     */
    private static final String TOKEN_PAYLOAD_KEY_USER_ID = "uid";
    /**
     * 租户id
     */
    private static final String TOKEN_PAYLOAD_KEY_TENANT_ID = "tid";
    /**
     * 创建时间
     */
    private static final String TOKEN_PAYLOAD_KEY_INIT_TIME = "iat";
    /**
     * 过期时间
     */
    private static final String TOKEN_PAYLOAD_KEY_EXPIRES_TIME = "ext";
    
    
	/**
	 * 生成token，该方法只在用户登录成功后调用
	 * 
	 * @param payload，可以存储用户id，token生成时间，token过期时间等自定义字段
	 * @return token字符串,若失败则返回null
	 */
	public static String createToken(Map<String, Object> payload) {
		String tokenString=null;
		// 创建一个 JWS object
		JWSObject jwsObject = new JWSObject(HEADER, new Payload(new JSONObject(payload)));
		try {
			// 将jwsObject 进行HMAC签名
			jwsObject.sign(new MACSigner(SECRET));
			tokenString=jwsObject.serialize();
		} catch (JOSEException e) {
			/*if(logger.isDebugEnabled()) {
				logger.error("签名失败:{}", e.getMessage());
			}*/
		}
		return tokenString;
	}
	
	
	/**
	 * 根据用户id、租户id创建token
	 * @param userId
	 * @param tenantId
	 * @return
	 */
	public static String createToken(String userId, String tenantId) {
		// 生成token
		Map<String, Object> payload = new HashMap<String, Object>(16);
		Date date = new Date();
		// 用户ID
		payload.put(TOKEN_PAYLOAD_KEY_USER_ID, userId);
		// 租户ID
		payload.put(TOKEN_PAYLOAD_KEY_TENANT_ID, tenantId);
		// 生成时间
		payload.put(TOKEN_PAYLOAD_KEY_INIT_TIME, date.getTime());
		// 过期时间24小时
		payload.put(TOKEN_PAYLOAD_KEY_EXPIRES_TIME, date.getTime() + 1000 * 60 * 60 * 24);
		String token = Jwt.createToken(payload);
		return token;
	}
  
    
    /**
     * 校验token是否合法，返回Map集合,集合中主要包含    state状态码   data鉴权成功后从token中提取的数据
     * 该方法在过滤器中调用，每次请求API时都校验
     * @param token
     * @return  Map<String, Object>
     */
	public static Map<String, Object> validToken(String token) {
		Map<String, Object> resultMap = new HashMap<String, Object>(16);
		try {
			JWSObject jwsObject = JWSObject.parse(token);
			Payload payload = jwsObject.getPayload();
			JWSVerifier verifier = new MACVerifier(SECRET);

			if (jwsObject.verify(verifier)) {
				JSONObject jsonOBj = payload.toJSONObject();
				// token校验成功（此时没有校验是否过期）
				resultMap.put("state", TokenState.VALID.toString());
				// 若payload包含ext字段，则校验是否过期
				String extKey = "ext";
				if (jsonOBj.containsKey(extKey)) {
					long extTime = Long.valueOf(jsonOBj.get(TOKEN_PAYLOAD_KEY_EXPIRES_TIME).toString());
					long curTime = System.currentTimeMillis();
					// 过期了
					if (curTime > extTime) {
						resultMap.clear();
						resultMap.put("state", TokenState.EXPIRED.toString());
					}
				}
				resultMap.put("data", jsonOBj);

			} else {
				// 校验失败
				resultMap.put("state", TokenState.INVALID.toString());
			}

		} catch (Exception e) {
			resultMap.clear();
			resultMap.put("state", TokenState.INVALID.toString());
		}
		return resultMap;
	}

	public static String getUserId(String token) {
		String userId = "";
		try {
			JWSObject jwsObject = JWSObject.parse(token);
			Payload payload = jwsObject.getPayload();
			JWSVerifier verifier = new MACVerifier(SECRET);
			if (jwsObject.verify(verifier)) {
				JSONObject jsonOBj = payload.toJSONObject();
				userId = jsonOBj.get(TOKEN_PAYLOAD_KEY_USER_ID).toString();
			}
		} catch (Exception e) {
			userId = "";
		}
		return userId;
	}
	
	public static String getTenantId(String token) {
		String tenantId = "";
		try {
			JWSObject jwsObject = JWSObject.parse(token);
			Payload payload = jwsObject.getPayload();
			JWSVerifier verifier = new MACVerifier(SECRET);
			if (jwsObject.verify(verifier)) {
				JSONObject jsonOBj = payload.toJSONObject();
				tenantId = jsonOBj.get(TOKEN_PAYLOAD_KEY_TENANT_ID).toString();
			}
		} catch (Exception e) {
			tenantId = "";
		}
		return tenantId;
	}

	public static long getExpriedTime(String token) {
		long expriedTime = 0L;
		
		String ext = "ext";
		
		try {
			if(StringUtils.isNotEmpty(token)) {
				JWSObject jwsObject = JWSObject.parse(token);
				Payload payload = jwsObject.getPayload();
				JWSVerifier verifier = new MACVerifier(SECRET);

				if (jwsObject.verify(verifier)) {
					JSONObject jsonOBj = payload.toJSONObject();
					if (jsonOBj.containsKey(ext)) {
						expriedTime = Long.valueOf(jsonOBj.get(TOKEN_PAYLOAD_KEY_EXPIRES_TIME).toString());
					}
				}				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return expriedTime;

	}
	

}


