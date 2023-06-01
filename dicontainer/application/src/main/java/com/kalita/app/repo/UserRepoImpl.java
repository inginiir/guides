package com.kalita.app.repo;

import com.kalita.context.annotation.Component;

@Component
public class UserRepoImpl implements UserRepo {

    @Override
    public String saveUser(String name) {
        return name;
    }

    @Override
    public String getUser(String name) {
        return name;
    }
}
