package io.pms.api.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.services.GoogleCredentialService;
import io.pms.api.vo.UserInfo;

@RunWith(SpringRunner.class)
public class GoogleCredentialServiceTest {

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private AuditingHandler auditingHandler;

	@InjectMocks
	private GoogleCredentialService googleCredentialService;

	@MockBean
	private TokenStore tokenStore;

	@MockBean
	private OAuth2Request oAuth2Request;

	@MockBean
	private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

	@MockBean
	private OAuth2Authentication oAuth2Authentication;

	@MockBean
	private EmployeeRepository employeeRepository;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private Employee mockEmployee() {
		Employee employee = new Employee();
		employee.setEmpId("AFT00003");
		employee.setEmpName("John Doe");
		employee.setCurrentMgrId("AFT00002");
		employee.setCurrentDUHeadId("AFT00001");
		employee.setDateOfJoining(new DateTime().withDate(2024, 1, 1));
		employee.setStatus(Status.ACTIVE);
		employee.setPerformanceCycle(PerformanceCycle.APRIL);
		employee.setCurrentProject(Arrays.asList("PMS"));
		Set<Role> roles = new HashSet<Role>();
		roles.add(Role.EMPLOYEE);
		employee.setRoles(roles);
		return employee;
	}

	private UserInfo mockUserInfo() {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName("john.doe");
		userInfo.setEmailId("john.doe@afourtech.com");
		userInfo.setImageUrl("/person.png");
		userInfo.setGroups(Arrays.asList("prs@afpurtech.com"));
		return userInfo;
	}

	@Test
	public void whenCreateRequestForLogoutIsTriggered_thenReturnLogoutSuccess() {
		OAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
		Mockito.when(tokenStore.readAccessToken(Mockito.anyString())).thenReturn(token);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer " + token.getValue());

		googleCredentialService.createRequestForLogout(request);
	}

	@Test
	public void whenCreateRequestForLogoutWithNullHeaderIsTriggered_thenReturnLogoutSuccess() {
		OAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
		Mockito.when(tokenStore.readAccessToken(Mockito.anyString())).thenReturn(token);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("access_token", token.toString());

		googleCredentialService.createRequestForLogout(request);
	}

}
