package io.pms.api.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.common.Status;
import io.pms.api.model.Goal;

@Repository
public interface GoalRepository extends MongoRepository<Goal, String>{

	List<Goal> findAllByEmpId(String empId);

	List<Goal> findAllByEmpIdAndPerformanceCycleAndYear(String empId, String performanceCycle, Integer year);

	List<Goal> findAllByEmpIdAndPerformanceCycleAndYearAndStatus(String empId, String string, Integer i, Status active);

}
