package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.dto.option.CreateOptionRequest;
import com.aydnorcn.mis_app.dto.option.OptionResponse;
import com.aydnorcn.mis_app.dto.option.UpdateOptionRequest;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
@Tag(name = "Option Controller")
public class OptionController {

    private final OptionService optionService;

    @Operation(
            summary = "Retrieve option by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Option found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Option not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{optionId}")
    public ResponseEntity<APIResponse<OptionResponse>> getOptionById(@PathVariable String optionId) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Option retrieved successfully", new OptionResponse(optionService.getOptionById(optionId))));
    }

    @Operation(
            summary = "Create a new option for a poll"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Option created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Bad request | If request param(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Poll not found | If given poll id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to create option",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<APIResponse<OptionResponse>> createOption(@Validated @RequestBody CreateOptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Option created successfully", new OptionResponse(optionService.createOption(request))));
    }

    @Operation(
            summary = "Update option name by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Option updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Option not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Bad request | If request param(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to update option",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{optionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<APIResponse<OptionResponse>> updateOption(@PathVariable String optionId, @Validated @RequestBody UpdateOptionRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Option updated successfully", new OptionResponse(optionService.updateOption(optionId, request))));
    }

    @Operation(
            summary = "Delete option by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Option deleted"),
                    @ApiResponse(responseCode = "404", description = "Option not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to delete option",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{optionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<Void> deleteOption(@PathVariable String optionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}