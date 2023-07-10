package com.board;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.board.service.MemberUserDetailsServiceImpl;

import static org.springframework.security.config.Customizer.withDefaults;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

	private final AuthSuccessHandler authSuccessHandler; //의존성 주입
	private final AuthFailureHandler authFailureHandler;
	private final MemberUserDetailsServiceImpl userDetailsService;
	private final OAuth2SucessHandler oauth2SucessHandler;
    private final OAuth2FailureHandler oauth2FailureHandler;
	
	//스프링시큐리티 암호화 스프링빈 등록 
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//스프링시큐리티 적용 제외 대상 설정 스프링빈 등록
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		
		return (web)->web.ignoring().requestMatchers("/images/**","/css/**","/profile/**");
	}

	//로그인 처리
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .formLogin(login -> login
                    .usernameParameter("email")   
                    .loginPage("/member/login")
                    .successHandler(authSuccessHandler)  
                    .failureHandler(authFailureHandler));
        http
        	.oauth2Login(login -> login
                .loginPage("/member/login") 
                .successHandler(oauth2SucessHandler)
                .failureHandler(oauth2FailureHandler));        
        http
            .rememberMe(me -> me
                    .key("xavier")
                    .alwaysRemember(false) 
                    .tokenValiditySeconds(3600 * 24 * 7)
                    .rememberMeParameter("remember-me")
                    .userDetailsService(userDetailsService)
                    .authenticationSuccessHandler(authSuccessHandler));
		
		//접근권한 설정(권한 부여 : Authorization)
		http
			.authorizeHttpRequests()
			.requestMatchers("/member/**").permitAll()
			.requestMatchers("/restapi/**").permitAll()
			.requestMatchers("/board/**").hasAnyAuthority("USER","MASTER")
			.requestMatchers("/master/**").hasAnyAuthority("MASTER")
			.anyRequest().authenticated();
		
		//세션 설정
        http
            .sessionManagement(management -> management
                    .maximumSessions(1) 
                    .maxSessionsPreventsLogin(false) 
                    .expiredUrl("/member/login"));
        
        //로그 아웃
        http
            .logout(logout -> logout
                    .logoutUrl("/member/logout") 
                    .logoutSuccessUrl("/member/login") 
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll());
        
        //CSRF, CORS 공격 보안 비활성화
        http
           .csrf((csrf) -> csrf.disable())
           .cors((cors) -> cors.disable());
		
		System.out.println("************* 스프링 시큐리티 설정 완료 !!! *************");
		
		return http.build();
	}
}


/*
//로그인 처리(인증 : Authentication)
http
	.formLogin()
	.usernameParameter("userid")   //아이디 변수명
	.loginPage("/user/login") //사용자가 지정한 로그인 화면 보여주는 URL
	.successHandler(authSuccessHandler) //성공시 요청을 처리할 핸들러 
	.failureHandler(authFailureHandler); //실패시 요청을 처리할 핸들러

//Remember-me 기능 활성화
http
	.rememberMe()
	.key("tkdd")
	.alwaysRemember(false) //항상 기억할 것인지의 여부를 결정
	.tokenValiditySeconds(3600*24*7) 
	.rememberMeParameter("remember-me")
	.userDetailsService(userDetailsService)
	.authenticationSuccessHandler(authSuccessHandler);

//접근권한 설정(권한 부여 : Authorization)
http
	.authorizeHttpRequests()
	.requestMatchers("/user/**").permitAll()
	.requestMatchers("/board/**").hasAnyAuthority("USER","MASTER")
	.requestMatchers("/master/**").hasAnyAuthority("MASTER")
	.anyRequest().authenticated();

//세션 관리
http
	.sessionManagement()
	.maximumSessions(1) //-1이면 무제한 세션 허용
	.maxSessionsPreventsLogin(false) //true : 중복로그인 방지, false: 중복 로그인 시 이전 로그인 세션 삭제
	.expiredUrl("/user/login"); //세션이 만료된 경우 이동할 URL

//로그아웃 처리
http
	.logout()
	.logoutUrl("/board/logout") //스프링 시큐리티의 로그아웃을 가동시키는 사용자 지정 로그아웃 URL
	.logoutSuccessUrl("/user/login") //로그아웃이 성공했을때 리다이렉트하는 URL
	.invalidateHttpSession(true)
	.deleteCookies("JSESSIONID","remember-me") //JSESSIONID, remember-me 쿠키 삭제
	.permitAll();
	
	//스프링시큐리티 적용 제외 대상 설정 스프링빈 등록
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		
		return (web)->web.ignoring().requestMatchers("/images/**","/css/**","/profile/**");
	}

*/	
//CSRF, CORS 공격 방어 비활성화 
