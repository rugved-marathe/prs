package io.pms.api.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import io.pms.api.common.Constants;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.vo.UserInfo;

@Service
public class GoogleCredentialService {

	@Autowired
	private AuthorizationServerEndpointsConfiguration config;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private EmployeeRepository employeeRepository;

	public String createRequestForAccessToken(UserInfo userInfo) {
		OAuth2AccessToken token = null;
		String emailId = userInfo.getEmailId();
		HashMap<String, Object> parameters = new HashMap<>();
		Employee employee = employeeRepository.findByEmail(emailId);
		if (employee != null) {
			Map<String, Object> additionalInfo = new HashMap<>();
			additionalInfo.put("roles", employee.getRoles());
			employee.setProfileImage(userInfo.getImageUrl());
			employeeRepository.save(employee);
			parameters.put("email", emailId);
			parameters.put("roles", employee.getRoles());
			token = this.generateAccessToken(parameters);

			((DefaultOAuth2AccessToken) token).setAdditionalInformation(additionalInfo);
			return "Success." + token;
		} else {
			throw new BadCredentialsException(Constants.UNAUTHORIZED_MESSAGE);
		}
	}

	private OAuth2AccessToken generateAccessToken(HashMap<String, Object> parameters) {
		HashMap<String, String> authorizationParameters = new HashMap<String, String>();
		ArrayList<String> scopes = new ArrayList<String>(Arrays.asList("read", "write", "trust"));

		Map<String, Serializable> extensionProperties = new HashMap<String, Serializable>();
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(Constants.ROLE_TRUSTED_CLIENT));

		OAuth2Request oauth2Request = new OAuth2Request(authorizationParameters, Constants.CLIENT_ID, authorities, true,
				new HashSet<String>(scopes), new HashSet<String>(Arrays.asList("oauth2-resource")), null, null,
				extensionProperties);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				parameters.get("email"), null, authorities);

		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oauth2Request, authenticationToken);
		oAuth2Authentication.setAuthenticated(true);

		AuthorizationServerTokenServices tokenService = config.getEndpointsConfigurer().getTokenServices();
		OAuth2AccessToken accessToken = tokenService.createAccessToken(oAuth2Authentication);

		return accessToken;
	}

	public String createRequestForLogout(HttpServletRequest httpRequest) {
		String authHeader = httpRequest.getHeader("Authorization");

		if (authHeader != null) {
			String tokenValue = authHeader.replace("Bearer", "").trim();
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
			tokenStore.removeAccessToken(accessToken);
		} else if (httpRequest.getParameter("access_token") != null) {
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(httpRequest.getParameter("access_token"));
			tokenStore.removeAccessToken(accessToken);
		}

		httpRequest.getSession().invalidate();
		return "Logout Successful.";
	}
}
