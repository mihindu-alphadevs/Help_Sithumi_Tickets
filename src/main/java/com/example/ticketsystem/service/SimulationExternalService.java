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
public class SimulationExternalService {
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private List<Thread> threads = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketPoolService.class);

    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private SimulationService simulationService;
    @Autowired
    private TicketPoolService ticketPool;

    public SimulationExternalService() {
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
            simulationService.startTicketGeneration(startingTicketNumber,configuration.getTicketReleaseRate(),stopFlag,i);
        }

        // Start Customers
        for (int i = 1; i <= configuration.getNumberOfCustomers(); i++) {
            simulationService.startTicketPurchasing(i,configuration.getTicketReleaseRate(),stopFlag);
        }
    }

}
