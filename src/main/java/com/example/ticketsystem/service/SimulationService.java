package com.example.ticketsystem.service;


import com.example.ticketsystem.model.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private List<Thread> threads = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketPoolService.class);

    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private TicketPoolService ticketPoolService;
    @Autowired
    private TicketPoolService ticketPool;

    public SimulationService() {
    }


    public void startTicketGeneration(int initialTicketNumber, int ticketReleaseRate, AtomicBoolean stopFlag, int vendorId) {
        generateTickets(initialTicketNumber, ticketReleaseRate, stopFlag, vendorId);
    }

    public void startTicketPurchasing(int customerID, int ticketReleaseRate, AtomicBoolean stopFlag) {
        buyTickets(customerID, ticketReleaseRate, stopFlag);
    }

    @Async("taskAddExecutor")
    public void generateTickets(int ticketNumber, int ticketReleaseRate, AtomicBoolean stopFlag, int vendorId) {
        LOGGER.info("== Add Ticket for {} == ",vendorId);
        while (!stopFlag.get()) {
            ticketPoolService.addTicket(ticketNumber++, vendorId);
            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                throw new RuntimeException("Thread was interrupted", e);
            }
        }
    }

    @Async("taskPurchaseExecutor")
    public void buyTickets(int customerID, int ticketReleaseRate, AtomicBoolean stopFlag) {
        LOGGER.info("== Purchase Ticket for {} == ",customerID);
        while (!stopFlag.get()) {
            ticketPoolService.buyTicket(customerID);
            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                throw new RuntimeException("Thread was interrupted", e);
            }
        }
    }

    public void stopSimulateTickets() {
        stopFlag.set(true);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        LOGGER.info("All ticket handling operations stopped.");
    }
}
