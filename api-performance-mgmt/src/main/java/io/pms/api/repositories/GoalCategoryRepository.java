package io.pms.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.model.GoalCategory;

@Repository
public interface GoalCategoryRepository extends MongoRepository<GoalCategory, String> {
    GoalCategory findByGoalCategoryId(String id);
    GoalCategory findByGoalCategory(String goalCategory);
}
