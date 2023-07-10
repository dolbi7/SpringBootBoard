package com.board;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.assertj.core.error.ShouldBeWritable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class JWTUtilTest {
	
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	String basekey = "boardrestboardrestboardrestboardrestboardrest";

	//토큰 생성
	@Test
	public void generateToken() { //time : 분단위

		// 키 설정
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(basekey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//헤더 부분 설정
		Map<String, Object> headers = new HashMap<String, Object>();
	    headers.put("typ", "JWT");
	    headers.put("alg", "HS256");
	    
	    //payload 설정
	    Map<String, Object> payloads = new HashMap<>();
	    payloads.put("email", "aaa@aaa.com");
	    payloads.put("password", "12345");
	    
	    int days = 1;

	    JwtBuilder builder = Jwts.builder()
	    						.setHeader(headers)
	    						.setClaims(payloads)
	    						.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
	    						.setExpiration(Date.from(ZonedDateTime.now().plusDays(days).toInstant()))
	    						.signWith(signingKey, signatureAlgorithm);

	    String result = builder.compact(); //
	    log.info("JWT = {}", result);
	}
	
	//토큰에서 email, password 추출
		@Test
		public void getDataFromToken() throws Exception{
			
			String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjEyMzQ1IiwiZW1haWwiOiJhYWFAYWFhLmNvbSIsImlhdCI6MTY4ODM1NzM3MSwiZXhwIjoxNjg4NDQzNzcxfQ.CqaD5yuN_q8ayyuZTrdWOBNkmTlCNoj4_IgjB4UWzkk";
			
			Claims claims = Jwts.parserBuilder()
	                .setSigningKey(DatatypeConverter.parseBase64Binary(basekey))
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
			/*
			Map<String, Object> data = new HashMap<>();
			data.put("email", claims.get("email").toString());
			data.put("password", claims.get("password").toString());
			
			return data;
			*/
			log.info("email = {}, password = {}",claims.get("email").toString(), claims.get("password").toString());
		}	
	

}
