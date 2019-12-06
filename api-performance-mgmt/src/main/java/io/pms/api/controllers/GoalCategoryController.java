package io.pms.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.pms.api.model.GoalCategory;
import io.pms.api.services.GoalCategoryService;
import io.pms.api.vo.GoalCategoryListVO;
import io.pms.api.vo.GoalCategoryVO;

@RestController
public class GoalCategoryController {

    @Autowired
    GoalCategoryService goalCategoryService;

    @RequestMapping(value = "/goal-categories", method = RequestMethod.POST)
    public ResponseEntity<GoalCategory> createGoalCategory(@RequestBody GoalCategoryVO goalCategoryVO) {
        return new ResponseEntity<>(goalCategoryService.createGoalCategory(goalCategoryVO), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/goal-categories", method = RequestMethod.GET)
    public ResponseEntity<GoalCategoryListVO> getGoalCategories() {
        GoalCategoryListVO goalCategories = goalCategoryService.getGoalCategories();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Total-Count", String.valueOf(goalCategories.getGoalCategories().size()));
        return new ResponseEntity<>(goalCategories, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/goal-categories/{goalCategoryId}", method = RequestMethod.PUT)
    public ResponseEntity<GoalCategory> editGoalCategory(@PathVariable("goalCategoryId") String goalCategoryid, @RequestBody GoalCategoryVO goalCategoryVO) {
        return new ResponseEntity<>(goalCategoryService.editGoalCategory(goalCategoryid, goalCategoryVO), HttpStatus.OK);
    }

    @RequestMapping(value = "/goal-categories/{goalCategoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<GoalCategory> deleteGoalCategory(@PathVariable("goalCategoryId") String goalCategoryId) {
        return new ResponseEntity<>(goalCategoryService.deleteGoalCategory(goalCategoryId), HttpStatus.OK);
    }
}
