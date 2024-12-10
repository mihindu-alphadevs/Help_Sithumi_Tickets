package com.example.ticketsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;


@Service
class CustomerService {

    private final TicketPoolService ticketPoolService;

    @Autowired
    public CustomerService(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }
    public void removeTicket(int customerRetrievalRate, AtomicBoolean stopFlag, int customerId) {
        removeTickets(customerRetrievalRate, stopFlag, customerId);
    }


    @Async
    public void removeTickets(int customerRetrievalRate, AtomicBoolean stopFlag, int customerId) {
        while (!stopFlag.get()) {
            ticketPoolService.buyTicket(customerId);
            try {
                Thread.sleep(customerRetrievalRate);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
