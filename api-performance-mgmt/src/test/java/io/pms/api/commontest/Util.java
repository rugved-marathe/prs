package io.pms.api.commontest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class Util {

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void checkErrorJson(MvcResult result, String sourceType, String message, String source)
			throws Exception {
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		JSONArray errorsArray = jsonObject.getJSONArray("errors");
		JSONObject errorObject = errorsArray.getJSONObject(0);
		assertEquals(sourceType, errorObject.get("source"));
		assertEquals(message, errorObject.get("message"));
		assertEquals(source, errorObject.get("detailedMessage"));
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
	 * @param src source the source bean.
	 * @param target target the target bean.
	 * @throws BeansException if the copying failed.
	 */
	public static void copyNonNullProperties(Object src, Object target) throws BeansException{
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}
}
