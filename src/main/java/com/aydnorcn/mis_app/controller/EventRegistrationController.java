package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event_registration.EventRegistrationResponse;
import com.aydnorcn.mis_app.entity.EventRegistration;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.EventRegistrationService;
import com.aydnorcn.mis_app.utils.params.EventRegistrationParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
@Tag(name = "Event Registration Controller")
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @Operation(
            summary = "Retrieve event registration by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event registration found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventRegistrationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Event registration not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<EventRegistrationResponse>> getEventRegistrationById(@PathVariable String id) {
        EventRegistration eventRegistration = eventRegistrationService.getEventRegistrationById(id);
        return ResponseEntity
                .ok(new APIResponse<>(true, "Event registration retrieved successfully", new EventRegistrationResponse(eventRegistration)));
    }

    @Operation(
            summary = "Retrieve event registrations by filtering and pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event registrations retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (event, user) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY),
            @Parameter(name = "sort-by", description = "Sort by", in = ParameterIn.QUERY),
            @Parameter(name = "sort-order", description = "Sort order", in = ParameterIn.QUERY),
            @Parameter(name = "event-id", description = "Event id", in = ParameterIn.QUERY),
            @Parameter(name = "user-id", description = "User id", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "Status", in = ParameterIn.QUERY),
    })
    @GetMapping
    public ResponseEntity<APIResponse<PageResponseDto<EventRegistrationResponse>>> getEventRegistrations(@RequestParam(required = false) Map<String, Object> searchParams) {
        EventRegistrationParams params = new EventRegistrationParams(searchParams);

        PageResponseDto<EventRegistration> eventRegistrations = eventRegistrationService.getEventRegistrations(params);
        List<EventRegistrationResponse> eventRegistrationResponses = eventRegistrations.getContent()
                .stream()
                .map(EventRegistrationResponse::new)
                .toList();

        return ResponseEntity.ok(
                new APIResponse<>(true, "Event registrations retrieved successfully",
                        new PageResponseDto<>(eventRegistrationResponses, eventRegistrations.getPageNo(), eventRegistrations.getPageSize(), eventRegistrations.getTotalElements(), eventRegistrations.getTotalPages())
                ));
    }

    @Operation(
            summary = "Save event registration"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event registration saved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventRegistrationResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Bad request | If event registration already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given event id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping
    public ResponseEntity<APIResponse<EventRegistrationResponse>> saveEventRegistration(@RequestParam String eventId) {
        EventRegistration eventRegistration = eventRegistrationService.saveEventRegistration(eventId);
        return ResponseEntity
                .ok(new APIResponse<>(true, "Event registration saved successfully", new EventRegistrationResponse(eventRegistration)));
    }

    @Operation(
            summary = "Delete event registration by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Event registration deleted"),
                    @ApiResponse(responseCode = "404", description = "Event registration not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventRegistration(@PathVariable String id) {
        eventRegistrationService.deleteEventRegistration(id);
        return ResponseEntity.noContent().build();
    }
}
