package com.kalita.app.service;

import com.kalita.app.repo.UserRepo;
import com.kalita.context.annotation.Component;
import com.kalita.context.annotation.Injected;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Injected
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public String saveUser(String name) {
        String savedUser = userRepo.saveUser(name);
        return "User created: " + savedUser;
    }

    @Override
    public String getUser(String name) {
        return userRepo.getUser(name);
    }
}
