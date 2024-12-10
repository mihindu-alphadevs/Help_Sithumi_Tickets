package com.example.ticketsystem.service;

import com.example.ticketsystem.model.Logs;
import com.example.ticketsystem.repository.LogsRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TicketPoolService {

  // Setter for maximumTicketCapacity
  @Setter
  private int maximumTicketCapacity;
  private final List<Integer> tickets = Collections.synchronizedList(new ArrayList<>());
  private static final Logger LOGGER = LoggerFactory.getLogger(TicketPoolService.class);

  @Autowired
  private LogsRepository logsRepository;


  public TicketPoolService(int maximumTicketCapacity, int totalTickets) {
      this.maximumTicketCapacity = maximumTicketCapacity;
      // Initialize with totalTickets
      for (int i = 1; i <= totalTickets; i++) {
          tickets.add(i);
      }
    }

  // Setter for initial ticket list
    public void setInitialTickets(List<Integer> tickets) {
        this.tickets.addAll(tickets);
    }

    public TicketPoolService() {
    }

    public synchronized void addTicket(int ticketNumber, int vendorId) {
        //LOGGER.info("Max:{}, Current : {}", maximumTicketCapacity, tickets.size());
        LOGGER.info("== Simulate Ticket Adding == ");
        while (tickets.size() >= maximumTicketCapacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        tickets.add(ticketNumber);
        String log = "Vendor " + vendorId + " added ticket: " + ticketNumber + " | TicketPool size: " + tickets.size();
      LOGGER.info(log);
      Logs logs = new Logs();
      logs.setLog(log);
      logs.setTimestamp(LocalDateTime.now());
      logsRepository.save(logs);
        notifyAll();
    }

    public synchronized int buyTicket(int customerId) {
        LOGGER.info("== Simulate Ticket Purchase == ");
        while (tickets.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        int ticket = tickets.remove(0);
        String log = "CustomerService " + customerId + " bought ticket: " + ticket + " | TicketPool size: " + tickets.size();
      LOGGER.info(log);
      Logs logs = new Logs();
      logs.setLog(log);
      logs.setTimestamp(LocalDateTime.now());
      logsRepository.save(logs);
        notifyAll();
        return ticket;
    }

}