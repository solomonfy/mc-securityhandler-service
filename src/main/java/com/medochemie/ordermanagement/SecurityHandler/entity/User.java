package com.medochemie.ordermanagement.SecurityHandler.entity;

import com.medochemie.ordermanagement.SecurityHandler.utilities.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Constant.USER_ENTITY)
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String userName;
    private String password;
    private Collection<Role> roles;
    private String phone;
    private String profileImageUrl;
    private boolean isActive;
    private boolean isNotLocked;
    private String agentId;
    private String countryCode;
    private String createdBy;
    private Date joinDate;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;

//    public void setEmailId() {
//        this.emailId = this.firstName + "." + this.lastName + "@" + this.countryCode+ ".@medochemie.com";
//    }
}
