/**
 * 
 */
package io.pms.api.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.commontest.Util;
import io.pms.api.controllers.ReviewFormController;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.ValidationException;
import io.pms.api.fixture.ReviewFormFixture;
import io.pms.api.services.ReviewFormService;
import io.pms.api.vo.GenericResponse;
import io.pms.api.vo.ReviewFormListVO;
import io.pms.api.vo.ReviewFormVO;

/**
 * @author rugved.m
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ReviewFormController.class, secure = false)
public class ReviewFormControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private ReviewFormService reviewFormService;

	@MockBean
	private AuthorizationServerTokenServices tokenServices;

	@MockBean
	private AuditingHandler auditingHandler;

	private GenericResponse response;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private ReviewFormVO mockReviewForm() {
		ReviewFormVO reviewForm = new ReviewFormVO();
		reviewForm.setId("5c012f6722a8d1159cec114d");
		reviewForm.setEmpId("AFT00001");
		reviewForm.setCycle(PerformanceCycle.APRIL);
		reviewForm.setAccomplishments(Collections.emptyList());
		reviewForm.setTrainingCertification(Collections.emptyList());
		reviewForm.setBusinessPhilosophies(Collections.emptyList());
		reviewForm.setCommonCompetencies(Collections.emptyList());
		reviewForm.setRoleResponsibilities(Collections.emptyList());
		reviewForm.setFormStatus(FormStatus.PENDING);
		reviewForm.setRating(3.4);
		reviewForm.setYear(2019);
		return reviewForm;
	}

	/**
	 * 
	 * Test method for
	 * {@link io.pms.api.controllers.ReviewFormController#swapReviewCycle(java.util.List)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSwapReviewCycle() throws Exception {
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add("AFT000001");
		employeeIds.add("AFT000002");

		response = new GenericResponse();
		response.setMessage("Response");

		Mockito.when(reviewFormService.swapReviewCycle(Mockito.any())).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/review/swapCycle")
				.contentType(MediaType.APPLICATION_JSON).content(Util.asJsonString(employeeIds));
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
		assertNotNull(resultObject);

		assertEquals("Response", resultObject.get("message"));
	}

	@Test
	public void testCreateReviewForm() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, false));

		Mockito.when(reviewFormService.createReviewForm(any()))
				.thenReturn(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, true));
		RequestBuilder request = MockMvcRequestBuilders.post("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isCreated()).andExpect(content().string(notNullValue()));

	}

	@Test
	public void testCreateReviewFormSend() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.validReviewForm(FormStatus.PENDING, false));

		Mockito.when(reviewFormService.createReviewForm(any()))
				.thenReturn(ReviewFormFixture.validReviewForm(FormStatus.PENDING, true));
		RequestBuilder request = MockMvcRequestBuilders.post("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isCreated()).andExpect(content().string(notNullValue()));

	}

	@Test
	public void testCreateReviewFormInvalid() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.invalidReviewForm());

		Mockito.when(reviewFormService.createReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.post("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}
	@Test
	public void testCreateReviewFormNoEmpId() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.reviewFormNoEmpId());

		Mockito.when(reviewFormService.createReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.post("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testCreateReviewFormYearGreaterThanCurrent() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.reviewFormYearGreaterThanCurrent());

		Mockito.when(reviewFormService.createReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.post("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testCreateReviewFormYearLessThanCurrent() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.reviewFormYearLessThanCurrent());

		Mockito.when(reviewFormService.createReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.post("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateReviewForm() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, false));

		Mockito.when(reviewFormService.updateReviewForm(any()))
				.thenReturn(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, true));
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isOk()).andExpect(content().string(notNullValue()));

	}

	@Test
	public void testUpdateReviewFormSend() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, false));

		Mockito.when(reviewFormService.updateReviewForm(any()))
				.thenReturn(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, true));
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isOk()).andExpect(content().string(notNullValue()));

	}

	@Test
	public void testUpdateReviewFormInvalid() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.invalidReviewForm());

		Mockito.when(reviewFormService.updateReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateReviewFormNoEmpId() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.reviewFormNoEmpId());

		Mockito.when(reviewFormService.updateReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateReviewFormYearGreaterThanCurrent() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.reviewFormYearGreaterThanCurrent());

		Mockito.when(reviewFormService.updateReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateReviewFormYearLessThanCurrent() throws IOException, Exception {

		String requestBody = Util.asJsonString(ReviewFormFixture.reviewFormYearLessThanCurrent());

		Mockito.when(reviewFormService.updateReviewForm(any())).thenThrow(ValidationException.class);
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateReviewFormNoInput() throws IOException, Exception {

		String requestBody = "";

		Mockito.when(reviewFormService.updateReviewForm(any())).thenThrow(NotFoundException.class);
		RequestBuilder request = MockMvcRequestBuilders.put("/review").contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody);

		mockMvc.perform(request).andExpect(status().isBadRequest());

	}

	@Test
	public void testGetReviewForm_whenNoParameters() {
		String employeeId = "AFT00001";
		List<ReviewFormVO> formListVO = Arrays.asList(mockReviewForm());
		ReviewFormListVO listVO = new ReviewFormListVO();
		listVO.setReviewForms(formListVO);

		Mockito.when(reviewFormService.getReviewForm(employeeId, null, null, null)).thenReturn(listVO);
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/review")
					.contentType(MediaType.APPLICATION_JSON).param("employeeId", "AFT00001").param("role", "EMPLOYEE");
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			assertNotNull(resultObject);
			assertNotNull(resultObject.get("reviewForms"));

			JSONObject actualReviewFormObject = (JSONObject) resultObject.getJSONArray("reviewForms").get(0);
			assertEquals("AFT00001", actualReviewFormObject.get("empId"));
			assertEquals("APRIL", actualReviewFormObject.get("performanceCycle"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void whenGetReviewForm_thenNoResult() {
		String employeeId = "AFT00001";
		List<ReviewFormVO> formListVO = Arrays.asList(mockReviewForm());
		ReviewFormListVO listVO = new ReviewFormListVO();
		listVO.setReviewForms(formListVO);

		Mockito.when(reviewFormService.getReviewForm(employeeId, null, null, null)).thenReturn(listVO);
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/review")
					.contentType(MediaType.APPLICATION_JSON);
			mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void dispatchReviewForm_whenEmpListIsNotNullOrEmpty() throws Exception {

		ArrayList<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00002");
		empIdList.add("AFT00003");

		String requestBody = Util.asJsonString(empIdList);

		Mockito.doNothing().when(reviewFormService).dispatchReviewForms(Mockito.any(), Mockito.anyInt(), Mockito.any());

		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/review/dispatchReviewForms")
					.contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
					.param("cycle", PerformanceCycle.APRIL.toString()).param("year", "2019");

			mockMvc.perform(requestBuilder).andExpect(status().isAccepted()).andReturn();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void dispatchReviewForm_whenEmpListIsEmpty() throws Exception {

		ArrayList<String> empIdList = new ArrayList<String>();

		String requestBody = Util.asJsonString(empIdList);

		Mockito.doNothing().when(reviewFormService).dispatchReviewForms(Mockito.any(), Mockito.anyInt(), Mockito.any());

		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/review/dispatchReviewForms")
					.contentType(MediaType.APPLICATION_JSON_VALUE).param("cycle", PerformanceCycle.APRIL.toString())
					.param("year", "2019").content(requestBody);

			mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void dispatchReviewForm_whenEmpListIsNull() throws Exception {

		List<String> empIdList = null;

		String requestBody = Util.asJsonString(empIdList);

		Mockito.doNothing().when(reviewFormService).dispatchReviewForms(Mockito.any(), Mockito.anyInt(), Mockito.any());

		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/review/dispatchReviewForms")
					.contentType(MediaType.APPLICATION_JSON_VALUE).param("cycle", PerformanceCycle.APRIL.toString())
					.param("year", "2019").content(requestBody);

			mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetReviewForm() {
		
		String formId = "5c012f6722a8d1159cec114d";
		
		Mockito.when(reviewFormService.getReviewFormById(Mockito.anyString())).thenReturn(mockReviewForm());
		
		try {
			RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/review/5c012f6722a8d1159cec114d")
					.contentType(MediaType.APPLICATION_JSON);
			MvcResult mvcResult = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
			JSONObject resultObject = new JSONObject(mvcResult.getResponse().getContentAsString());
			
			assertNotNull(resultObject);
			assertEquals(formId, resultObject.get("id"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
