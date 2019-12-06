package io.pms.api.model;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Document(collection = "common")
public class Common {

    @Id
    private String id;

    private List<String> competencies;

    private List<String> philosophies;

    @LastModifiedDate
    private DateTime modifiedDate;
}
