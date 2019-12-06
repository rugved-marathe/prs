package io.pms.api.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.pms.api.controllers.UploadCSVController;
import io.pms.api.services.UploadCSVService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UploadCSVController.class, secure = false)
public class UploadCsvControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UploadCSVService uploadCSVService;
	
	@MockBean
	private AuditingHandler auditingHandler;
	
	@Before
	public void setup() {

	}
	
	private MockMultipartFile getCSV(String fileName) throws IOException {
		return new MockMultipartFile("file",Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
	}
	
	@Test
	public void testProcessFile() throws IOException, Exception {
		mockMvc.perform(fileUpload("/upload").file(getCSV("ValidEmpData.csv")))
		.andExpect(status().isCreated());
		
	}
	
	@Test
	public void testProcessFilePartially() throws IOException, Exception {
		Mockito.when(uploadCSVService.processFile(any())).thenReturn(Arrays.asList("AFT00498"));
		mockMvc.perform(fileUpload("/upload").file(getCSV("InvalidEmpData.csv")))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("Following Employee Ids are not processed")));
		
	}
}
