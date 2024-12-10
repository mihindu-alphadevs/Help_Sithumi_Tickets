package com.example.ticketsystem.controller;

import com.example.ticketsystem.service.TicketSystemRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
class TicketController {

  private final TicketSystemRunner ticketSystemRunner;

  public TicketController(TicketSystemRunner ticketSystemRunner) {
    this.ticketSystemRunner = ticketSystemRunner;
  }

  @PostMapping("/start")
  public String startProcessing() {
    ticketSystemRunner.startProcessing();
    return "Ticket processing started.";
  }

  @PostMapping("/stop")
  public String stopProcessing() {
    ticketSystemRunner.stopProcessing();
    return "Ticket processing stopped.";
  }
}