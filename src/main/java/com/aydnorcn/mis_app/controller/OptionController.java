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
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Option> getOptionById(@PathVariable String optionId) {
        return ResponseEntity.ok(optionService.getOptionById(optionId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<Option> createOption(@Validated @RequestBody CreateOptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.createOption(request));
    }

    @PutMapping("/{optionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<Option> updateOption(@PathVariable String optionId, @Validated @RequestBody UpdateOptionRequest request) {
        return ResponseEntity.ok(optionService.updateOption(optionId, request));
    }

    @DeleteMapping("/{optionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<Void> deleteOption(@PathVariable String optionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}