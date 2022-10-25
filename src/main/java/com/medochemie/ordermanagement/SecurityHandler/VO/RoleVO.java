package com.medochemie.ordermanagement.SecurityHandler.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO {

    private String id;
    private String roleName;
    private String description;
}
