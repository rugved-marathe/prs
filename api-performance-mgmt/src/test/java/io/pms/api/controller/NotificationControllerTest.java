package io.pms.api.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.pms.api.common.FormStatus;
import io.pms.api.common.Role;
import io.pms.api.commontest.Util;
import io.pms.api.controllers.NotificationController;
import io.pms.api.exception.PMSAppException;
import io.pms.api.services.NotificationService;
import io.pms.api.vo.FormNotificationVO;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = NotificationController.class, secure = false)
public class NotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private AuditingHandler auditingHandler;
	
	@MockBean
	private NotificationService notificationService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private FormNotificationVO mockFormNotificationVo() {
		FormNotificationVO formNotificationVO = new FormNotificationVO();
		formNotificationVO.setRole(Role.EMPLOYEE);
		formNotificationVO.setFormStatus(FormStatus.PENDING);
		formNotificationVO.setEmployeeId("AFT00001");
		return formNotificationVO;
	}

	@Test
	public void testNotification() {
		Mockito.when(notificationService.sendFormNotification(Mockito.any())).thenReturn(null);
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/notification")
					.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(mockFormNotificationVo()));
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			String result = mvcResult.getResponse().getContentAsString();
			
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNotificationThrowsPMSAppException() {
		Mockito.when(notificationService.sendFormNotification(Mockito.any())).thenThrow(PMSAppException.class);
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/notification")
					.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(mockFormNotificationVo()));
			mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
