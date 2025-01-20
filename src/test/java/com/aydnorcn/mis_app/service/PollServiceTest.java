package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.PollRepository;
import com.aydnorcn.mis_app.utils.PollType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PollService pollService;

    @Test
    void getPollById_ReturnPoll_WhenPollIdExists(){
        String pollId = "1";
        Poll poll = new Poll();
        poll.setId(pollId);
        poll.setTitle("Poll Title");
        poll.setDescription("Poll Description");

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        Poll result = pollService.getPollById(pollId);

        assertEquals(poll, result);
        assertEquals(poll.getTitle(), result.getTitle());
        assertEquals(poll.getDescription(), result.getDescription());
    }

    @Test
    void getPollById_ThrowsResourceNotFoundException_WhenPollIdDoesNotExists(){
        String pollId = "1";

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pollService.getPollById(pollId));
    }

    @Test
    void createPoll_SavesAndReturnsPoll_WhenRequestIsValid() {
        CreatePollRequest request = new CreatePollRequest("Poll Title", "Poll Description",PollType.SINGLE_CHOICE,
                List.of("Choice1", "Choice2", "Choice3"), null);


        when(pollRepository.saveAndFlush(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Poll result = pollService.createPoll(request);

        assertNotNull(result);
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getType(), result.getType());
        assertEquals(request.getChoices().size(), result.getOptions().size());
        assertEquals(1, result.getMaxVoteCount());
    }

    @Test
    void updatePoll_SavesAndReturnsPoll_WhenRequestIsValid() {
        String pollId = "1";
        CreatePollRequest request = new CreatePollRequest("Poll Title", "Poll Description",PollType.SINGLE_CHOICE,
                List.of("Choice1", "Choice2", "Choice3"), null);

        Poll poll = new Poll();
        poll.setId(pollId);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollRepository.saveAndFlush(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Poll result = pollService.updatePoll(pollId, request);

        assertNotNull(result);
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getType(), result.getType());
        assertEquals(request.getChoices().size(), result.getOptions().size());
        assertEquals(1, result.getMaxVoteCount());
    }

    @Test
    void updatePoll_ThrowsResourceNotFoundException_WhenPollIdDoesNotExists() {
        String pollId = "1";
        CreatePollRequest request = new CreatePollRequest("Poll Title", "Poll Description",PollType.SINGLE_CHOICE,
                List.of("Choice1", "Choice2", "Choice3"), null);

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pollService.updatePoll(pollId, request));
    }

    @Test
    void patchPoll_SavesAndReturnPoll_WhenRequestIsValid() {
        String pollId = "1";
        PatchPollRequest request = new PatchPollRequest(null, "Patched Description", null, null);

        Poll poll = new Poll();
        poll.setId(pollId);
        poll.setOptions(List.of(new Option(), new Option()));
        poll.setTitle("Poll Title");
        poll.setDescription("Poll Description");
        poll.setType(PollType.MULTIPLE_CHOICE);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Poll result = pollService.patchPoll(pollId, request);

        assertNotNull(result);
        assertEquals("Poll Title", result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(PollType.MULTIPLE_CHOICE, result.getType());
        assertEquals(2, result.getOptions().size());
    }

    @Test
    void patchPoll_ThrowsResourceNotFoundException_WhenPollIdDoesNotExists() {
        String pollId = "1";
        PatchPollRequest request = new PatchPollRequest("Patched Title", "Patched Description", null, null);

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pollService.patchPoll(pollId, request));
    }

    @Test
    void deletePoll_DeletesPoll_WhenPollIdExists() {
        String pollId = "1";
        Poll poll = new Poll();
        poll.setId(pollId);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));

        pollService.deletePoll(pollId);

        verify(pollRepository).delete(poll);
    }

    @Test
    void deletePoll_ThrowsResourceNotFoundException_WhenPollIdDoesNotExists() {
        String pollId = "1";

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pollService.deletePoll(pollId));
    }
}
