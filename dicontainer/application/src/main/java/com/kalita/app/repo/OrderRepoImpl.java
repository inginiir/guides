package com.kalita.app.repo;

import com.kalita.context.annotation.Component;

@Component
public class OrderRepoImpl implements OrderRepo {

    @Override
    public boolean buyItem(String user, String item) {
        return user != null && item != null;
    }
}
