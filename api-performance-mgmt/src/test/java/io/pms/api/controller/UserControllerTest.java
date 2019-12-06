package io.pms.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
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

import io.pms.api.common.Role;
import io.pms.api.controllers.UserController;
import io.pms.api.services.UserService;
import io.pms.api.vo.EmployeeVO;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class, secure = false)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private AuditingHandler auditingHandler;

	@MockBean
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private EmployeeVO employeeVO() {
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setEmpId("AFT00000");
		employeeVO.setEmpName("John Doe");
		employeeVO.setEmail("john.doe@afourtech.com");
		employeeVO.setProfileImage("person.png");
		Set<Role> roles = new HashSet<>();
		roles.add(Role.EMPLOYEE);
		roles.add(Role.MANAGER);
		employeeVO.setRoles(roles);
		return employeeVO;
	}

	@Test
	public void testGetEmployeeProfile() {
		Mockito.when(userService.getUserProfile(Mockito.any())).thenReturn(employeeVO());
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/getUserProfile")
					.contentType(MediaType.APPLICATION_JSON);
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());

			assertNotNull(resultObject);
			assertEquals("AFT00000", resultObject.get("empId"));
			assertEquals("John Doe", resultObject.get("empName"));
			assertEquals("john.doe@afourtech.com", resultObject.get("email"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}