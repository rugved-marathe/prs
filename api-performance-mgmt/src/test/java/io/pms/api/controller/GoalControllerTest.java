package io.pms.api.controller;

import static io.pms.api.commontest.Util.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.common.CompletionStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.commontest.Util;
import io.pms.api.controllers.GoalController;
import io.pms.api.exception.NotFoundException;
import io.pms.api.repositories.GoalRepository;
import io.pms.api.services.GoalService;
import io.pms.api.vo.GoalListVO;
import io.pms.api.vo.GoalVO;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GoalController.class, secure = false)
public class GoalControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mappingMongoConverter;

	@MockBean
	private GoalService goalService;

	@MockBean
	private GoalRepository goalRepository;

	@MockBean
	private AuditingHandler auditingHandler;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private GoalVO goalVO() {
		GoalVO mockGoalVO = new GoalVO();
		mockGoalVO.setCompletionStatus(CompletionStatus.COMPLETE);
		mockGoalVO.setDescription("goal description");
		mockGoalVO.setEmpId("AFT00000");
		mockGoalVO.setPerformanceCycle(PerformanceCycle.APRIL);
		mockGoalVO.setRating(4);
		mockGoalVO.setReviewerComments("xyz");
		mockGoalVO.setYear(2019);
		return mockGoalVO;
	}

	private GoalListVO goalListVO() {
		List<GoalVO> goalVOs = new ArrayList<GoalVO>();
		goalVOs.add(goalVO());
		GoalListVO goalListVO = new GoalListVO();
		goalListVO.setGoals(goalVOs);
		return goalListVO;
	}

	@Test
	public void testAddGoalController() throws Exception {

		List<GoalVO> goalVOs = new ArrayList<GoalVO>();
		goalVOs.add(goalVO());
		GoalListVO listVO = new GoalListVO();
		listVO.setGoals(goalVOs);

		Mockito.when(goalService.addGoal(goalVO())).thenReturn(goalVO());

		RequestBuilder request = MockMvcRequestBuilders.post("/goal").contentType(MediaType.APPLICATION_JSON)
				.content(Util.asJsonString(listVO));

		mockMvc.perform(request).andExpect(status().isCreated()).andExpect(content().string(notNullValue()));
	}

	@Test
	public void testAddGoalController_WhenInvalidGoalVO() throws Exception {

		List<GoalVO> goalVOs = new ArrayList<GoalVO>();
		goalVOs.add(goalVO());
		GoalListVO listVO = new GoalListVO();
		listVO.setGoals(goalVOs);

		try {
			Mockito.when(goalService.addGoal(goalVO())).thenThrow(NotFoundException.class);

			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/goal").contentType(MediaType.APPLICATION_JSON)
					.content(asJsonString(goalVOs));
			mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetGoalsController() throws Exception {

		Mockito.when(goalService.getGoals(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(goalListVO());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/goal").contentType(MediaType.APPLICATION_JSON)
				.param("empId", "empId").param("performanceCycle", "performanceCycle").param("year", "15");

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();

		JSONObject resultObject = new JSONObject(result.getResponse().getContentAsString());

		assertNotNull(resultObject.getJSONArray("goals").get(0));
		JSONObject employeeObject = (JSONObject) resultObject.getJSONArray("goals").get(0);
		assertNotNull(employeeObject.get("description"));
		assertEquals("goal description", employeeObject.get("description"));

	}

	@Test
	public void testGetGoalsController_WhenInvalidEmpId() throws Exception {

		try {
			Mockito.when(goalService.getGoals(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
					.thenThrow(NotFoundException.class);

			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/goal").contentType(MediaType.APPLICATION_JSON)
					.param("empId", "invalidEmpId").param("performanceCycle", "performanceCycle").param("year", "15");

			mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testEditGoalController() throws Exception {

		Mockito.when(goalService.editGoal(goalVO())).thenReturn(goalVO());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/goal").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(goalVO()));
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();

		assertNotNull(result);
	}

	@Test
	public void testEditGoalController_WhenInvalidGoalId() throws Exception {
		try {
			Mockito.when(goalService.editGoal(goalVO())).thenThrow(NotFoundException.class);

			RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/goal").contentType(MediaType.APPLICATION_JSON)
					.content(asJsonString(goalVO()));
			mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteGoalController() throws Exception {
		Mockito.doNothing().when(goalService).deleteGoals("");
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/goal").contentType(MediaType.APPLICATION_JSON)
				.param("goalId", "goalId");
		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isNoContent()).andReturn();

		assertNotNull(result);
	}

	@Test
	public void testDeleteGoalController_WhenInvalidGoalId() throws Exception {
		Mockito.doThrow(new NotFoundException(null)).when(goalService).deleteGoals(Mockito.anyString());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/goal/").contentType(MediaType.APPLICATION_JSON)
				.param("goalId", "goalId");
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound()).andReturn();

	}

	@Test
	public void testAddPreviousGoalsGoalController() throws Exception {

		String fileName = "test.txt";

		List<String> unprocessedEmpIds = new ArrayList<>();

		Mockito.when(goalService.addPreviousGoals(Mockito.any(MultipartFile.class))).thenReturn(unprocessedEmpIds);

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName, "text/plain",
				"test data".getBytes());

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload("/goal/addPreviousGoals")
				.file(mockMultipartFile);
		this.mockMvc.perform(builder).andExpect(status().isCreated());

	}

	@Test
	public void testAddPreviousGoalsGoalController_whenNotAllEmpAreProcessed() throws Exception {

		String fileName = "test.txt";

		List<String> unProcessedEmpIds = Arrays.asList("AFT00000", "AFT00004");

		Mockito.when(goalService.addPreviousGoals(Mockito.any(MultipartFile.class))).thenReturn(unProcessedEmpIds);

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName, "text/plain",
				"test data".getBytes());

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload("/goal/addPreviousGoals")
				.file(mockMultipartFile);
		this.mockMvc.perform(builder).andExpect(status().isOk()).andExpect(
				jsonPath("$.message", is("Following Employee Ids are not processed: " + unProcessedEmpIds.toString())));
	}
}
