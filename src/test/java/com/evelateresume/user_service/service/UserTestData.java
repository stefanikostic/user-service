package com.evelateresume.user_service.service;

import com.evelateresume.user_service.entity.User;
import com.evelateresume.user_service.entity.UserRole;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserTestData {

    public User getUser() {
        return new User("j1uwwi-susss-dsnjd", "username", "password", "Stefani Kostic", "stefanikostic@hotmail.com",
                UserRole.ADMIN.name());
    }
}
