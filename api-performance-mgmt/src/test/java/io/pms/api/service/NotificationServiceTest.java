package io.pms.api.service;

import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.common.Role;
import io.pms.api.common.Status;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.services.NotificationService;
import io.pms.api.vo.EmailResponse;
import io.pms.api.vo.FormNotificationVO;
import io.pms.api.vo.NotificationVO;

@RunWith(SpringRunner.class)
public class NotificationServiceTest {

	@MockBean
	private MappingMongoConverter mongoConverter;

	@MockBean
	private AuditingHandler auditingHandler;

	@MockBean
	private EmployeeRepository employeeRepository;

	@MockBean
	private RestTemplate restTemplate;

	@InjectMocks
	private NotificationService notificationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private EmailResponse mockEmailResponse() {
		EmailResponse emailResponse = new EmailResponse();
		emailResponse.setSuccess("true");
		emailResponse.setMsg("Email Sent Successfully !!!");
		return emailResponse;
	}

	private FormNotificationVO mockFormNotificationVO() {
		FormNotificationVO formNotificationVO = new FormNotificationVO();
		formNotificationVO.setEmployeeId("AFT00001");
		return formNotificationVO;
	}

	private NotificationVO notificationVO() {
		NotificationVO notificationVO = new NotificationVO();
		notificationVO.setApiKey("");
		notificationVO.setEmail("john.doe@afourtech.com");
		notificationVO.setTo("james.gosling@afourtech.com");
		notificationVO.setSubject("Test Subject");
		notificationVO.setHtml("This is mail body.");
		return notificationVO;
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
		employee.setCurrentProject(Arrays.asList("PRS"));
		return employee;
	}

	@Test
	public void whenSendFormNotificationForUnderReviewStatusIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.EMPLOYEE);
		mockFormNotification.setFormStatus(FormStatus.UNDER_REVIEW);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForPendingStatusIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.HR);
		mockFormNotification.setFormStatus(FormStatus.PENDING);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForFormReceivedStatusIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.MANAGER);
		mockFormNotification.setFormStatus(FormStatus.FORM_RECEIVED);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForApprovedStatusIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.DU_HEAD);
		mockFormNotification.setFormStatus(FormStatus.APPROVED);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForPendingStatusAndHrRoleIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.HR);
		mockFormNotification.setFormStatus(FormStatus.INCOMPLETE);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForPendingStatusAndEmployeeRoleIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.EMPLOYEE);
		mockFormNotification.setFormStatus(FormStatus.PENDING);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForPendingStatusAndManagerRoleIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.MANAGER);
		mockFormNotification.setFormStatus(FormStatus.PENDING);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}

	@Test
	public void whenSendFormNotificationForPendingStatusAndDuHeadRoleIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.DU_HEAD);
		mockFormNotification.setFormStatus(FormStatus.PENDING);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}
	
	@Test
	public void whenSendFormNotificationForFinishedStatusAndHrRoleIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.HR);
		mockFormNotification.setFormStatus(FormStatus.FINISHED);
		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}
	
	@Test
	public void whenSendFormNotificationForCompletedStatusAndManagerRoleIsTriggered_thenNotificationIsSent() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.MANAGER);
		mockFormNotification.setFormStatus(FormStatus.COMPLETED);

		Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(mockEmployee());

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		EmailResponse emailResponse = notificationService.sendFormNotification(mockFormNotification);

		assertNull(emailResponse);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = ValidationException.class)
	public void whenSendFormNotificationForPendingStatusAndDuHeadRoleIsTriggered_thenThrowValidationException() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(null);
		mockFormNotification.setFormStatus(FormStatus.PENDING);

		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());

		notificationService.sendFormNotification(mockFormNotification);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = ValidationException.class)
	public void whenSendFormNotificationForPendingStatusAndManagerRoleIsTriggered_thenThrowValidationException() {
		FormNotificationVO mockFormNotification = mockFormNotificationVO();
		mockFormNotification.setRole(Role.MANAGER);
		mockFormNotification.setFormStatus(null);
		
		Mockito.when(restTemplate.postForObject("http://192.168.7.196:80/projects/sendEmail", notificationVO(),
				EmailResponse.class)).thenReturn(mockEmailResponse());
		
		notificationService.sendFormNotification(mockFormNotification);
	}
	
	
	/*
	 * @Test public void
	 * whenSendFormNotificationForNullRoleValueIsTriggered_thenNotificationIsNotSent
	 * () { FormNotificationVO mockFormNotification = mockFormNotificationVO();
	 * mockFormNotification.setFormStatus(FormStatus.PENDING);
	 * Mockito.when(employeeRepository.findByEmpId(Mockito.anyString())).thenReturn(
	 * mockEmployee());
	 * 
	 * Mockito.when(restTemplate.postForObject(
	 * "http://192.168.7.196:80/projects/sendEmail", notificationVO(),
	 * EmailResponse.class)).thenReturn(mockEmailResponse());
	 * 
	 * EmailResponse emailResponse =
	 * notificationService.sendFormNotification(mockFormNotification);
	 * 
	 * assertNull(emailResponse); }
	 */
}
