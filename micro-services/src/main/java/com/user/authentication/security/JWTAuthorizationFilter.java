package com.user.authentication.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.user.authentication.constant.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (isHeaderNullOrEmpty(request) ) {
			chain.doFilter(request, response);
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(getAuthentication(request));
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = getRequestedToken(request);
		if (token != null) {
			try {
				String user = getUsernameByToken(token);
				if (user != null) {
					return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
				}
			} catch (Exception e) {
			    e.printStackTrace();
			}
			return null;
		}
		return null;
	}
	public static String getRequestedToken(HttpServletRequest request) {
		return request.getHeader(HEADER_STRING);
	}
	public static String getUsernameByToken(String token) {
		return Jwts.parser().setSigningKey(LOGIN_TOKEN_KEY.getBytes())
		.parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().getSubject();
		
	}
	
	private boolean isHeaderNullOrEmpty(HttpServletRequest request) {
		String header = request.getHeader(HEADER_STRING);
		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			return true;
		}
		return false;
	}
}
