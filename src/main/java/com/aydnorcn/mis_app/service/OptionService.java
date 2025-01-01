package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.option.CreateOptionRequest;
import com.aydnorcn.mis_app.dto.option.UpdateOptionRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final PollService pollService;

    public Option getOptionById(String optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
    }

    public Option createOption(CreateOptionRequest request) {
        Poll poll = pollService.getPollById(request.getPollId());

        Option option = new Option();
        option.setText(request.getOptionText());
        option.setPoll(poll);

        return optionRepository.save(option);
    }

    public Option updateOption(String optionId, UpdateOptionRequest request) {
        Option option = getOptionById(optionId);
        option.setText(request.getOptionText());
        return optionRepository.save(option);
    }

    public void deleteOption(String optionId) {
        Option option = getOptionById(optionId);
        optionRepository.delete(option);
    }
}