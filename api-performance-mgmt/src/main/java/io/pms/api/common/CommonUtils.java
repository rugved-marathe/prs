package io.pms.api.common;

import static io.pms.api.common.Constants.NOT_FOUND;
import static io.pms.api.common.ErrorType.RESOURCE_NOT_FOUND;

import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import io.pms.api.exception.Errors;
import io.pms.api.model.Employee;

/**
 * {@code CommonUtils} Provides common application related utilities.
 * 
 */

public class CommonUtils {

	
	private static VelocityEngine velocityEngine;

	static {
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	
	
	/**
	 * @param sourceType
	 *            source of the error object
	 * @return {@link Errors} return an Errors object
	 */
	public static Errors validationErrorObject(String sourceType) {
		return Errors.builder().source(sourceType).message(RESOURCE_NOT_FOUND.getErrorMessage())
				.detailedMessage(sourceType.concat(NOT_FOUND)).build();
	}

	/**
	 * @param errorMessages
	 *            sourceTypes to be included in the error list
	 * @return {@link List<Errors>} list of errors
	 */
	public static List<Errors> listOfErrors(String... errorMessages) {
		List<String> listOfErrorMessages = Arrays.asList(errorMessages);
		List<Errors> listOfErrors = listOfErrorMessages.stream().filter(Objects::nonNull)
				.map(CommonUtils::validationErrorObject).collect(Collectors.toList());
		return listOfErrors;
	}

	/**
	 * @param source
	 *            detailedMessage to be included in the error list
	 * @param errorMessage
	 *            specific error message
	 * @param detailedMessage
	 *            detailedMessage message
	 * @param errorList
	 */
	public static void createErrorList(String source, String errorMessage, String detailedMessage,
			List<Errors> errorList) {
		Errors errors = new Errors(source, errorMessage, source.concat(detailedMessage));
		errorList.add(errors);
	}

	public static boolean checkAuthoirty(AuthorizationServerTokenServices tokenServices, String role) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Map<String, Object> additionalInfo = tokenServices.getAccessToken((OAuth2Authentication) authentication)
				.getAdditionalInformation();
		@SuppressWarnings("unchecked")
		Set<String> customInfo = (Set<String>) additionalInfo.get("roles");
		if (customInfo.contains(role))
			return true;
		else
			return false;
	}

	public static String checkNull(Employee employee) {
		if (employee.getDateOfLeaving() != null)
			return employee.getDateOfLeaving().toString();
		return "";
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] allPropertyDescriptors = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (PropertyDescriptor pd : allPropertyDescriptors) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * ignoring only null property values.
	 * 
	 * @param src
	 *            Source the source bean.
	 * @param target
	 *            Target the target bean.
	 * @throws BeansException
	 *             if the copying failed.
	 */
	public static void copyNonNullProperties(Object src, Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static Period computeExperience(DateTime joiningDate) {
		return new Period(joiningDate.toLocalDate(), new LocalDate(), PeriodType.yearMonthDay());
	}
	
	public static String getEmailBody(String templateName, Map<String, String> placeHoldersMap) {

		Template template = velocityEngine.getTemplate("/templates/" + templateName);
		VelocityContext context = new VelocityContext();
		placeHoldersMap.forEach((key, value) -> context.put(key, value));
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		String body = writer.toString();
		return body;

	}
	
}
