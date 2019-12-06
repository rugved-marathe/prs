package io.pms.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.test.context.junit4.SpringRunner;

import io.pms.api.common.CompletionStatus;
import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Status;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.fixture.ReviewFormFixture;
import io.pms.api.model.CommonTag;
import io.pms.api.model.Employee;
import io.pms.api.model.Goal;
import io.pms.api.model.ReviewForm;
import io.pms.api.model.RoleTag;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.repositories.GoalRepository;
import io.pms.api.repositories.ReviewFormRepository;
import io.pms.api.repositories.RoleTagRepository;
import io.pms.api.services.NotificationService;
import io.pms.api.services.ReviewFormService;
import io.pms.api.vo.EmailResponse;
import io.pms.api.vo.GenericResponse;
import io.pms.api.vo.ReviewFormListVO;
import io.pms.api.vo.ReviewFormVO;

/**
 * @author pavas.s
 *
 */
@RunWith(SpringRunner.class)
public class ReviewFormServiceTest {
	@MockBean
	private AuditingHandler auditingHandler;

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private ReviewFormRepository reviewFormRepository;

	@MockBean
	private EmployeeRepository employeeRepository;

	@MockBean
	private RoleTagRepository roleTagRepository;

	@MockBean
	private GoalRepository goalRepository;

	@MockBean
	private NotificationService notificationService;

	@InjectMocks
	private ReviewFormService reviewFormService;

	private GenericResponse response;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private ReviewForm mockReviewForm() {
		ReviewForm reviewForm = new ReviewForm();
		reviewForm.setId("5c012f6722a8d1159cec114d");
		reviewForm.setEmpId("AFT00001");
		reviewForm.setCycle(PerformanceCycle.APRIL);
		reviewForm.setAccomplishments(Collections.emptyList());
		reviewForm.setTrainingCertification(Collections.emptyList());
		reviewForm.setBusinessPhilosophies(Collections.emptyList());
		reviewForm.setCommonCompetencies(Collections.emptyList());
		reviewForm.setRoleResponsibilities(Collections.emptyList());
		reviewForm.setFormStatus(FormStatus.PENDING);
		reviewForm.setFinalDiscussionDate(new DateTime());
		reviewForm.setRating((double) 3.4);
		reviewForm.setYear(2019);
		return reviewForm;
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
		employee.setDateOfLeaving(new DateTime().withDate(2024, 1, 1));
		return employee;

	}

	private Goal mockGoal() {
		Goal mockGoal = new Goal();
		mockGoal.setCompletionStatus(CompletionStatus.COMPLETE);
		mockGoal.setDescription("goal description");
		mockGoal.setEmpId("AFT00000");
		mockGoal.setPerformanceCycle(PerformanceCycle.APRIL);
		mockGoal.setRating(4);
		mockGoal.setReviewerComments("xyz");
		mockGoal.setStatus(Status.ACTIVE);
		mockGoal.setYear(2019);
		return mockGoal;
	}

	private List<Goal> mockGoalsList() {
		List<Goal> goals = new ArrayList<Goal>();
		Goal tempGoal = mockGoal();

		goals.add(mockGoal());
		goals.add(tempGoal);

		return goals;
	}

	private RoleTag roleTag() {
		List<CommonTag> mockRoleResponsibilities = new ArrayList<>();
		List<CommonTag> mockCommonCompetencies = new ArrayList<>();
		List<CommonTag> mockBusinessPhilosophies = new ArrayList<>();

		RoleTag tagVO = new RoleTag();
		tagVO.setId("5bfe685730642420c4e1da76");
		tagVO.setTagName("Mock Role Tag");
		tagVO.setDescription("");
		tagVO.setRoleResponsibilities(mockRoleResponsibilities);
		tagVO.setCommonCompetencies(mockCommonCompetencies);
		tagVO.setBusinessPhilosophies(mockBusinessPhilosophies);
		tagVO.setModifiedDate(DateTime.parse("2018-11-30T00:00:00"));
		tagVO.setModifiedBy("john.doe@afourtech.com");
		tagVO.setStatus(Status.ACTIVE);
		return tagVO;
	}

	private EmailResponse emailResponse() {
		EmailResponse response = new EmailResponse();
		response.setMsg("Test Messsage");
		response.setSuccess("Test Success");

		return response;
	}

	@Test
	public void whenSwapReviewCycleIsApril_thenSwappedToOctoberCycle() {
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add("AFT000001");
		employeeIds.add("AFT000002");
		Employee employee = mockEmployee();
		employee.setPerformanceCycle(PerformanceCycle.OCTOBER);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

		response = reviewFormService.swapReviewCycle(employeeIds);

		assertNotNull(response);
		assertEquals("Performance Cycle changed for employees", response.getMessage());
	}

	@Test
	public void whenSwapReviewCycleIsOctober_thenSwappedToAprilCycle() {
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add("AFT000001");
		employeeIds.add("AFT000002");
		Employee employee = mockEmployee();
		employee.setPerformanceCycle(PerformanceCycle.OCTOBER);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee);
		Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(mockEmployee());

		response = reviewFormService.swapReviewCycle(employeeIds);

		assertNotNull(response);
		assertEquals("Performance Cycle changed for employees", response.getMessage());
	}

	@Test
	public void whenSwapReviewCycle_thenEmployeeMayNotBeFound() {
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add("AFT000001");
		employeeIds.add("AFT000002");

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);

		response = reviewFormService.swapReviewCycle(employeeIds);

		assertNotNull(response);
		assertEquals("Performance Cycle changed for employees", response.getMessage());
	}

	@Test(expected = PMSAppException.class)
	public void whenSwapReviewCycle_thenPMSAppException() throws PMSAppException {
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add("AFT000001");
		employeeIds.add("AFT000002");

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenThrow(new RuntimeException());

		response = reviewFormService.swapReviewCycle(employeeIds);

	}

	@Test(expected = PMSAppException.class)
	public void whenSwapReviewCycleIsTriggered_thenThrowPMSAppException() {
		reviewFormService.swapReviewCycle(null);
	}

	@Test(expected = PMSAppException.class)
	public void testSwapReviewCycle_whenExceptionIsThrown() {
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add("AFT000001");
		employeeIds.add("AFT000002");

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenThrow(PMSAppException.class);

		reviewFormService.swapReviewCycle(employeeIds);
	}
	
	@Test
	public void testUpdateReviewFormSavedForLater() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true);
		Mockito.when(reviewFormRepository.findOne(anyString()))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		ReviewFormVO updatedReviewForm = reviewFormService
				.updateReviewForm(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, true));
		assertNotNull(updatedReviewForm);
		assertNotNull(updatedReviewForm.getId());

	}

	@Test(expected = ValidationException.class)
	public void testUpdateReviewNoEmpId() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		reviewFormService.updateReviewForm(ReviewFormFixture.reviewFormNoEmpId());

	}

	@Test(expected = ValidationException.class)
	public void testUpdateReviewInvalidForm() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		reviewFormService.updateReviewForm(ReviewFormFixture.invalidReviewForm());

	}

	@Test(expected = ValidationException.class)
	public void testUpdateReviewYearLessThanCurrent() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.PENDING, true));
		reviewFormService.updateReviewForm(ReviewFormFixture.reviewFormYearLessThanCurrent());

	}

	@Test(expected = ValidationException.class)
	public void testUpdateReviewYearGreaterThanCurrent() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.PENDING, true));
		reviewFormService.updateReviewForm(ReviewFormFixture.reviewFormYearGreaterThanCurrent());

	}

	@Test(expected = NotFoundException.class)
	public void testUpdateReviewFormNotFound() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(null);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		reviewFormService.updateReviewForm(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, true));

	}

	@Test(expected = ValidationException.class)
	public void testUpdateReviewEmptyForm() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(null);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		 reviewFormService.updateReviewForm(null);

	}

	@Test
	public void testUpdateReviewForm_whenFormStatusIsRecieved() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.FORM_RECEIVED, true);

		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(reviewForm);
		Mockito.when(goalRepository.findAllByEmpIdAndPerformanceCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(mockGoalsList());

		Mockito.when(reviewFormRepository.save(reviewForm)).thenReturn(reviewForm);

		ReviewFormVO reviewFormVO = ReviewFormFixture.validReviewForm(FormStatus.FORM_RECEIVED, true);

		ReviewFormVO updatedReviewForm = reviewFormService.updateReviewForm(reviewFormVO);
		assertNotNull(updatedReviewForm);
		assertNotNull(updatedReviewForm.getId());
		assertEquals(updatedReviewForm.getId(), reviewForm.getId());
	}

	@Test
	public void testUpdateReviewForm_whenFormStatusIsApproved() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.APPROVED, true);

		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(reviewForm);
		Mockito.when(goalRepository.findAllByEmpIdAndPerformanceCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(mockGoalsList());
		Mockito.when(reviewFormRepository.save(reviewForm)).thenReturn(reviewForm);
		ReviewFormVO reviewFormVO = ReviewFormFixture.validReviewForm(FormStatus.APPROVED, true);

		ReviewFormVO updatedReviewForm = reviewFormService.updateReviewForm(reviewFormVO);
		assertNotNull(updatedReviewForm);
		assertNotNull(updatedReviewForm.getId());

	}

	@Test
	public void testCreateReviewFormSavedForLater() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		ReviewFormVO addedReviewForm = reviewFormService
				.createReviewForm(ReviewFormFixture.validReviewForm(FormStatus.INCOMPLETE, false));
		assertNotNull(addedReviewForm);
		assertNotNull(addedReviewForm.getId());

	}

	@Test(expected = ValidationException.class)
	public void testCreateReviewNoEmpId() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		reviewFormService.createReviewForm(ReviewFormFixture.reviewFormNoEmpId());

	}

	@Test(expected = ValidationException.class)
	public void testCreateReviewInvalidForm() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, true));
		reviewFormService.createReviewForm(ReviewFormFixture.invalidReviewForm());

	}

	@Test(expected = ValidationException.class)
	public void testCreateReviewYearLessThanCurrent() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.PENDING, true));
		reviewFormService.createReviewForm(ReviewFormFixture.reviewFormYearLessThanCurrent());

	}

	@Test(expected = ValidationException.class)
	public void testCreateReviewYearGreaterThanCurrent() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.PENDING, true));
		reviewFormService.createReviewForm(ReviewFormFixture.reviewFormYearGreaterThanCurrent());

	}

	@Test(expected = ValidationException.class)
	public void testCreateReviewEmptyForm() {
		ReviewForm reviewForm = ReviewFormFixture.validReviewFormModel(FormStatus.INCOMPLETE, false);
		Mockito.when(reviewFormRepository.save(reviewForm))
				.thenReturn(ReviewFormFixture.validReviewFormModel(FormStatus.PENDING, true));
		reviewFormService.createReviewForm(null);

	}

	@Test
	public void whenGetReviewForm_thenReturnReviewFormForEmployee() {
		String employeeId = "AFT00001";
		List<ReviewForm> reviewFormList = new ArrayList<>();
		reviewFormList.add(mockReviewForm());

		Mockito.when(reviewFormRepository.findByEmpId(Mockito.anyString())).thenReturn(reviewFormList);

		ReviewFormListVO reviewFormListVO = reviewFormService.getReviewForm(employeeId, null, null, null);

		assertNotNull(reviewFormListVO);
	}
	
	@Test
	public void testGetReviewForm_whenPerformanceCycleIsNotNull() {
		String employeeId = "AFT00001";
		List<ReviewForm> reviewFormList = new ArrayList<>();
		ReviewForm form = mockReviewForm();
		form.setFormStatus(FormStatus.INCOMPLETE);
		reviewFormList.add(form);

		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(reviewFormList);

		ReviewFormListVO reviewFormListVO = reviewFormService.getReviewForm(employeeId, "PerformanceCycle.APRIL", "2018", null);

		assertNotNull(reviewFormListVO);
		assertEquals(mockReviewForm().getEmpId(), reviewFormListVO.getReviewForms().get(0).getEmpId());
	}

	@Test
	public void whenGetReviewFormWithParameters_thenReturnReviewFormForEmployee() {
		String employeeId = "AFT00001", performanceCycle = "APRIL", year = "2019";
		List<ReviewForm> reviewFormList = new ArrayList<>();
		reviewFormList.add(mockReviewForm());

		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(reviewFormList);

		ReviewFormListVO reviewFormListVO = reviewFormService.getReviewForm(employeeId, performanceCycle, year, null);
		assertNotNull(reviewFormListVO);
	}

	@Test
	public void whenGetReviewFormWithParameters_thenReturnReviewFormForEmployeeNotHavingIncompleteStatus() {
		String employeeId = "AFT00001", performanceCycle = "APRIL", year = "2019";

		ReviewForm form = mockReviewForm();
		form.setFormStatus(FormStatus.INCOMPLETE);

		List<ReviewForm> reviewFormList = new ArrayList<>();
		reviewFormList.add(mockReviewForm());
		reviewFormList.add(form);

		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(reviewFormList);

		ReviewFormListVO reviewFormListVO = reviewFormService.getReviewForm(employeeId, performanceCycle, year, null);
		assertNotNull(reviewFormListVO);
	}

	@Test
	public void whenGetReviewForm_thenNoReviewFormForEmployee() throws NotFoundException {
		expectedException.expect(NotFoundException.class);

		String employeeId = "AFT00001";
		List<ReviewForm> reviewFormList = new ArrayList<>();

		Mockito.when(reviewFormRepository.findByEmpId(Mockito.anyString())).thenReturn(reviewFormList);

		reviewFormService.getReviewForm(employeeId, null, null, null);
	}

	@Test
	public void whenGetReviewForm_thenNoReviewFormsFound() throws NotFoundException {
		expectedException.expect(NotFoundException.class);

		String employeeId = "AFT00001";

		Mockito.when(reviewFormRepository.findByEmpId(Mockito.anyString())).thenReturn(null);

		reviewFormService.getReviewForm(employeeId, null, null, null);
	}

	@Test
	public void testAverage() {

		Integer[] roleSpecificRatings = { 6, 4, 2, 4, 1 };
		Integer[] goalRatings = { 4, 5, 4, 6, 5 };
		Integer[] commonCompetencyRatings = { 6, 3, 4, 5, 6 };

		List<Integer> roleSpecificRatingsList = new ArrayList<Integer>(Arrays.asList(roleSpecificRatings));
		List<Integer> goalRatingsList = new ArrayList<Integer>(Arrays.asList(goalRatings));
		List<Integer> commonCompetencyRatingsList = new ArrayList<Integer>(Arrays.asList(commonCompetencyRatings));

		double averageRoleSpecificRatings = 3.4;
		double averageGoalRatings = 4.8;
	  double averageCommonCompetencyRatings = 4.8; 
	  double finalRatings = Math.round((1.7 + 1.44 + 0.96)*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testAverageWhenRoleSpecificRatingsArrayIsEmpty() {

		Integer[] roleSpecificRatings = {};
		Integer[] goalRatings = { 6, 4, 2, 4, 1 };
		Integer[] commonCompetencyRatings = { 6, 3, 4, 5, 6 };

		List<Integer> roleSpecificRatingsList = new ArrayList<Integer>(Arrays.asList(roleSpecificRatings));
		List<Integer> goalRatingsList = new ArrayList<Integer>(Arrays.asList(goalRatings));
		List<Integer> commonCompetencyRatingsList = new ArrayList<Integer>(Arrays.asList(commonCompetencyRatings));

		double averageRoleSpecificRatings = 0;
		double averageGoalRatings = 3.4;
	  double averageCommonCompetencyRatings = 4.8; 
	  double finalRatings = Math.round((0.0 + 1.02 + 0.96)*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testAverageWhenGoalRatingsArrayIsEmpty() {

		Integer[] roleSpecificRatings = { 6, 4, 2, 4, 1 };
		Integer[] goalRatings = {};
		Integer[] commonCompetencyRatings = { 6, 3, 4, 5, 6 };

		List<Integer> roleSpecificRatingsList = new ArrayList<Integer>(Arrays.asList(roleSpecificRatings));
		List<Integer> goalRatingsList = new ArrayList<Integer>(Arrays.asList(goalRatings));
		List<Integer> commonCompetencyRatingsList = new ArrayList<Integer>(Arrays.asList(commonCompetencyRatings));

		double averageRoleSpecificRatings = 3.4;
		double averageGoalRatings = 0;
	  double averageCommonCompetencyRatings = 4.8; 
	  double finalRatings = Math.round((1.7 + 0.0 + 0.96)*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testAverageWhenCommonCompetencyRatingsArrayIsEmpty() {

		Integer[] roleSpecificRatings = { 6, 4, 2, 4, 1 };
		Integer[] goalRatings = { 6, 3, 4, 5, 6 };
		Integer[] commonCompetencyRatings = {};

		List<Integer> roleSpecificRatingsList = new ArrayList<Integer>(Arrays.asList(roleSpecificRatings));
		List<Integer> goalRatingsList = new ArrayList<Integer>(Arrays.asList(goalRatings));
		List<Integer> commonCompetencyRatingsList = new ArrayList<Integer>(Arrays.asList(commonCompetencyRatings));

		double averageRoleSpecificRatings = 3.4;
		double averageGoalRatings = 4.8;
	  double averageCommonCompetencyRatings = 0; 
	  double finalRatings = Math.round((1.7 + 1.44 + 0.0 )*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testAverageWhenRoleSpecificRatingsArrayIsNull() {

		Integer[] goalRatings = { 6, 4, 2, 4, 1 };
		Integer[] commonCompetencyRatings = { 6, 3, 4, 5, 6 };

		List<Integer> roleSpecificRatingsList = null;
		List<Integer> goalRatingsList = new ArrayList<Integer>(Arrays.asList(goalRatings));
		List<Integer> commonCompetencyRatingsList = new ArrayList<Integer>(Arrays.asList(commonCompetencyRatings));

		double averageRoleSpecificRatings = 0;
		double averageGoalRatings = 3.4;
	  double averageCommonCompetencyRatings = 4.8; 
	  double finalRatings = Math.round((0.0 + 1.02 + 0.96)*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testAverageWhenGoalRatingsArrayIsNull() {

		Integer[] roleSpecificRatings = { 6, 4, 2, 4, 1 };
		Integer[] commonCompetencyRatings = { 6, 3, 4, 5, 6 };

		List<Integer> roleSpecificRatingsList = new ArrayList<Integer>(Arrays.asList(roleSpecificRatings));
		List<Integer> goalRatingsList = null;
		List<Integer> commonCompetencyRatingsList = new ArrayList<Integer>(Arrays.asList(commonCompetencyRatings));

		double averageRoleSpecificRatings = 3.4;
		double averageGoalRatings = 0;
	  double averageCommonCompetencyRatings = 4.8; 
	  double finalRatings = Math.round((1.7 + 0.0 + 0.96)*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testAverageWhenCommonCompetencyRatingsArrayIsNull() {

		Integer[] roleSpecificRatings = { 6, 4, 2, 4, 1 };
		Integer[] goalRatings = { 6, 3, 4, 5, 6 };

		List<Integer> roleSpecificRatingsList = new ArrayList<Integer>(Arrays.asList(roleSpecificRatings));
		List<Integer> goalRatingsList = new ArrayList<Integer>(Arrays.asList(goalRatings));
		List<Integer> commonCompetencyRatingsList = null;

		double averageRoleSpecificRatings = 3.4;
		double averageGoalRatings = 4.8;
	  double averageCommonCompetencyRatings = 0; 
	  double finalRatings = Math.round((1.7 + 1.44 + 0.0 )*1e2)/1e2;
	  
		Map<String, Double> assummedRating = new HashMap<String, Double>();

		assummedRating.put("averageRoleSpecificRating", averageRoleSpecificRatings);
		assummedRating.put("averageGoalRatings", averageGoalRatings);
		assummedRating.put("averageCommonCompetencyRatings", averageCommonCompetencyRatings);
		assummedRating.put("finalRatings", finalRatings);

		Map<String, Double> response = reviewFormService.average(roleSpecificRatingsList, goalRatingsList,
				commonCompetencyRatingsList);

		assertNotNull(response);
		assertEquals(assummedRating, response);
	}

	@Test
	public void testDispatchReviewForms_whenEmployeeIsNotFound() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormIsNullAndRoleTagIsNotEmpty() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = null;

		List<RoleTag> roleTags = new ArrayList<>();
		roleTags.add(roleTag());

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormIsNullAndRoleTagIsEmpty() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = null;

		List<RoleTag> roleTags = new ArrayList<>();

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormStatusIsFailed() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = ReviewFormFixture.validReviewFormModel(FormStatus.FORM_RECEIVED, true);
		form.setFormStatus(FormStatus.FAILED);

		List<RoleTag> roleTags = new ArrayList<>();
		RoleTag tag = roleTag();
		tag.setBusinessPhilosophies(form.getBusinessPhilosophies());
		tag.setCommonCompetencies(form.getCommonCompetencies());
		tag.setRoleResponsibilities(form.getRoleResponsibilities());
		tag.setDescription("Test Description");
		roleTags.add(tag);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormIsNullAndRoleTagNotEmpty() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = ReviewFormFixture.validReviewFormModel(FormStatus.FORM_RECEIVED, true);

		List<RoleTag> roleTags = new ArrayList<>();
		RoleTag tag = roleTag();
		tag.setBusinessPhilosophies(form.getBusinessPhilosophies());
		tag.setCommonCompetencies(form.getCommonCompetencies());
		tag.setRoleResponsibilities(form.getRoleResponsibilities());
		tag.setDescription("Test Description");
		roleTags.add(tag);

		form = null;

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormStatusIsFailedAndThrowsException() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = ReviewFormFixture.validReviewFormModel(FormStatus.FAILED, true);

		List<CommonTag> commonTags = new ArrayList<CommonTag>();

		List<RoleTag> roleTags = new ArrayList<>();
		RoleTag tag = roleTag();
		tag.setBusinessPhilosophies(commonTags);
		tag.setCommonCompetencies(commonTags);
		tag.setRoleResponsibilities(commonTags);
		tag.setDescription("Test Description");
		roleTags.add(tag);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenThrow(new RuntimeException());
		Mockito.when(reviewFormRepository.findOne(anyString())).thenThrow(new RuntimeException());
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormStatusIsFailedAndRoleTagIsEmpty() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = ReviewFormFixture.validReviewFormModel(FormStatus.FAILED, true);

		List<RoleTag> roleTags = new ArrayList<>();

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormStatusIsPending() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = ReviewFormFixture.validReviewFormModel(FormStatus.PENDING, true);

		List<RoleTag> roleTags = new ArrayList<>();
		RoleTag tag = roleTag();
		tag.setBusinessPhilosophies(form.getBusinessPhilosophies());
		tag.setCommonCompetencies(form.getCommonCompetencies());
		tag.setRoleResponsibilities(form.getRoleResponsibilities());
		tag.setDescription("Test Description");
		roleTags.add(tag);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}

	@Test
	public void testDispatchReviewForms_whenFormStatusIsFormRecieved() throws Exception {

		PerformanceCycle cycle = PerformanceCycle.APRIL;
		Integer year = 2019;
		List<String> empIdList = new ArrayList<String>();
		empIdList.add("AFT00001");
		empIdList.add("AFT00003");

		ReviewForm form = ReviewFormFixture.validReviewFormModel(FormStatus.FORM_RECEIVED, true);

		List<RoleTag> roleTags = new ArrayList<>();
		RoleTag tag = roleTag();
		tag.setBusinessPhilosophies(form.getBusinessPhilosophies());
		tag.setCommonCompetencies(form.getCommonCompetencies());
		tag.setRoleResponsibilities(form.getRoleResponsibilities());
		tag.setDescription("Test Description");
		roleTags.add(tag);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());
		Mockito.when(reviewFormRepository.findByEmpIdAndCycleAndYear(Mockito.anyString(),
				Mockito.eq(PerformanceCycle.APRIL), Mockito.anyInt())).thenReturn(form);
		Mockito.when(roleTagRepository.findByTagNameAndStatus(Mockito.anyString(), Mockito.any())).thenReturn(roleTags);
		Mockito.when(reviewFormRepository.save(form)).thenReturn(form);
		Mockito.when(reviewFormRepository.findOne(anyString())).thenReturn(form);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(emailResponse());

		reviewFormService.dispatchReviewForms(cycle, year, empIdList);
	}
	
	@Test
	public void testGetReviewFormById() {
		
		String formId = "5c012f6722a8d1159cec114d";
		
		Mockito.when(reviewFormRepository.findOne(Mockito.anyString())).thenReturn(mockReviewForm());
		
		ReviewFormVO formVO = reviewFormService.getReviewFormById(formId);
		
		assertNotNull(formVO);
		assertEquals(formId, formVO.getId());
		
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetReviewFormById_whenReviewFormNotFound() {
		
		String formId = "5c012f6722a8d1159cec114d";
		
		Mockito.when(reviewFormRepository.findOne(Mockito.anyString())).thenReturn(null);
		
		reviewFormService.getReviewFormById(formId);
		
	}
}