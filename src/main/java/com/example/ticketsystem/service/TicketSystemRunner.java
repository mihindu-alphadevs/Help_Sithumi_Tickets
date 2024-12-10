package com.example.ticketsystem.service;

import com.example.ticketsystem.model.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketSystemRunner {

  private final List<String> ticketList = Collections.synchronizedList(new ArrayList<>());
  private final int MAX_TICKETS = 50; // Maximum number of tickets to add
  private final AtomicBoolean running = new AtomicBoolean(false);
  private ExecutorService executorService;
  private static final Logger LOGGER = LoggerFactory.getLogger(TicketSystemRunner.class);
  private Configuration configuration;
  @Autowired
  private ConfigurationService configurationService;

  public void startProcessing() {
    Optional<Configuration> optionalConfig = configurationService.getConfigurations(1L);
    if(optionalConfig.isEmpty()){
      LOGGER.error("No Configuration found");
      return;
    }
    configuration = optionalConfig.get();

    if (running.compareAndSet(false, true)) {
      executorService = Executors.newFixedThreadPool(2);

      // Thread for adding tickets
      executorService.submit(this::addTickets);

      // Thread for purchasing tickets
      executorService.submit(this::purchaseTickets);
    } else {
      System.out.println("Processing is already running.");
    }
  }

  public void stopProcessing() {
    if (running.compareAndSet(true, false)) {
      if (executorService != null) {
        executorService.shutdownNow();
        try {
          if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("Forcefully shutting down executor service.");
            executorService.shutdownNow();
          }
        } catch (InterruptedException e) {
          executorService.shutdownNow();
          Thread.currentThread().interrupt();
        }
      }
      System.out.println("Processing stopped.");
    } else {
      System.out.println("Processing is not running.");
    }
  }

  private void addTickets() {
    for (int i = 1; i <= configuration.getMaxTicketCapacity() && running.get(); i++) {
      try {
        Thread.sleep(configuration.getTicketReleaseRate()); // Simulate time taken to add tickets
        String ticket = "Ticket-" + i;
        ticketList.add(ticket);
        System.out.println("Added: " + ticket);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Adding tickets interrupted.");
      }
    }
  }

  private void purchaseTickets() {
    while (running.get()) {
      try {
        Thread.sleep(configuration.getCustomerRetrievalRate()); // Simulate time taken to purchase tickets
        synchronized (ticketList) {
          if (!ticketList.isEmpty()) {
            String ticket = ticketList.remove(0);
            System.out.println("Purchased: " + ticket);
          } else if (ticketList.isEmpty() && !running.get()) {
            break;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Purchasing tickets interrupted.");
      }
    }
  }
}
