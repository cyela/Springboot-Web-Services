package com.spring.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class jwtUtil {
	private final static String Key="axWDVrgnYJil";
	private final static String ISSUER="ADMIN_SHOPPING";
	private final static String SUBJECT="USER_SHOPPING";
	
	public  String createToken(String session_email, String session_pass) {
		Map<String, Object> map=new HashMap<>();
		map.put("session_email", session_email);
		map.put("session_pass", session_pass);
		
		SignatureAlgorithm signAlg=SignatureAlgorithm.HS256;
		String br=Jwts.builder()
				.setIssuer(ISSUER)
				.setClaims(map)
				.setSubject(SUBJECT)
				.signWith(signAlg, Key)
				.compact();
		
		return br;
	}
	
	public void checkToken(String token) {
		Claims claim=Jwts.parser().setSigningKey(Key).parseClaimsJws(token).getBody();
		System.out.println("username"+claim.get("session_email"));
	}
	
}
