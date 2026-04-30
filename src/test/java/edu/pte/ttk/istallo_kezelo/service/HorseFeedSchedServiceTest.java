package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HorseFeedSchedServiceTest {

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private HorseFeedSchedRepository horseFeedSchedRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private HorseFeedSchedService horseFeedSchedService;

    @Test
    void addHorseToFeedSched_savesLinkAndSyncsStorage() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        FeedSched feedSched = ServiceTestSupport.feedSched(4L, "Morning");
        Item item = ServiceTestSupport.item(5L, "Hay");
        FeedSchedItem itemLink = ServiceTestSupport.feedSchedItem(6L, feedSched, item, 1.5);
        feedSched.getFeedSchedItems().add(itemLink);
        HorseFeedSched savedLink = ServiceTestSupport.horseFeedSched(7L, horse, feedSched);

        when(feedSchedRepository.findById(4L)).thenReturn(Optional.of(feedSched));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(horseFeedSchedRepository.existsByFeedSchedAndHorse(feedSched, horse)).thenReturn(false);
        when(horseFeedSchedRepository.save(org.mockito.ArgumentMatchers.any(HorseFeedSched.class))).thenReturn(savedLink);

        HorseFeedSched result = horseFeedSchedService.addHorseToFeedSched(4L, 2L);

        assertEquals(7L, result.getId());
        verify(storageService).syncAmountInUseForItem(5L);
    }

    @Test
    void addHorseToFeedSched_throwsWhenHorseAlreadyLinked() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        FeedSched feedSched = ServiceTestSupport.feedSched(4L, "Morning");

        when(feedSchedRepository.findById(4L)).thenReturn(Optional.of(feedSched));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(horseFeedSchedRepository.existsByFeedSchedAndHorse(feedSched, horse)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseFeedSchedService.addHorseToFeedSched(4L, 2L));

        assertEquals("A ló már hozzá van adva ehhez az etetési naplóhoz", exception.getMessage());
        verifyNoInteractions(storageService);
    }

    @Test
    void getters_delegateToRepository() {
        HorseFeedSched link = ServiceTestSupport.horseFeedSched(
            7L,
            ServiceTestSupport.horse(2L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), ServiceTestSupport.stable(3L, "Main")),
            ServiceTestSupport.feedSched(4L, "Morning")
        );

        when(horseFeedSchedRepository.findAll()).thenReturn(List.of(link));
        when(horseFeedSchedRepository.findById(7L)).thenReturn(Optional.of(link));
        when(horseFeedSchedRepository.findByFeedSched_Id(4L)).thenReturn(List.of(link));
        when(horseFeedSchedRepository.findByHorse_Id(2L)).thenReturn(List.of(link));

        assertEquals(List.of(link), horseFeedSchedService.getAllHorseFeedScheds());
        assertSame(link, horseFeedSchedService.getHorseFeedSchedById(7L));
        assertEquals(List.of(link), horseFeedSchedService.getHorsesForFeedSChed(4L));
        assertEquals(List.of(link), horseFeedSchedService.getFeedSchedsForHorse(2L));
    }

    @Test
    void removeHorseFromFeedSched_deletesLinkAndSyncsStorage() {
        FeedSched feedSched = ServiceTestSupport.feedSched(4L, "Morning");
        Item item = ServiceTestSupport.item(5L, "Hay");
        feedSched.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(6L, feedSched, item, 1.5));
        when(feedSchedRepository.findById(4L)).thenReturn(Optional.of(feedSched));

        horseFeedSchedService.removeHorseFromFeedSched(4L, 2L);

        verify(horseFeedSchedRepository).deleteByHorse_IdAndFeedSched_Id(2L, 4L);
        verify(storageService).syncAmountInUseForItem(5L);
    }

    @Test
    void removeAllFeedSchedsForHorse_deletesLinksAndSyncsDistinctItems() {
        FeedSched first = ServiceTestSupport.feedSched(4L, "Morning");
        FeedSched second = ServiceTestSupport.feedSched(8L, "Evening");
        Item hay = ServiceTestSupport.item(5L, "Hay");
        Item oats = ServiceTestSupport.item(9L, "Oats");
        first.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(6L, first, hay, 1.5));
        second.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(10L, second, hay, 2.0));
        second.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(11L, second, oats, 2.0));
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), ServiceTestSupport.stable(3L, "Main"));
        HorseFeedSched firstLink = ServiceTestSupport.horseFeedSched(7L, horse, first);
        HorseFeedSched secondLink = ServiceTestSupport.horseFeedSched(12L, horse, second);
        when(horseFeedSchedRepository.findByHorse_Id(2L)).thenReturn(List.of(firstLink, secondLink));

        horseFeedSchedService.removeAllFeedSchedsForHorse(2L);

        verify(horseFeedSchedRepository).deleteByHorse_Id(2L);
        verify(storageService).syncAmountInUseForItem(5L);
        verify(storageService).syncAmountInUseForItem(9L);
    }
}
