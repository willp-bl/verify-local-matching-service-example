package uk.gov.ida.verifylocalmatchingserviceexample.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingServiceRequestDto;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.NationalInsuranceNumberDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.dataaccess.VerifiedPidDAO;
import uk.gov.ida.verifylocalmatchingserviceexample.model.Person;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.Cycle3AttributesDtoBuilder.aCycle3AttributesDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.aPerson;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.PersonBuilder.anyPerson;

public class Cycle3MatchingServiceTest {
    private Cycle3MatchingService cycle3MatchingService;
    private MatchingServiceRequestDto matchingServiceRequestDto;
    private List<Person> cycle1MatchingUsers;
    private NationalInsuranceNumberDAO nationalInsuranceNumberDAO = mock(NationalInsuranceNumberDAO.class);
    private VerifiedPidDAO verifiedPidDAO = mock(VerifiedPidDAO.class);

    @Before
    public void setUp() {
        cycle3MatchingService = new Cycle3MatchingService(nationalInsuranceNumberDAO, verifiedPidDAO);
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenCycle3AttributeIsNotPresent() {
        assertTrue(cycle3MatchingService.matchUser(aMatchingServiceRequestDtoBuilder().build(), cycle1MatchingUsers).isEmpty());
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenCycle3AttributeIsEmpty() {
        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
            .withCycle3AttributesDto(aCycle3AttributesDtoBuilder().build()).build();

        assertTrue(cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers).isEmpty());
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenCycle3AttributeDoesNotHaveNationalInsuranceNumber() {
        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withAttribute("not-insurance-number", "test-value").build()).build();

        assertTrue(cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers).isEmpty());
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenCycle3AttributeDoesNotHaveNationalInsuranceNumberValue() {
        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber(null).build()).build();

        assertTrue(cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers).isEmpty());
    }

    @Test
    public void shouldReturnMatchingUserIdsWhenExactlyOneUserMatchesOnCycle3Attribute() {
        cycle1MatchingUsers = singletonList(anyPerson());
        when(nationalInsuranceNumberDAO.getMatchingUserIds(any(), any())).thenReturn(singletonList(1));

        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber("matching-insurance-number").build()).build();

        assertEquals(cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers), singletonList(1));
    }

    @Test
    public void shouldCheckCycle3AttributeForAllCycle1MatchingUsers() {
        cycle1MatchingUsers = Arrays.asList(aPerson().withId(1).build(), aPerson().withId(2).build());
        String nationalInsuranceNumber = "matching-insurance-number";
        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber(nationalInsuranceNumber).build()).build();

        cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers);

        verify(nationalInsuranceNumberDAO, times(1)).getMatchingUserIds(Arrays.asList(1, 2), nationalInsuranceNumber);
    }

    @Test
    public void shouldReturnAllMatchingUserIdsWhenMultipleUsersMatchOnCycle3Attribute() {
        cycle1MatchingUsers = singletonList(anyPerson());
        when(nationalInsuranceNumberDAO.getMatchingUserIds(any(), any())).thenReturn(Arrays.asList(1, 2));

        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber("matching-insurance-number").build()).build();

        assertEquals(cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers), Arrays.asList(1, 2));
    }

    @Test
    public void shouldReturnEmptyListOfMatchingUsersWhenNoUserMatchesOnCycle3Attribute() {
        cycle1MatchingUsers = singletonList(anyPerson());
        when(nationalInsuranceNumberDAO.getMatchingUserIds(any(), any())).thenReturn(EMPTY_LIST);

        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber("matching-insurance-number").build()).build();

        assertTrue(cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers).isEmpty());
    }

    @Test
    public void shouldSavePidWhenThereIsMatchOnExactlyOneUser() {
        cycle1MatchingUsers = singletonList(anyPerson());
        when(nationalInsuranceNumberDAO.getMatchingUserIds(any(), any())).thenReturn(singletonList(1));

        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber("matching-insurance-number").build()).build();

        cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers);

        verify(verifiedPidDAO, times(1)).save(matchingServiceRequestDto.getHashedPid(), 1);
    }

    @Test
    public void shouldNotSavePidWhenThereIsNoUserMatch() {
        cycle1MatchingUsers = singletonList(anyPerson());
        when(nationalInsuranceNumberDAO.getMatchingUserIds(any(), any())).thenReturn(emptyList());

        matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder().withCycle3AttributesDto(
            aCycle3AttributesDtoBuilder().withNationalInsuranceNumber("matching-insurance-number").build()).build();

        cycle3MatchingService.matchUser(matchingServiceRequestDto, cycle1MatchingUsers);

        verify(verifiedPidDAO, never()).save(any(), any());
    }
}