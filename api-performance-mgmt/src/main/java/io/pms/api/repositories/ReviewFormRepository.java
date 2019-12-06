package io.pms.api.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.model.ReviewForm;

@Repository
public interface ReviewFormRepository extends MongoRepository<ReviewForm, String> {

	public List<ReviewForm> findByEmpId(String employeeId);

	public List<ReviewForm> findByEmpIdAndCycleAndYear(String employeeId, String performanceCycle, Integer year);

	public ReviewForm findByEmpIdAndCycleAndYear(String empId, PerformanceCycle cycle, Integer year);

	public List<ReviewForm> findAllFormStatusByAndCycleAndYear(FormStatus completed, String performanceCycle,
			String year);

	public List<ReviewForm> findAllByAndCycleAndYear(PerformanceCycle performanceCycle, Integer year);

}
