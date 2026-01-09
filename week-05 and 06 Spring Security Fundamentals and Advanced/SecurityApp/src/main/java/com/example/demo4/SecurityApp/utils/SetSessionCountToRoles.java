package com.example.demo4.SecurityApp.utils;

import java.util.Map;

public class SetSessionCountToRoles {
    private final static Map<String, Integer> map = Map.of("ROLE_USER",2,"ROLE_CREATOR",4,"ROLE_ADMIN",5);
    public static Integer getSessionForRole(String role){
        return map.get(role);
    }
}