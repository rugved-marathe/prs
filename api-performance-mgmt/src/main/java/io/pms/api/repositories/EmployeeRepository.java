package io.pms.api.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.common.Role;
import io.pms.api.model.Employee;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

	public Employee findByEmpId(String empId);

	public List<Employee> findAllByCurrentMgrId(String mgrEmpId);

	public Employee findByEmail(String emailId);

	public List<Employee> findAllByCurrentDUHeadId(String duHeadEmpId);

	public List<Employee> findAllByPerformanceCycle(String performanceCycle);
	
	public List<Employee> findAllByRoles(Role role);
}
