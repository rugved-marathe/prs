package io.pms.api.services;

import static io.pms.api.common.CommonUtils.listOfErrors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.pms.api.common.CommonUtils;
import io.pms.api.common.Constants;
import io.pms.api.common.FormStatus;
import io.pms.api.common.Role;
import io.pms.api.exception.Errors;
import io.pms.api.exception.PMSAppException;
import io.pms.api.exception.ValidationException;
import io.pms.api.model.Employee;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.vo.EmailResponse;
import io.pms.api.vo.FormNotificationVO;
import io.pms.api.vo.NotificationVO;

@Service
public class NotificationService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Value("${emailServiceAPIKey}")
	private String emailServiceAPIKey;

	@Value("${emailHost}")
	private String emailHost;

	@Value("${serverURL}")
	private String apiURL;

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
	private static final String SUBJECT_KEY = "subject";

	@Value("${hrEmailGroup}")
	private String HR_DEPARTMENT_EMAIL;

	public EmailResponse sendNotification(NotificationVO notificationVO) {
		notificationVO.setApiKey(emailServiceAPIKey);
		notificationVO.setEmail(emailHost);
		EmailResponse responseEntity = null;
		try {
			responseEntity = restTemplate.postForObject(apiURL, notificationVO, EmailResponse.class);
		} catch (RestClientException e) {
			LOGGER.error("No response from the mail API.", e);
			throw new PMSAppException(listOfErrors("Error sending notification."));
		}

		return responseEntity;
	}

	public EmailResponse sendFormNotification(FormNotificationVO formNotificationVO) {
		List<Errors> validationErrorList = formNotificationVO.validateFormNotificationVO();
		if (!validationErrorList.isEmpty()) {
			LOGGER.error(validationErrorList.get(0).getMessage());
			throw new ValidationException(validationErrorList);
		}
		Employee employee = employeeRepository.findByEmpId(formNotificationVO.getEmployeeId());
		Employee manager = employeeRepository.findByEmpId(employee.getCurrentMgrId());
		Employee duHead = employeeRepository.findByEmpId(employee.getCurrentDUHeadId());
		
		Role role = formNotificationVO.getRole();
		Map<String, String> placeholders = new HashMap<>();
		switch (role) {
		case HR:
			if (FormStatus.PENDING.equals(formNotificationVO.getFormStatus())) {
				placeholders.put("name", employee.getEmpName());
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_PENDING);
				sendFormNotification(employee.getEmail(), "HR_PENDING_EMPLOYEE.vm", placeholders);
				sendFormNotification(manager.getEmail(), "HR_PENDING.vm", placeholders);
				sendFormNotification(duHead.getEmail(), "HR_PENDING.vm", placeholders);
			} else if (FormStatus.FINISHED.equals(formNotificationVO.getFormStatus())) {
				placeholders.put("name", employee.getEmpName());
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_FINISHED);
				
				sendFormNotification(HR_DEPARTMENT_EMAIL, "FINISHED_HR.vm", placeholders);
				sendFormNotification(manager.getEmail(), "FINISHED_MANAGER.vm", placeholders);
				sendFormNotification(duHead.getEmail(), "FINISHED_DU_HEAD.vm", placeholders);
				
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_FINISHED_EMPLOYEE);
				sendFormNotification(employee.getEmail(), "FINISHED_EMPLOYEE.vm", placeholders);
			} else {
				LOGGER.debug("Form status : {}", formNotificationVO.getFormStatus());
			}
			break;
		case EMPLOYEE:
			if (FormStatus.UNDER_REVIEW.equals(formNotificationVO.getFormStatus())) {
				placeholders.put("name", employee.getEmpName());
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_UNDER_REVIEW_EMPLOYEE);
				sendFormNotification(employee.getEmail(), "UNDER_REVIEW_EMPLOYEE.vm", placeholders);
				
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_UNDER_REVIEW);
				sendFormNotification(manager.getEmail(), "UNDER_REVIEW_MANAGER.vm", placeholders);
			} else {
				LOGGER.debug("Form status : {}", formNotificationVO.getFormStatus());
			}
			break;

		case MANAGER:
			if (FormStatus.FORM_RECEIVED.equals(formNotificationVO.getFormStatus())) {
				placeholders.put("name", employee.getEmpName());
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_FORM_RECIEVED);
				
				sendFormNotification(manager.getEmail(), "FORM_RECEIVED_MANAGER.vm", placeholders);
				sendFormNotification(duHead.getEmail(), "FORM_RECEIVED_DU_HEAD.vm", placeholders);
			} else if (FormStatus.COMPLETED.equals(formNotificationVO.getFormStatus())) {
				placeholders.put("name", employee.getEmpName());
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_COMPLETED);
				
				sendFormNotification(HR_DEPARTMENT_EMAIL, "COMPLETE_HR.vm", placeholders);
				sendFormNotification(manager.getEmail(), "COMPLETE_MANAGER.vm", placeholders);
			} else {
				LOGGER.debug("Form status : {}", formNotificationVO.getFormStatus());
			}
			break;

		case DU_HEAD:
			if (FormStatus.APPROVED.equals(formNotificationVO.getFormStatus())) {
				placeholders.put("name", employee.getEmpName());
				placeholders.put(SUBJECT_KEY, Constants.EMAIL_SUBJECT_APPROVED);
				sendFormNotification(HR_DEPARTMENT_EMAIL, "APPROVED_HR.vm", placeholders);
				sendFormNotification(manager.getEmail(), "APPROVED_MANAGER.vm", placeholders);
				sendFormNotification(duHead.getEmail(), "APPROVED_DU_HEAD.vm", placeholders);
			} else {
				LOGGER.debug("Form status : {}", formNotificationVO.getFormStatus());
			}
			break;
		}

		return null;
	}

	public void sendFormNotification(String email, String templateName, Map<String, String> placeholders) {
		NotificationVO notificationVO = new NotificationVO();

		if (StringUtils.isNotBlank(templateName)) {
			String body = CommonUtils.getEmailBody(templateName, placeholders);
			LOGGER.debug("TemplateName: {} and Generated Body: {}", templateName, body);
			notificationVO.setTo(email);
			notificationVO.setSubject(placeholders.get(SUBJECT_KEY));
			notificationVO.setHtml(body);
		}
		this.sendNotification(notificationVO);
	}
}
