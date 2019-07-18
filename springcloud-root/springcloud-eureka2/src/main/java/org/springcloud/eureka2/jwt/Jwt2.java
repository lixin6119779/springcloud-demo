package org.springcloud.eureka2.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcloud.eureka2.common.utils.DateUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

/**
* 基于jjwt的json web token
* @Author: mjh
* @Date: 2018-03-19 17:35:35
*/
public class Jwt2 {
	
	private static Logger logger = LoggerFactory.getLogger(Jwt2.class);
	
    private static byte[] secret  = "3d990d227dy7shkif96s227697shfdjks".getBytes();

    /**
     * 生成token
     * @param payload
     * @param expiration
     * @return
     */
    public static String sign(Map<String, Object> payload, Date expiration) {
        String token = Jwts.builder()
                .setClaims(payload)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        return token;
    }

    /**
     * 获取token中的信息
     * @param token
     * @return
     */
    public static Claims getClaims(String token){
        Claims claims = null;
        try {
        	claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
        	logger.warn("token无效!");
        } catch (ExpiredJwtException e) {
			logger.warn("token过期!");
		}
        return  claims;
    }
    
    /**
     * 校验token
     * @param token
     * @return
     */
    public static TokenState validateToken(String token) {
    	if(StringUtils.isNotBlank(token)) {
    		Claims claims = null;
    		
    		try {
            	claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();
            } catch (SignatureException e) {
            	logger.warn("token无效!");
            	return TokenState.INVALID;
            } catch (ExpiredJwtException e) {
    			logger.warn("token过期!");
    			return TokenState.EXPIRED;
    		}
    		
    		if(null==claims) {
    			logger.warn("token无效!");
    			return TokenState.INVALID;
    		}
    		
    		long currentTime = System.currentTimeMillis();
    		long expirationTime = claims.getExpiration().getTime();
    		
    		logger.debug("currentTime:"+currentTime);
    		logger.debug("expirationTime:"+expirationTime);
    		
    		if(currentTime>expirationTime) {
    			logger.warn("token过期!");
    			return TokenState.EXPIRED;
    		}else {
    			logger.warn("token有效!");
    			return TokenState.VALID;
    		}
    		
    	}else {
    		logger.warn("token无效!");
    		return TokenState.INVALID;
    	}
    	
    }
    
    
    public static void main(String[] args) {
    	
    	Map<String, Object> payload = new HashMap<String, Object>(16);
    	payload.put("uid", "p007");
		String token = Jwt2.sign(payload, DateUtils.addSeconds(DateUtils.getNowDate(), 3));
		
		System.out.println("token:"+token);
		
		TokenState st = Jwt2.validateToken(token);
		
		System.out.println("TokenState:"+st);
		
		
		String token2 = Jwt2.sign(payload, DateUtils.addSeconds(DateUtils.getNowDate(), 3));
		
		System.out.println("token:"+token2);
		
		st = Jwt2.validateToken(token);
		
		Claims c = Jwt2.getClaims(token);
		
		System.out.println("uid:"+c.get("uid") );
		
		System.out.println("TokenState:"+st);
	}
}
