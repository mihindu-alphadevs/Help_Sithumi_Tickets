package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.AddConfigurationDto;
import com.example.ticketsystem.model.Configuration;
import com.example.ticketsystem.repository.ConfigurationRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationService {
    @Autowired
    private ConfigurationRepository configurationRepository;

    public String saveConfiguration(AddConfigurationDto addConfigurationDto) {
        Configuration configuration = new Configuration();
        configuration.setNumberOfCustomers(addConfigurationDto.getNumberOfCustomers());
        configuration.setTotalTickets(addConfigurationDto.getTotalTickets());
        configuration.setCustomerRetrievalRate(addConfigurationDto.getCustomerRetrievalRate());
        configuration.setMaxTicketCapacity(addConfigurationDto.getMaxTicketCapacity());
        configuration.setNumberOfVendors(addConfigurationDto.getNumberOfVendors());
        configuration.setTicketReleaseRate(addConfigurationDto.getTicketReleaseRate());
        configurationRepository.save(configuration);
        return "configuration saved successfully.";
    }

    public Optional<Configuration> getConfigurations(Long id){
        return configurationRepository.findById(id);
    }
}

