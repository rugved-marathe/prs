package io.pms.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.common.CompletionStatus;
import io.pms.api.common.GoalType;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Status;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Employee;
import io.pms.api.model.Goal;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.repositories.GoalRepository;
import io.pms.api.services.GoalService;
import io.pms.api.vo.GoalListVO;
import io.pms.api.vo.GoalVO;

@RunWith(SpringRunner.class)
public class GoalServiceTest {

	@SpyBean
	private GoalService goalService;

	@MockBean
	private GoalRepository goalRepository;

	@MockBean
	private EmployeeRepository employeeRepository;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private Goal goal() {
		Goal mockGoal = new Goal();
		mockGoal.setCompletionStatus(CompletionStatus.COMPLETE);
		mockGoal.setDescription("goal description");
		mockGoal.setEmpId("AFT00000");
		mockGoal.setPerformanceCycle(PerformanceCycle.APRIL);
		mockGoal.setRating(4);
		mockGoal.setReviewerComments("xyz");
		mockGoal.setStatus(Status.ACTIVE);
		mockGoal.setYear(2019);
		mockGoal.setGoalType(GoalType.HIRING);
		mockGoal.setDuration(1);
		return mockGoal;
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
		mockGoalVO.setGoalType(GoalType.HIRING);
		mockGoalVO.setDuration(1);
		return mockGoalVO;
	}

	private Employee employee() {
		Employee employee = new Employee();
		employee.setEmpId("AFT00396");
		employee.setEmpName("Lionel Messi");
		employee.setCurrentMgrId("AFT00002");
		employee.setCurrentDUHeadId("AFT00001");
		employee.setDateOfJoining(new DateTime().withDate(2024, 1, 1));
		employee.setStatus(Status.ACTIVE);
		employee.setPerformanceCycle(PerformanceCycle.APRIL);
		employee.setCurrentProject(Arrays.asList("PMS"));
		employee.setDateOfLeaving(new DateTime().withDate(2024, 1, 1));
		return employee;
	}

	@Test
	public void testAddGoal() throws Exception {

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());
		GoalVO actualGoalVO = goalService.addGoal(goalVO());

		assertNotNull(actualGoalVO);
		assertEquals(goalVO(), actualGoalVO);
	}

	@Test(expected = ValidationException.class)
	public void testAddGoal_WhenGoalVOIsEmpty() throws Exception {

		GoalVO goalVO = new GoalVO();

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());
		goalService.addGoal(goalVO);

	}

	@Test(expected = ValidationException.class)
	public void testAddGoal_WhenNullEmpIdInGoalVO() throws Exception {

		GoalVO goalVO = goalVO();
		goalVO.setEmpId(null);

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());
		goalService.addGoal(goalVO);
	}

	@Test(expected = ValidationException.class)
	public void testAddGoal_WhenNullDescriptionInGoalVO() throws Exception {

		GoalVO goalVO = goalVO();
		goalVO.setDescription(null);

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());
		goalService.addGoal(goalVO);
	}

	@Test(expected = ValidationException.class)
	public void testAddGoal_WhenNullYearInGoalVO() throws Exception {

		GoalVO goalVO = goalVO();
		goalVO.setYear(null);

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());
		goalService.addGoal(goalVO);
	}

	@Test(expected = ValidationException.class)
	public void testAddGoal_WhenNullPerformanceCycleInGoalVO() throws Exception {

		GoalVO goalVO = goalVO();
		goalVO.setPerformanceCycle(null);

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());
		goalService.addGoal(goalVO);
	}

	@Test
	public void testGetGoals_ForEmpIdPerfoamanceCycleYear() throws Exception {
		List<Goal> goalList = new ArrayList<>();
		goalList.add(goal());
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee());
		Mockito.when(goalRepository.findAllByEmpIdAndPerformanceCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(goalList);
		GoalListVO goalListVO = goalService.getGoals("empId", "performanceCycle", 2019);

		GoalVO expectedGoalVO = goalListVO.getGoals().get(0);
		assertNotNull(expectedGoalVO);
		assertEquals(expectedGoalVO.getEmpId(), goal().getEmpId());
	}

	@Test
	public void testGetGoals_whenGoalStatusIsNull() throws Exception {
		List<Goal> goalList = new ArrayList<>();
		Goal goal = goal();
		goal.setStatus(null);
		goalList.add(goal);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee());
		Mockito.when(goalRepository.findAllByEmpIdAndPerformanceCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(goalList);
		GoalListVO goalListVO = goalService.getGoals("empId", "performanceCycle", 2019);

		GoalVO expectedGoalVO = goalListVO.getGoals().get(0);
		assertNotNull(expectedGoalVO);
		assertEquals(expectedGoalVO.getEmpId(), goal().getEmpId());
	}

	@Test
	public void testGetGoals_whenGoalStatusIsInvalid() throws Exception {
		List<Goal> goalList = new ArrayList<>();
		Goal goal = goal();
		goal.setStatus(Status.INACTIVE);
		goalList.add(goal);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(employee());
		Mockito.when(goalRepository.findAllByEmpIdAndPerformanceCycleAndYear(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt())).thenReturn(goalList);
		GoalListVO goalListVO = goalService.getGoals("empId", "performanceCycle", 2019);

		assertEquals(true, goalListVO.getGoals().isEmpty());
	}

	@Test(expected = NotFoundException.class)
	public void testGetGoals_WhenInvalidEmployeeId() throws Exception {
		List<Goal> goalList = new ArrayList<>();
		goalList.add(goal());

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(null);
		goalService.getGoals("empId", "performanceCycle", 2019);
	}

	@Test
	public void testEditGoal() throws Exception {

		Mockito.when(goalRepository.findOne(Mockito.anyString())).thenReturn(goal());
		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());

		GoalVO testedGoalVO = goalService.editGoal(goalVO());

		assertEquals(goalVO(), testedGoalVO);
	}

	@Test(expected = NotFoundException.class)
	public void testEditGoal_WhenInvalidGoalId() throws Exception {

		Mockito.when(goalRepository.findOne(Mockito.anyString())).thenReturn(null);
		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());

		goalService.editGoal(goalVO());
	}

	@Test
	public void testDeleteGoal() throws Exception {
		List<String> goalIds = new ArrayList<>();
		goalIds.add("goalId");

		Mockito.when(goalRepository.findOne(Mockito.anyString())).thenReturn(goal());

		goalService.deleteGoals("goalId");
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteGoal_WhenInvalidGoalId() throws Exception {
		List<String> goalIds = new ArrayList<>();
		goalIds.add("goalId");

		Mockito.when(goalRepository.findOne(Mockito.anyString())).thenReturn(null);

		goalService.deleteGoals("goalId");
	}

	private MultipartFile getTSV(String fileName) throws IOException {
		return new MockMultipartFile("Test.tsv", fileName, "text/tsv",
				Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
	}

	@Test
	public void testAddPreviousGoals() throws IOException {

		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(goal());

		List<String> unProcessedEmpIds = goalService.addPreviousGoals(getTSV("ValidPrevGoals.tsv"));

		assertEquals(true, unProcessedEmpIds.isEmpty());

	}

	@Test
	public void testAddPreviousGoals_whenBlankEntry() throws IOException {
		List<String> unProcessedEmpIds = goalService.addPreviousGoals(getTSV("BlankEntryInPrevGoals.tsv"));

		assertEquals("AFT00000", unProcessedEmpIds.get(0));
	}
	
	@Test
	public void testAddPreviousGoals_whenBlankLine() throws IOException {
		List<String> unProcessedEmpIds = goalService.addPreviousGoals(getTSV("BlankLineInPrevGoals.tsv"));

		assertEquals(true, unProcessedEmpIds.isEmpty());
	}

	@Test
	public void testAddPreviousGoals_whenErrorInParsingEmployeeRecord() throws IOException {
		Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenThrow(RuntimeException.class);

		List<String> unProcessedEmpIds = goalService.addPreviousGoals(getTSV("ValidPrevGoals.tsv"));

		assertNotNull(unProcessedEmpIds);
		assertEquals("AFT00000", unProcessedEmpIds.get(0));

	}

	@Test(expected = ValidationException.class)
	public void testAddPreviousGoals_whenFileIsEmpty() throws IOException {
		goalService.addPreviousGoals(getTSV("EmptyPrevGoals.tsv"));

	}

	@Test(expected = ValidationException.class)
	public void testAddPreviousGoals_whenFileIsNull() throws IOException {
		goalService.addPreviousGoals(null);

	}

	@Test(expected = ValidationException.class)
	public void testAddPreviousGoals_whenInvalidFile() {
		String fileName = "test.txt";

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fileName, "text/plain",
				"test data".getBytes());

		goalService.addPreviousGoals(mockMultipartFile);
	}
}
