package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.EventResponse;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.EventService;
import com.aydnorcn.mis_app.utils.params.EventParams;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event Controller")
public class EventController {

    private final EventService eventService;

    @Operation(
            summary = "Retrieve event by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Event not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable String eventId) {
        Event event = eventService.getEventById(eventId);

        return ResponseEntity.ok(new EventResponse(event));
    }

    @Operation(
            summary = "Retrieve events by filtering and pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Events retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (user) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY),
            @Parameter(name = "sort-by", description = "Sort by", in = ParameterIn.QUERY),
            @Parameter(name = "sort-order", description = "Sort order", in = ParameterIn.QUERY),
            @Parameter(name = "location", description = "Location of event", in = ParameterIn.QUERY),
            @Parameter(name = "date", description = "Date of event", in = ParameterIn.QUERY),
            @Parameter(name = "start-after", description = "Event(s) started after", in = ParameterIn.QUERY),
            @Parameter(name = "end-before", description = "Event(s) finished before", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "Status of event", in = ParameterIn.QUERY),
            @Parameter(name = "created-by", description = "Event(s) created by", in = ParameterIn.QUERY),

    })
    @GetMapping
    public ResponseEntity<PageResponseDto<EventResponse>> getEvents(@RequestParam(required = false) Map<String, Object> searchParams) {
        EventParams params = new EventParams(searchParams);

        PageResponseDto<Event> events = eventService.getEvents(params);
        List<EventResponse> eventResponses = events.getContent().stream().map(EventResponse::new).toList();

        return ResponseEntity.ok(
                new PageResponseDto<>(eventResponses, events.getPageNo(), events.getPageSize(), events.getTotalElements(), events.getTotalPages())
        );
    }

    @Operation(
            summary = "Create a new event"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Event created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, organizator or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<EventResponse> createEvent(@Validated @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new EventResponse(event));
    }

    @Operation(
            summary = "Update all required parts of event"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, organizator or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable String eventId, @Validated @RequestBody CreateEventRequest request) {
        Event event = eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(new EventResponse(event));
    }

    @Operation(
            summary = "Update partial parts of event"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, organizator or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<EventResponse> patchEvent(@PathVariable String eventId, @Validated @RequestBody PatchEventRequest request) {
        Event event = eventService.patchEvent(eventId, request);

        return ResponseEntity.ok(new EventResponse(event));
    }

    @Operation(
            summary = "Delete event by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Event deleted"),
                    @ApiResponse(responseCode = "404", description = "Event not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, organizator or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<Void> deleteEvent(@PathVariable String eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}