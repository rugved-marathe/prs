package io.pms.api.fixture;

import java.util.Arrays;

import io.pms.api.common.CommonUtils;
import io.pms.api.common.FormStatus;
import io.pms.api.common.PerformanceCycle;
import io.pms.api.model.Accomplishment;
import io.pms.api.model.CommonTag;
import io.pms.api.model.ReviewForm;
import io.pms.api.model.TrainingCertification;
import io.pms.api.vo.AccomplishmentVO;
import io.pms.api.vo.CommonTagVO;
import io.pms.api.vo.ReviewFormVO;
import io.pms.api.vo.TrainingCertificationVO;

public class ReviewFormFixture {

	public static ReviewFormVO validReviewForm(FormStatus formStatus, boolean hasId) {
		ReviewFormVO reviewFormVO = new ReviewFormVO();
		if (hasId)
			reviewFormVO.setId("artporugr5034985ai4th0h");
		reviewFormVO.setEmpId("AFT001");
		reviewFormVO.setCycle(PerformanceCycle.APRIL);
		reviewFormVO.setYear(2019);
		reviewFormVO.setFormStatus(formStatus);
		if (FormStatus.PENDING.equals(formStatus)) {
			reviewFormVO.setRoleResponsibilities(Arrays.asList(commonTags()));
			reviewFormVO.setBusinessPhilosophies(Arrays.asList(commonTags()));
			reviewFormVO.setCommonCompetencies(Arrays.asList(commonTags()));
		} else if (FormStatus.FORM_RECEIVED.equals(formStatus)) {
			reviewFormVO.setRoleResponsibilities(Arrays.asList(commonTags()));
			reviewFormVO.setBusinessPhilosophies(Arrays.asList(commonTags()));
			reviewFormVO.setCommonCompetencies(Arrays.asList(commonTags()));
			reviewFormVO.setAccomplishments(Arrays.asList(accomplishment()));
			reviewFormVO.setTrainingCertification(Arrays.asList(trainings()));
		} else if (FormStatus.APPROVED.equals(formStatus)) {
			reviewFormVO.setRoleResponsibilities(Arrays.asList(commonTags()));
			reviewFormVO.setBusinessPhilosophies(Arrays.asList(commonTags()));
			reviewFormVO.setCommonCompetencies(Arrays.asList(commonTags()));
			reviewFormVO.setAccomplishments(Arrays.asList(accomplishment()));
			reviewFormVO.setTrainingCertification(Arrays.asList(trainings()));
		}
		return reviewFormVO;
	}

	public static ReviewFormVO invalidReviewForm() {
		ReviewFormVO reviewFormVO = new ReviewFormVO();
		reviewFormVO.setEmpId("AFT001");
		reviewFormVO.setCycle(PerformanceCycle.APRIL);
		reviewFormVO.setYear(2019);
		reviewFormVO.setFormStatus(FormStatus.PENDING);
		return reviewFormVO;
	}

	public static ReviewFormVO reviewFormYearLessThanCurrent() {
		ReviewFormVO reviewFormVO = new ReviewFormVO();
		reviewFormVO.setEmpId("AFT001");
		reviewFormVO.setCycle(PerformanceCycle.APRIL);
		reviewFormVO.setYear(2012);
		reviewFormVO.setFormStatus(FormStatus.INCOMPLETE);
		return reviewFormVO;
	}

	public static ReviewFormVO reviewFormYearGreaterThanCurrent() {
		ReviewFormVO reviewFormVO = new ReviewFormVO();
		reviewFormVO.setEmpId("AFT001");
		reviewFormVO.setCycle(PerformanceCycle.APRIL);
		reviewFormVO.setYear(2022);
		reviewFormVO.setFormStatus(FormStatus.INCOMPLETE);
		return reviewFormVO;
	}

	public static ReviewFormVO reviewFormNoEmpId() {
		ReviewFormVO reviewFormVO = new ReviewFormVO();
		reviewFormVO.setEmpId(null);
		reviewFormVO.setCycle(PerformanceCycle.APRIL);
		reviewFormVO.setYear(2022);
		reviewFormVO.setFormStatus(FormStatus.INCOMPLETE);
		return reviewFormVO;
	}

	public static ReviewForm validReviewFormModel(FormStatus formStatus, boolean hasId) {
		ReviewForm reviewFormModel = new ReviewForm();
		if (hasId)
			reviewFormModel.setId("artporugr5034985ai4th0h");
		reviewFormModel.setEmpId("AFT001");
		reviewFormModel.setCycle(PerformanceCycle.APRIL);
		reviewFormModel.setYear(2019);
		reviewFormModel.setFormStatus(formStatus);
		if (FormStatus.PENDING.equals(formStatus)) {
			reviewFormModel.setRoleResponsibilities(Arrays.asList(commonTagsModel()));
			reviewFormModel.setBusinessPhilosophies(Arrays.asList(commonTagsModel()));
			reviewFormModel.setCommonCompetencies(Arrays.asList(commonTagsModel()));
		} else if (FormStatus.FORM_RECEIVED.equals(formStatus)) {
			reviewFormModel.setAvgCommonCompetencyRating(3.8);
			reviewFormModel.setAvgRoleSpecificRating(4.2);
			reviewFormModel.setRoleResponsibilities(Arrays.asList(commonTagsModel()));
			reviewFormModel.setBusinessPhilosophies(Arrays.asList(commonTagsModel()));
			reviewFormModel.setCommonCompetencies(Arrays.asList(commonTagsModel()));
			reviewFormModel.setAccomplishments(Arrays.asList(accomplishmentModel()));
			reviewFormModel.setTrainingCertification(Arrays.asList(trainingsModel()));
		} else if (FormStatus.APPROVED.equals(formStatus)) {
			reviewFormModel.setAvgCommonCompetencyRating(3.8);
			reviewFormModel.setAvgRoleSpecificRating(4.2);
			reviewFormModel.setRoleResponsibilities(Arrays.asList(commonTagsModel()));
			reviewFormModel.setBusinessPhilosophies(Arrays.asList(commonTagsModel()));
			reviewFormModel.setCommonCompetencies(Arrays.asList(commonTagsModel()));
			reviewFormModel.setAccomplishments(Arrays.asList(accomplishmentModel()));
			reviewFormModel.setTrainingCertification(Arrays.asList(trainingsModel()));
		}

		return reviewFormModel;
	}

	private static AccomplishmentVO accomplishment() {
		AccomplishmentVO accomplishmentVO = new AccomplishmentVO();
		accomplishmentVO.setProjectName("Test Project");
		accomplishmentVO.setRoleInProject("Devloper");
		return accomplishmentVO;
	}

	private static CommonTagVO commonTags() {
		CommonTagVO commonTagVO = new CommonTagVO();
		commonTagVO.setAttribute("Test Responsibility");
		commonTagVO.setRating(4);
		commonTagVO.setSelfComments("Test Self Comments");
		commonTagVO.setReviewerComments("Test Reviewer Comments");
		return commonTagVO;
	}

	private static TrainingCertificationVO trainings() {
		TrainingCertificationVO trainings = new TrainingCertificationVO();
		trainings.setName("Test Trainings");
		return trainings;
	}

	private static Accomplishment accomplishmentModel() {
		Accomplishment target = new Accomplishment();
		CommonUtils.copyNonNullProperties(accomplishment(), target);
		return target;
	}

	private static CommonTag commonTagsModel() {
		CommonTag commonTag = new CommonTag();
		CommonUtils.copyNonNullProperties(commonTags(), commonTag);
		return commonTag;
	}

	private static TrainingCertification trainingsModel() {
		TrainingCertification trainings = new TrainingCertification();
		CommonUtils.copyNonNullProperties(trainings(), trainings);
		return trainings;
	}

}
