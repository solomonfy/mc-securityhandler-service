package com.medochemie.ordermanagement.SecurityHandler.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String userName;
    private Collection<RoleVO> roleVOS;
    private String phone;
    private String profileImageUrl;
    private boolean isActive;
    private boolean isNotLocked;
    private String agentId;
    private String countryCode;
    private Date joinDate;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
}
