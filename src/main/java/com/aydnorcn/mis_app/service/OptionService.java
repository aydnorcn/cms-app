package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;

    public Option getOptionById(String optionId){
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
    }

    public Option createOption(Option option) {
        //TODO : Check if poll is active.
        //TODO : Check if user is allowed to create option.
        return null;
//        return optionRepository.save(option);
    }
}