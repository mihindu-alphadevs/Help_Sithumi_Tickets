package com.example.ticketsystem.service;

import com.example.ticketsystem.model.Logs;
import com.example.ticketsystem.repository.LogsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private LogsRepository logRepository;

    public List<Logs> getAllLogs() {
        return logRepository.findAll();  // Fetch all logs from the database
    }
}
