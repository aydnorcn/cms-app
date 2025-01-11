package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.option.CreateOptionRequest;
import com.aydnorcn.mis_app.dto.option.UpdateOptionRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @GetMapping("/{optionId}")
    public ResponseEntity<Option> getOptionById(@PathVariable String optionId) {
        return ResponseEntity.ok(optionService.getOptionById(optionId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('ORGANIZATOR')")
    public ResponseEntity<Option> createOption(@Validated @RequestBody CreateOptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.createOption(request));
    }

    @PutMapping("/{optionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('ORGANIZATOR')")
    public ResponseEntity<Option> updateOption(@PathVariable String optionId, @Validated @RequestBody UpdateOptionRequest request) {
        return ResponseEntity.ok(optionService.updateOption(optionId, request));
    }

    @DeleteMapping("/{optionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('ORGANIZATOR')")
    public ResponseEntity<Void> deleteOption(@PathVariable String optionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}