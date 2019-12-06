package io.pms.api.vo;

import io.pms.api.common.FormStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinalRatingReportVO {
	private String empId;
	private String empName;
	private Double finalRating;
	private String formStatus;
	@Override
	public String toString() {
		return this.empId + "," + this.empName + "," + this.finalRating + "," + this.formStatus +"\n";

	}
}