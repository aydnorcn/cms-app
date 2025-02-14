package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.assignment.CreateAssignmentRequest;
import com.aydnorcn.mis_app.dto.assignment.PatchAssignmentRequest;
import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.AssignmentFilter;
import com.aydnorcn.mis_app.repository.AssignmentRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.params.AssignmentParams;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EventService eventService;
    private final UserService userService;

    @Cacheable(value = "assignment", key = "#assignmentId")
    public Assignment getAssignmentById(String assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ASSIGNMENT_NOT_FOUND));
    }

    public PageResponseDto<Assignment> getAssignments(AssignmentParams params) {
        var assignedTo = (params.getCreatedBy() == null) ? null : userService.getUserById(params.getAssignedTo());
        var event = (params.getEventId() == null) ? null : eventService.getEventById(params.getEventId());
        var createdBy = (params.getCreatedBy() == null) ? null : userService.getUserById(params.getCreatedBy());

        Specification<Assignment> specification = AssignmentFilter.filter(assignedTo, event, params.getIsCompleted(), params.getMinPriority(), params.getMaxPriority(), params.getCreatedDateRangeParams().getCreatedAfter(), params.getCreatedDateRangeParams().getCreatedBefore(), createdBy);

        Page<Assignment> page = assignmentRepository.findAll(specification, PageRequest.of(params.getPageNo(), params.getPageSize(), params.getSort()));

        return new PageResponseDto<>(page);
    }

    public Assignment createAssignment(CreateAssignmentRequest request) {
        Assignment assignment = new Assignment();
        updateAssignmentFields(assignment, request);

        return assignmentRepository.save(assignment);
    }

    @CachePut(value = "assignment", key = "#assignmentId")
    public Assignment updateAssignment(String assignmentId, CreateAssignmentRequest request) {
        Assignment assignment = getAssignmentById(assignmentId);

        updateAssignmentFields(assignment, request);

        return assignmentRepository.save(assignment);
    }

    @CachePut(value = "assignment", key = "#assignmentId")
    public Assignment patchAssignment(String assignmentId, PatchAssignmentRequest request) {
        Assignment assignment = getAssignmentById(assignmentId);

        patchAssignmentFields(assignment, request);

        return assignmentRepository.save(assignment);
    }

    @CacheEvict(value = "assignment", key = "#assignmentId")
    public void deleteAssignment(String assignmentId) {
        Assignment assignment = getAssignmentById(assignmentId);
        assignmentRepository.delete(assignment);
    }

    private void updateAssignmentFields(Assignment assignment, CreateAssignmentRequest request) {
        Event event = eventService.getEventById(request.getEventId());
        List<User> assignedTo = request.getAssignedTo().stream().map(userService::getUserById).toList();

        assignment.setAssignedTo(new LinkedList<>(assignedTo));
        assignment.setEvent(event);
        assignment.setTitle(request.getTitle());
        assignment.setContent(request.getContent());
        assignment.setPriority(request.getPriority());
    }

    private void patchAssignmentFields(Assignment assignment, PatchAssignmentRequest request) {
        if (request.getAssignedTo() != null && !request.getAssignedTo().isEmpty()) {
            List<User> assignedTo = request.getAssignedTo().stream().map(userService::getUserById).toList();
            assignment.setAssignedTo(assignedTo);
        }
        if (request.getEventId() != null) {
            Event event = eventService.getEventById(request.getEventId());
            assignment.setEvent(event);
        }
        if (request.getTitle() != null) {
            assignment.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            assignment.setContent(request.getContent());
        }
        if (request.getPriority() != null) {
            assignment.setPriority(request.getPriority());
        }
    }
}