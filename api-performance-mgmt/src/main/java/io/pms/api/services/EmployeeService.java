package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.pms.api.common.CommonUtils;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.exception.AlreadyExistsException;
import io.pms.api.exception.Errors;
import io.pms.api.exception.NotFoundException;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.vo.EmployeeListVO;
import io.pms.api.vo.EmployeeReviewForm;
import io.pms.api.vo.EmployeeVO;
import io.pms.api.vo.ManagerVO;;

@Service
public class EmployeeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	public EmployeeListVO getEmployeeByEmpId(String empId) {
		EmployeeListVO employeeListVO = new EmployeeListVO();
		Employee employee = employeeRepository.findByEmpId(empId);
		if (null != employee) {
			EmployeeVO employeeVO = new EmployeeVO();
			BeanUtils.copyProperties(employee, employeeVO);
			employeeVO.setDateOfJoining(employee.getDateOfJoining().toString(DateTimeFormat.forPattern("dd/MM/yyyy")));
			employeeVO.setExperience(CommonUtils.computeExperience(employee.getDateOfJoining()));

			Employee manager = employeeRepository.findByEmpId(employee.getCurrentMgrId());
			Employee duHead = employeeRepository.findByEmpId(employee.getCurrentDUHeadId());
			if (null != manager)
				employeeVO.setManagerName(manager.getEmpName());

			if (null != duHead)
				employeeVO.setDuHeadName(duHead.getEmpName());

			List<EmployeeVO> employeeVOs = new ArrayList<>();
			employeeVOs.add(employeeVO);
			employeeListVO.setEmployees(employeeVOs);
			return employeeListVO;
		} else {
			LOGGER.error("employee with id " + empId + " not found");
			throw new NotFoundException(listOfErrors("employee"));
		}

	}

	public EmployeeListVO editEmployeeByEmpId(EmployeeVO employeeVO) {
		Employee employeeToBeEdited = employeeRepository.findByEmpId(employeeVO.getEmpId());
		if (employeeToBeEdited != null) {
			employeeVO.setEmail(null);
			CommonUtils.copyNonNullProperties(employeeVO, employeeToBeEdited);
			if (employeeVO.getStatus() != null) {
				employeeToBeEdited.setStatus(Status.valueOf(employeeVO.getStatus()));
			}
			if (employeeVO.getDateOfJoining() != null) {
				employeeToBeEdited.setDateOfJoining(
						DateTime.parse(employeeVO.getDateOfJoining(), DateTimeFormat.forPattern("dd/MM/yyyy")));
			}
			Employee employee = employeeRepository.save(employeeToBeEdited);
			EmployeeVO savedEmployeeVO = new EmployeeVO();
			BeanUtils.copyProperties(employee, savedEmployeeVO);
			savedEmployeeVO
					.setDateOfJoining(employee.getDateOfJoining().toString(DateTimeFormat.forPattern("dd/MM/yyyy")));
			savedEmployeeVO.setExperience(CommonUtils.computeExperience(employee.getDateOfJoining()));
			if (null != employee) {
				Employee manager = employeeRepository.findByEmpId(employee.getCurrentMgrId());
				Employee duHead = employeeRepository.findByEmpId(employee.getCurrentDUHeadId());
				if (null != manager)
					savedEmployeeVO.setManagerName(manager.getEmpName());
				
				if (null != duHead)
					savedEmployeeVO.setDuHeadName(duHead.getEmpName());
			}

			List<EmployeeVO> employeeList = new ArrayList<>();
			employeeList.add(savedEmployeeVO);
			EmployeeListVO employeeListVO = new EmployeeListVO();
			employeeListVO.setEmployees(employeeList);
			return employeeListVO;
		} else {
			LOGGER.error("employee with id " + employeeVO.getEmpId() + " not found");
			throw new NotFoundException(listOfErrors("employee"));
		}

	}

	public EmployeeListVO getAllEmployees(String managerId, String duHeadId, String performanceCycle) throws Exception {
		EmployeeListVO employeeListVO = new EmployeeListVO();
		List<Employee> employees = new ArrayList<Employee>();
		if (StringUtils.isNotBlank(managerId)) {
			LOGGER.debug("fetching employees with managerId " + managerId);
			Employee employee = employeeRepository.findByEmpId(managerId);
			if (employee != null)
				employees = employeeRepository.findAllByCurrentMgrId(managerId);
			else {
				LOGGER.error("manager with id " + managerId + " not found");
				throw new NotFoundException(listOfErrors("invalid manager id"));
			}
		} else if (StringUtils.isNotBlank(duHeadId)) {
			LOGGER.debug("fetching employees with duHeadId " + duHeadId);
			Employee employee = employeeRepository.findByEmpId(duHeadId);
			if (employee != null) {
				employees = employeeRepository.findAllByCurrentDUHeadId(duHeadId);
			} else {
				LOGGER.error("duHead with id " + duHeadId + " not found");
				throw new NotFoundException(listOfErrors("invalid DU head id"));
			}

		} else if (StringUtils.isNotBlank(performanceCycle)) {
			LOGGER.debug("fetching employees with performance cycle " + performanceCycle);
			if (EnumUtils.isValidEnum(PerformanceCycle.class, performanceCycle.toUpperCase())) {
				employees = employeeRepository.findAllByPerformanceCycle(performanceCycle);
			} else {
				LOGGER.error(performanceCycle + " is invalid performance cycle");
				throw new NotFoundException(listOfErrors("invalid performance cycle"));
			}

		} else {
			LOGGER.debug("fetching all the employees in database");
			employees = employeeRepository.findAll();
		}
		List<EmployeeVO> employeeList = employees.stream().map(employee -> {
			EmployeeVO employeeVO = new EmployeeVO();
			BeanUtils.copyProperties(employee, employeeVO);
			employeeVO.setDateOfJoining(employee.getDateOfJoining().toString(DateTimeFormat.forPattern("dd/MM/yyyy")));
			employeeVO.setExperience(CommonUtils.computeExperience(employee.getDateOfJoining()));
			return employeeVO;
		}).collect(Collectors.toList());
		employeeListVO.setEmployees(employeeList);
		return employeeListVO;

	}

	@Transactional(rollbackFor = { Exception.class })
	public EmployeeListVO swapReviewCycle(List<String> documentIds) throws Exception {

		EmployeeListVO employeeListVO = new EmployeeListVO();
		List<Employee> editedforEmployees = new ArrayList<>();
		for (String empId : documentIds) {
			Employee employee = employeeRepository.findOne(empId);
			if (null == employee) {
				throw new NotFoundException(listOfErrors("employee with id " + empId));
			}
		}
		for (String empId : documentIds) {
			Employee employee = employeeRepository.findOne(empId);
			if (employee.getPerformanceCycle().equals(PerformanceCycle.APRIL))
				employee.setPerformanceCycle(PerformanceCycle.OCTOBER);
			else
				employee.setPerformanceCycle(PerformanceCycle.APRIL);
			editedforEmployees.add(employeeRepository.save(employee));
			List<EmployeeVO> employeeList = editedforEmployees.stream().map(emp -> {
				EmployeeVO employeeVO = new EmployeeVO();
				BeanUtils.copyProperties(employee, employeeVO);
				return employeeVO;
			}).collect(Collectors.toList());

			employeeListVO.setEmployees(employeeList);

		}
		return employeeListVO;
	}

	public EmployeeListVO addEmployee(EmployeeVO employeeVO) {
		LOGGER.debug("adding new employee");
		List<Errors> validationErrorList = employeeVO.validateCreateEmployee();
		if (validationErrorList.isEmpty()) {
			LOGGER.debug("employeeVO passed validation");
			if (null == employeeRepository.findByEmail(employeeVO.getEmail())) {
				Employee employee = new Employee();
				CommonUtils.copyNonNullProperties(employeeVO, employee);
				employee.setId(null);
				employee.setStatus(Status.valueOf(employeeVO.getStatus()));
				employee.setDateOfJoining(
						DateTime.parse(employeeVO.getDateOfJoining(), DateTimeFormat.forPattern("dd/MM/yyyy")));
				employee = employeeRepository.save(employee);
				EmployeeVO savedEmployeeVO = new EmployeeVO();
				BeanUtils.copyProperties(employee, savedEmployeeVO);
				List<EmployeeVO> employeeList = new ArrayList<EmployeeVO>();
				employeeList.add(savedEmployeeVO);
				EmployeeListVO employeeListVO = new EmployeeListVO();
				employeeListVO.setEmployees(employeeList);
				return employeeListVO;
			} else {
				LOGGER.error("adding new employee failed as email " + employeeVO.getEmail() + " already exist");
				throw new AlreadyExistsException(employeeVO.validateDuplicateEmployee());
			}

		} else {
			LOGGER.error("employee validation failed.Error list : " + validationErrorList);
			throw new ValidationException(validationErrorList);
		}
	}

	/*
	 * NOTE: The YEAR parameter has been taken for avoiding to get older records and
	 * passed to subsequent method. Currently this param is not used while querying
	 * as there is no old data in system
	 */
	public List<EmployeeReviewForm> allEmployees(Integer year, String managerId, String duHeadId, String formStatus)
			throws Exception {
		List<EmployeeReviewForm> employeeReviewForms = new ArrayList<>();
		if (StringUtils.isNotBlank(managerId))
			employeeReviewForms = employeesByManager(year, managerId);
		else if (StringUtils.isNotBlank(duHeadId))
			employeeReviewForms = employeesByDUHead(year, duHeadId);
		else if (StringUtils.isNotBlank(formStatus))
			employeeReviewForms = findWithFormStatus(year, formStatus);
		else
			employeeReviewForms = findWithFormStatus(year, null);
		return employeeReviewForms;
	}

	private List<EmployeeReviewForm> employeesByManager(Integer year, String managerId) {

		AggregationOperation projection = Aggregation.project().and("empId").as("empId").and("empName").as("empName")
				.and("designation").as("designation").and("performanceCycle").as("cycle")
				.and("EmpReviewForm.formStatus").as("formStatus").and("EmpReviewForm._id").as("formId")
				.and("EmpReviewForm.year").as("year");

		LookupOperation lookupOperation = LookupOperation.newLookup().from("ReviewForm").localField("empId")
				.foreignField("empId").as("EmpReviewForm");
		/*
		 * Criteria criteria = Criteria.where("EmpReviewForm.year").is(year);
		 * if(StringUtils.isNotBlank(managerId)) { criteria =
		 * criteria.andOperator(Criteria.where("currentMgrId").is(managerId)); }
		 */

		Criteria criteria = Criteria.where("currentMgrId").is(managerId);

		Aggregation aggregation = Aggregation.newAggregation(lookupOperation, Aggregation.unwind("EmpReviewForm", true),
				Aggregation.match(criteria), projection);

		return mongoTemplate.aggregate(aggregation, "Employees", EmployeeReviewForm.class).getMappedResults();

	}

	private List<EmployeeReviewForm> findWithFormStatus(Integer year, String formStatus) {

		AggregationOperation projection = Aggregation.project().and("empId").as("empId").and("empName").as("empName")
				.and("profileImage").as("profileImage").and("designation").as("designation").and("performanceCycle")
				.as("cycle").and("EmpReviewForm.formStatus").as("formStatus").and("EmpReviewForm._id").as("formId")
				.and("EmpReviewForm.year").as("year");

		LookupOperation lookupOperation = LookupOperation.newLookup().from("ReviewForm").localField("empId")
				.foreignField("empId").as("EmpReviewForm");

		/*
		 * Criteria criteria = new
		 * Criteria().orOperator(Criteria.where("EmpReviewForm.year").is(year),Criteria.
		 * where("EmpReviewForm.year").is(0)); if(StringUtils.isNotBlank(formStatus)) {
		 * criteria =
		 * criteria.andOperator(Criteria.where("EmpReviewForm.formStatus").is(formStatus
		 * )); }
		 */

		Criteria criteria = new Criteria();
		if (StringUtils.isNotBlank(formStatus)) {
			criteria = Criteria.where("EmpReviewForm.formStatus").is(formStatus);
		}

		Aggregation aggregation = Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria),
				Aggregation.unwind("EmpReviewForm", true), projection);

		return mongoTemplate.aggregate(aggregation, "Employees", EmployeeReviewForm.class).getMappedResults();
	}

	private List<EmployeeReviewForm> employeesByDUHead(Integer year, String duHeadId) {

		AggregationOperation projection = Aggregation.project().and("empId").as("empId").and("empName").as("empName")
				.and("designation").as("designation").and("performanceCycle").as("cycle").and("currentMgrId")
				.as("currentMgrId").and("EmpMgr.empName").as("currentMgrName").and("EmpReviewForm.formStatus")
				.as("formStatus").and("EmpReviewForm._id").as("formId").and("EmpReviewForm.year").as("year");

		LookupOperation lookupOperation = LookupOperation.newLookup().from("ReviewForm").localField("empId")
				.foreignField("empId").as("EmpReviewForm");

		LookupOperation selfLookupOperation = LookupOperation.newLookup().from("Employees").localField("currentMgrId")
				.foreignField("empId").as("EmpMgr");

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("currentDUHeadId").is(duHeadId)), lookupOperation,
				Aggregation.unwind("EmpReviewForm", true), selfLookupOperation, Aggregation.unwind("EmpMgr"),
				projection, Aggregation.sort(Sort.Direction.ASC, "currentMgrId"));

		return mongoTemplate.aggregate(aggregation, "Employees", EmployeeReviewForm.class).getMappedResults();

	}

	public List<ManagerVO> getAllEmployeeByRole(Role role) {
		List<Employee> employeesByRole = employeeRepository.findAllByRoles(role);
		if (employeesByRole != null) {
			return employeesByRole.stream().map(employee -> {
				ManagerVO managerVO = new ManagerVO();
				managerVO.setId(employee.getEmpId());
				managerVO.setName(employee.getEmpName());
				return managerVO;
			}).collect(Collectors.toList());
		} else {
			LOGGER.error("Employees with role " + role.toString() + " not found");
			throw new PMSAppException(listOfErrors("System does not have employees of role : " + role.toString()));
		}
	}
}