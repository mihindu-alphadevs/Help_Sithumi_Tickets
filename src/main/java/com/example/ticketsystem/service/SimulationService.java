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

    public void simulateTickets() {
        LOGGER.info("Ticket handling operations started.");
        //Always accessing the 1st Config Entry
        Optional<Configuration> optionalConfig = configurationService.getConfigurations(1L);
        if(optionalConfig.isEmpty()){
            LOGGER.error("No Configuration found");
            return;
        }
        Configuration configuration = optionalConfig.get();
        // Set the configuration dynamically
        ticketPool.setMaximumTicketCapacity(configuration.getMaxTicketCapacity());
        ticketPool.setInitialTickets(new ArrayList<>(configuration.getTotalTickets())); // Set initial tickets
        // Start Vendors
        int startingTicketNumber = configuration.getTotalTickets() + 1;
        for (int i = 1; i <= configuration.getNumberOfVendors(); i++) {
//            VendorService vendor = new VendorService();
//            vendor.setVendorId(i);
//            vendor.setTicketNumber(startingTicketNumber);
//            vendor.setTicketReleaseRate(configuration.getTicketReleaseRate());
//            vendor.setStopFlag(stopFlag);
//
//            Thread vendorThread = new Thread(vendor, "Vendor-" + i);
//            threads.add(vendorThread);
//            vendorThread.start();
//            startingTicketNumber += configuration.getMaxTicketCapacity(); // Ensure unique ticket numbers
            startTicketGeneration(startingTicketNumber,configuration.getTicketReleaseRate(),stopFlag,i);

        }

        // Start Customers
        for (int j = 1; j <= configuration.getNumberOfCustomers(); j++) {
            removeTicket(configuration.getCustomerRetrievalRate(), stopFlag, j);
        }
    }

    public void startTicketGeneration(int initialTicketNumber, int ticketReleaseRate, AtomicBoolean stopFlag, int vendorId) {
        generateTickets(initialTicketNumber, ticketReleaseRate, stopFlag, vendorId);
    }

    @Async
    public void generateTickets(int ticketNumber, int ticketReleaseRate, AtomicBoolean stopFlag, int vendorId) {
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

