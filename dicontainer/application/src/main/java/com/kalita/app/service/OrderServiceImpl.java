package com.kalita.app.service;

import com.kalita.app.repo.OrderRepo;
import com.kalita.context.annotation.Component;
import com.kalita.context.annotation.Injected;

@Component
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;

    @Injected
    public OrderServiceImpl(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public String buyItem(String user, String item) {
        if (orderRepo.buyItem(user, item)) {
            return "Order '" + item + "' by user " + user + " successfully created";
        } else {
            return "Error while creating an order";
        }
    }
}
