package com.example.ticketsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class VendorService {

  private final TicketPoolService ticketPoolService;

  @Autowired
  public VendorService(TicketPoolService ticketPoolService) {
    this.ticketPoolService = ticketPoolService;
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
}
