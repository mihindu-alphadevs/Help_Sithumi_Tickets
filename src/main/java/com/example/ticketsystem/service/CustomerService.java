package com.example.ticketsystem.service;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
class CustomerService implements Runnable {

    @Autowired
    private TicketPoolService ticketPool;
    private int customerRetrievalRate;
    private AtomicBoolean stopFlag;
    private int customerId;

    public CustomerService(int customerId, TicketPoolService ticketPool, int customerRetrievalRate, AtomicBoolean stopFlag) {
        this.customerId = customerId;
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.stopFlag = stopFlag;
    }

    // Setter methods
    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public void setStopFlag(AtomicBoolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public CustomerService() {
    }

    @Override
    public void run() {
        while (!stopFlag.get()) {
            ticketPool.buyTicket(customerId);
            try {
                Thread.sleep(customerRetrievalRate);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
