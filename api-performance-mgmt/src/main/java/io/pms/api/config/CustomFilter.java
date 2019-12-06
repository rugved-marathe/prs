//package io.pms.api.config;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//@Component
//public class CustomFilter extends GenericFilterBean {
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		System.out.println(request.getRemoteAddr());
//		List<String> legalIPs = new ArrayList<>();
//		legalIPs.add("0:0:0:0:0:0:0:1");
//		legalIPs.add("127.0.0.1");
//		legalIPs.add("192.168.10.242");
//		legalIPs.add("192.168.5.30");
//		legalIPs.add("192.168.4.183");
//		
//		if (!legalIPs.contains(request.getRemoteAddr())) {
//			throw new ServletException("forbidden");
//		}
//		chain.doFilter(request, response);
//	}
//}