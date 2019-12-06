package io.pms.api.model;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
@Getter @Setter
@Document(collection = "goalCategoryMaster")
public class GoalCategory {
    @Id
    private String goalCategoryId;

    @NonNull
    private String goalCategory;

    @LastModifiedDate
    private DateTime modifiedDate;

}
