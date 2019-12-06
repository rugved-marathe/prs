package io.pms.api.model;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "responsibilityMaster")
public class Responsibility {

    @Id
    private String responsibilityId;

    @NonNull
    private String responsibilityDescription;

    @NonNull @Indexed
    private String roleId;

    @LastModifiedDate
    private DateTime modifiedDate;

    @Builder
    public Responsibility(String responsibilityDescription, String roleId) {
        this.responsibilityDescription = responsibilityDescription;
        this.roleId = roleId;
    }
}
