package com.medochemie.ordermanagement.SecurityHandler.enums;

public enum Authority {
    READ_USER("READ_USER"),
    UPDATE_USER("UPDATE_USER"),
    DELETE_USER("DELETE_USER"),
    ADD_USER("ADD_USER"),
    INACTIVATE_USER("INACTIVATE_USER");

    private String authority;

    //constructor
    Authority(String authority){
        this.authority = authority;
    }

    //getter
    private String getAuthority(){
        return this.authority;
    }
}
