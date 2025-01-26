package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.strategy.CreateVoteStrategy;
import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.VoteRepository;
import com.aydnorcn.mis_app.utils.PollType;
import com.aydnorcn.mis_app.utils.params.VoteParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserService userService;

    @Mock
    private Map<PollType, CreateVoteStrategy> strategyMap;

    @Mock
    private UserContextService userContextService;

    @Mock
    private OptionService optionService;

    @Mock
    private PollService pollService;

    @InjectMocks
    private VoteService voteService;

    private User user;
    private Poll poll;
    private Option option;
    private Vote expectedVote;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-id");

        poll = new Poll();
        poll.setActive(true);
        poll.setType(PollType.SINGLE_CHOICE);

        option = new Option();
        option.setId("option-id");
        option.setPoll(poll);

        expectedVote = new Vote();
        expectedVote.setUser(user);
        expectedVote.setOption(option);
    }

    @Test
    void getVoteById_ShouldReturnVote_WhenVoteExists() {
        String voteId = "vote-id";
        Vote vote = new Vote();
        vote.setId(voteId);
        vote.setUser(user);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));

        Vote result = voteService.getVoteById(voteId);

        assertNotNull(result);
        assertEquals(voteId, result.getId());
        assertEquals(user, result.getUser());
    }

    @Test
    void getVoteById_ShouldThrowResourceNotFoundException_WhenVoteDoesNotExist() {
        String voteId = "vote-id";

        when(voteRepository.findById(voteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> voteService.getVoteById(voteId));
    }

    @Test
    void getVotes_ShouldReturnPageResponseDto_WhenParamsAreValid() {
        String pollId = "1";
        String userId = "2";
        String optionId = "3";

        VoteParams params = new VoteParams();
        params.setPollId(pollId);
        params.setUserId(userId);
        params.setOptionId(optionId);

        Page<Vote> page = new PageImpl<>(List.of(new Vote()));

        when(userService.getUserById(userId)).thenReturn(new User());
        when(optionService.getOptionById(optionId)).thenReturn(new Option());
        when(pollService.getPollById(pollId)).thenReturn(new Poll());
        when(voteRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        PageResponseDto<Vote> result = voteService.getVotes(params);

        assertEquals(page.getContent(), result.getContent());
        assertEquals(page.getTotalElements(), result.getTotalElements());
    }

    @Test
    void createVote_ShouldCreateSingleChoiceVote_WhenPollTypeIsSingleChoice(){
        VoteRequest request = new VoteRequest();
        request.setOptionId(option.getId());

        CreateVoteStrategy strategy = mock(CreateVoteStrategy.class);

        when(optionService.getOptionById(option.getId())).thenReturn(option);
        when(strategyMap.get(PollType.SINGLE_CHOICE)).thenReturn(strategy);
        when(strategy.createVote(option)).thenReturn(expectedVote);

        Vote result = voteService.createVote(request);

        assertNotNull(result);
        assertEquals(option, result.getOption());
    }

    @Test
    void createVote_ShouldCreateMultipleChoiceVote_WhenPollTypeIsSingleChoice(){
        VoteRequest request = new VoteRequest();
        request.setOptionId(option.getId());
        poll.setType(PollType.MULTIPLE_CHOICE);
        poll.setMaxVoteCount(3);

        CreateVoteStrategy strategy = mock(CreateVoteStrategy.class);
        when(optionService.getOptionById(option.getId())).thenReturn(option);
        when(strategyMap.get(PollType.MULTIPLE_CHOICE)).thenReturn(strategy);
        when(strategy.createVote(option)).thenReturn(expectedVote);


        Vote result = voteService.createVote(request);

        assertNotNull(result);
        assertEquals(option, result.getOption());
    }

    @Test
    void createVote_ShouldThrowAPIException_WhenPollIsNotActive() {
        VoteRequest request = new VoteRequest();
        request.setOptionId("1");

        poll.setActive(false);

        when(optionService.getOptionById(anyString())).thenReturn(option);

        assertThrows(APIException.class, () -> voteService.createVote(request));
    }

    @Test
    void deleteVote_ShouldDeleteVote_WhenVoteExists() {
        String voteId = "vote123";
        Vote vote = new Vote();
        vote.setId(voteId);
        vote.setUser(user);

        when(voteRepository.findById(voteId)).thenReturn(Optional.of(vote));

        voteService.deleteVote(voteId);

        verify(voteRepository).delete(vote);
    }

    @Test
    void deleteVote_ShouldThrowResourceNotFoundException_WhenVoteDoesNotExist() {
        String voteId = "1";

        when(voteRepository.findById(voteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> voteService.deleteVote(voteId));
    }

    @Test
    void isAuthenticatedUserOwnerOfVote_ShouldReturnTrue_WhenUserIsOwner() {
        when(voteRepository.findById(expectedVote.getId())).thenReturn(Optional.of(expectedVote));
        when(userContextService.getCurrentAuthenticatedUser()).thenReturn(user);

        boolean result = voteService.isAuthenticatedUserOwnerOfVote(expectedVote.getId());

        assertTrue(result);
    }

    @Test
    void isAuthenticatedUserOwnerOfVote_ShouldReturnFalse_WhenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setId("another-id");
        expectedVote.setUser(anotherUser);

        when(voteRepository.findById(expectedVote.getId())).thenReturn(Optional.of(expectedVote));
        when(userContextService.getCurrentAuthenticatedUser()).thenReturn(user);

        boolean result = voteService.isAuthenticatedUserOwnerOfVote(expectedVote.getId());

        assertFalse(result);
    }
}