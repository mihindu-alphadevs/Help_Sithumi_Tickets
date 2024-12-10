package com.example.ticketsystem.controller;


import com.example.ticketsystem.model.Logs;
import com.example.ticketsystem.service.LogService;
import com.example.ticketsystem.service.SimulationExternalService;
import com.example.ticketsystem.service.SimulationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    @Autowired
    private SimulationService simulationService;
    @Autowired
    private LogService logService;
    @Autowired
    private SimulationExternalService simulationExternalService;


    @PostMapping("/start")
    public String simulatorStart() {
        simulationExternalService.simulateTickets();
        return "Simulating ticket additions...";
    }


    @GetMapping("/stop")
    public String simulatorStop() {
        // Stop the simulation
        simulationService.stopSimulateTickets();

        // Fetch logs after stopping the simulation
        List<Logs> logs = logService.getAllLogs();

        // Build the response message
        StringBuilder response = new StringBuilder("Simulation stopped. Current Logs:\n");

        for (Logs log : logs) {
            response.append("Log ID: ").append(log.getLogId())
                    .append(", Log Message: ").append(log.getLog())
                    .append(", Timestamp: ").append(log.getTimestamp())
                    .append("\n");
        }

        // Return the combined response
        return response.toString();
    }
}
