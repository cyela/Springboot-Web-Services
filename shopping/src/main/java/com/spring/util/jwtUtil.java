package com.spring.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.model.User;
import com.spring.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class jwtUtil {

	private final static String Key = "axWDVrgnYJil";
	private final static String ISSUER = "ADMIN_SHOPPING";
	private final static String SUBJECT = "USER_SHOPPING";
	private final static String SES_EMAIL = "SESSION_EMAIL";
	private final static String SES_PASS = "SESSION_PASS";
	private final static String SES_TYPE = "SESSION_TYPE";

	@Autowired
	private UserRepository userRepo;

	public String createToken(String session_email, String session_pass, String session_type) {
		Map<String, Object> map = new HashMap<>();
		map.put(SES_EMAIL, session_email);
		map.put(SES_PASS, session_pass);
		map.put(SES_TYPE, session_type);

		SignatureAlgorithm signAlg = SignatureAlgorithm.HS256;
		String br = Jwts.builder().setIssuer(ISSUER).setClaims(map).setSubject(SUBJECT).signWith(signAlg, Key)
				.compact();

		return br;
	}

	public User checkToken(String token) {
		Claims claim = Jwts.parser().setSigningKey(Key).parseClaimsJws(token).getBody();
		User user = userRepo.findByEmailAndPasswordAndUsertype(claim.get(SES_EMAIL).toString(),
				claim.get(SES_PASS).toString(), claim.get(SES_TYPE).toString());
		return user;
	}

}