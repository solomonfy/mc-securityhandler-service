package com.medochemie.ordermanagement.SecurityHandler.entity;

import com.medochemie.ordermanagement.SecurityHandler.utilities.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Constant.ROLE_ENTITY)
public class Role {

    @Id
    private String id;
    private String roleName;
//    private String description;
//    private boolean isActive;
//    private Date createdOn;
//    private String createBy;
//    private String updatedBy;
//    private Date updatedOn;
}
