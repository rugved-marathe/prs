package io.pms.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import io.pms.api.exception.ValidationException;
import io.pms.api.repositories.EmployeeRepository;
import io.pms.api.services.EmployeeService;
import io.pms.api.services.UploadCSVService;

@RunWith(SpringRunner.class)
public class UploadServiceTest {

	@InjectMocks
	private UploadCSVService uploadCSVService;

	@MockBean
	private EmployeeService employeeService;

	@MockBean
	private EmployeeRepository employeeRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(null);
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any())).thenReturn(null);
	}

	private MultipartFile getCSV(String fileName) throws IOException {
		return new MockMultipartFile("TestEmp.csv", fileName, "text/csv",Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
	}
	
	@Test
	public void testProcessFile() throws IOException {
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(null);
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any())).thenReturn(null);
		List<String> unProcessedRecords = uploadCSVService.processFile(getCSV("ValidEmpData.csv"));
		assertThat(unProcessedRecords).isEmpty();
	}

	@Test
	public void testProcessFileForNewEmployee() throws IOException{
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(null);
		List<String> unProcessedRecords = uploadCSVService.processFile(getCSV("ValidDataForNewEmp.csv"));
		assertThat(unProcessedRecords).isEmpty();
	}
	

	@Test
	public void testProcessFileNoRecordsProcessed() throws IOException{
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(null);
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any())).thenReturn(null);
		List<String> unProcessedRecords = uploadCSVService.processFile(getCSV("InvalidEmpData.csv"));
		assertThat(unProcessedRecords).isNotEmpty();
	}

	@Test
	public void testProcessFileAddEmployeeFailed() throws IOException {
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenThrow(Exception.class);
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any())).thenReturn(null);
		List<String> unProcessedRecords = uploadCSVService.processFile(getCSV("InvalidEmpData.csv"));
		assertThat(unProcessedRecords).isNotEmpty();
	}

	@Test
	public void testProcessFileEditEmployeeFailed() throws IOException {
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(null);
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any())).thenThrow(Exception.class);
		List<String> unProcessedRecords = uploadCSVService.processFile(getCSV("InvalidEmpData.csv"));
		assertThat(unProcessedRecords).isNotEmpty();
	}

	@Test(expected = ValidationException.class)
	public void testProcessFileEmptyFile() throws IOException {
		Mockito.when(employeeService.addEmployee(Mockito.any())).thenReturn(null);
		Mockito.when(employeeService.editEmployeeByEmpId(Mockito.any())).thenThrow(Exception.class);
		uploadCSVService.processFile(getCSV("EmptyData.csv"));
	}

}
