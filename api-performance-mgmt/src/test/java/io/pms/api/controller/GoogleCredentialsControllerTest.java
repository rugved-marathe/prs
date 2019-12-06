package io.pms.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.pms.api.commontest.Util;
import io.pms.api.controllers.GoogleCredentialsController;
import io.pms.api.services.GoogleCredentialService;
import io.pms.api.vo.UserInfo;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GoogleCredentialsController.class, secure = false)
public class GoogleCredentialsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private AuditingHandler auditingHandler;

	@MockBean
	private GoogleCredentialService googleCredentialService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private String mockLoginResponse() {
		return "Success.7f4a2d9a-75bb-44c8-a69d-c8e971a7a3f5";
	}
	
	private UserInfo userInfo() {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName("john.doe");
		userInfo.setEmailId("john.doe@afourtech.com");
		userInfo.setImageUrl("person.png");
		userInfo.setGroups(Collections.emptyList());
		return userInfo;
	}
	

	@Test
	public void testLoginProcess() {
		Mockito.when(googleCredentialService.createRequestForAccessToken(Mockito.any()))
				.thenReturn(mockLoginResponse());
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/pmslogin")
					.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(userInfo()));
			MvcResult mvcResult;
			mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			String result = mvcResult.getResponse().getContentAsString();
			
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testLogoutProcess() {
		Mockito.when(googleCredentialService.createRequestForLogout(Mockito.any())).thenReturn("Logout Successful.");
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/logout");
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

			String result = mvcResult.getResponse().getContentAsString();
			assertEquals("Logout Successful.", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
