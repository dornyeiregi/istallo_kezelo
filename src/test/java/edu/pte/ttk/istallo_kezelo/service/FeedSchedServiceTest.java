package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedChangeRequestRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class FeedSchedServiceTest {

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private FeedSchedChangeRequestRepository feedSchedChangeRequestRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private FeedSchedService feedSchedService;

    @Test
    void createFeedSched_withoutLinks_savesFeedSched() {
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedEvening(true);
        dto.setDescription("Evening ration");
        when(feedSchedRepository.save(any(FeedSched.class))).thenAnswer(invocation -> {
            FeedSched feedSched = invocation.getArgument(0);
            if (feedSched.getId() == null) {
                feedSched.setId(7L);
            }
            return feedSched;
        });

        FeedSched result = feedSchedService.createFeedSched(dto);

        assertEquals(7L, result.getId());
        assertEquals(true, result.isFeedEvening());
        assertEquals("Evening ration", result.getDescription());
    }

    @Test
    void createFeedSched_withHorseAndItems_addsLinksAndSyncsStorage() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Item item = ServiceTestSupport.item(5L, "Hay");
        FeedSched saved = new FeedSched();
        saved.setId(7L);
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedMorning(true);
        dto.setHorseIds(List.of(2L));
        dto.setItems(List.of(new FeedSchedItemAmountDTO(5L, 1.5)));

        when(feedSchedRepository.save(any(FeedSched.class))).thenAnswer(invocation -> {
            FeedSched feedSched = invocation.getArgument(0);
            if (feedSched.getId() == null) {
                feedSched.setId(7L);
                return saved;
            }
            return feedSched;
        });
        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(saved));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        FeedSched result = feedSchedService.createFeedSched(dto);

        assertSame(saved, result);
        assertEquals(1, saved.getHorseFeedScheds().size());
        assertEquals(1, saved.getFeedSchedItems().size());
        verify(storageService).syncAmountInUseForItem(5L);
    }

    @Test
    void createFeedSched_throwsWhenNoFeedingTimeSelected() {
        FeedSchedDTO dto = new FeedSchedDTO();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> feedSchedService.createFeedSched(dto));

        assertEquals("Etetési időpont megadása kötelező.", exception.getMessage());
    }

    @Test
    void createFeedSched_throwsWhenItemIdsProvidedWithoutAmounts() {
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedMorning(true);
        dto.setItemIds(List.of(5L));
        when(feedSchedRepository.save(any(FeedSched.class))).thenAnswer(invocation -> {
            FeedSched feedSched = invocation.getArgument(0);
            feedSched.setId(7L);
            return feedSched;
        });

        RuntimeException exception = assertThrows(RuntimeException.class, () -> feedSchedService.createFeedSched(dto));

        assertEquals("Mennyiség kötelező.", exception.getMessage());
    }

    @Test
    void createFeedSchedRequest_andChangeRequestStoreRequestedData() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Item item = ServiceTestSupport.item(5L, "Hay");
        FeedSched savedFeedSched = new FeedSched();
        savedFeedSched.setId(7L);
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedMorning(true);
        dto.setDescription("Morning");
        dto.setHorseIds(List.of(2L));
        dto.setItems(List.of(new FeedSchedItemAmountDTO(5L, 1.5)));

        when(feedSchedRepository.save(any(FeedSched.class))).thenAnswer(invocation -> {
            FeedSched feedSched = invocation.getArgument(0);
            if (feedSched.getId() == null) {
                feedSched.setId(7L);
            }
            return savedFeedSched;
        });
        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(savedFeedSched));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(feedSchedChangeRequestRepository.save(any(FeedSchedChangeRequest.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        FeedSchedChangeRequest result = feedSchedService.createFeedSchedRequest(dto, auth);

        assertSame(savedFeedSched, result.getFeedSched());
        assertEquals("2", result.getRequestedHorseIds());
        assertEquals("5:1.5", result.getRequestedItemAmounts());
        verify(mailService).sendToAdmins(any(), any());
    }

    @Test
    void queryMethods_delegateToRepository() {
        FeedSched feedSched = ServiceTestSupport.feedSched(7L, "Morning");
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);

        when(feedSchedRepository.findAll()).thenReturn(List.of(feedSched));
        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(feedSched));
        when(feedSchedRepository.findByHorseFeedScheds_Horse_Id(2L)).thenReturn(List.of(feedSched));
        when(feedSchedRepository.findByHorseFeedScheds_Horse_HorseName("Csillag")).thenReturn(List.of(feedSched));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(feedSchedChangeRequestRepository.findAllByRequestedBy_IdOrderByRequestedAtDesc(1L)).thenReturn(List.of());
        when(feedSchedChangeRequestRepository.findAllByOrderByRequestedAtDesc()).thenReturn(List.of());

        assertEquals(List.of(feedSched), feedSchedService.getAllFeedScheds());
        assertSame(feedSched, feedSchedService.getFeedSchedById(7L));
        assertEquals(List.of(feedSched), feedSchedService.getFeedSchedByHorseId(2L));
        assertEquals(List.of(feedSched), feedSchedService.getFeedSchedByHorseName("Csillag"));
        assertEquals(List.of(), feedSchedService.getAllChangeRequests());
        assertEquals(List.of(), feedSchedService.getMyChangeRequests(auth));
        assertEquals(List.of(), feedSchedService.getMyChangeRequests(null));
    }

    @Test
    void updateFeedSched_forOwnerCreatesChangeRequest() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        FeedSched existing = ServiceTestSupport.feedSched(7L, "Morning");
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedMorning(true);

        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(feedSchedChangeRequestRepository.save(any(FeedSchedChangeRequest.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = feedSchedService.updateFeedSched(7L, dto, auth);

        assertEquals(false, result);
        verify(mailService).sendToAdmins(any(), any());
    }

    @Test
    void updateFeedSched_forAdminAppliesChangesAndSyncsAffectedItems() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse currentHorse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Horse newHorse = ServiceTestSupport.horse(4L, "Villam", owner, ServiceTestSupport.stable(3L, "Main"));
        Item oldItem = ServiceTestSupport.item(5L, "Hay");
        Item newItem = ServiceTestSupport.item(6L, "Oats");
        FeedSched existing = ServiceTestSupport.feedSched(7L, "Morning");
        existing.getHorseFeedScheds().add(ServiceTestSupport.horseFeedSched(8L, currentHorse, existing));
        existing.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(9L, existing, oldItem, 1.0));
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedEvening(true);
        dto.setDescription("Updated");
        dto.setHorseIds(List.of(4L));
        dto.setItems(List.of(new FeedSchedItemAmountDTO(6L, 2.0)));

        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(horseRepository.findById(4L)).thenReturn(Optional.of(newHorse));
        when(itemRepository.findById(6L)).thenReturn(Optional.of(newItem));
        when(feedSchedRepository.save(existing)).thenReturn(existing);

        boolean result = feedSchedService.updateFeedSched(7L, dto, auth);

        assertEquals(true, result);
        assertEquals("Updated", existing.getDescription());
        assertEquals(1, existing.getHorseFeedScheds().size());
        assertEquals(1, existing.getFeedSchedItems().size());
        verify(storageService).syncAmountInUseForItem(5L);
        verify(storageService).syncAmountInUseForItem(6L);
    }

    @Test
    void deleteFeedSched_clearsLinksAndSyncsStorage() {
        FeedSched existing = ServiceTestSupport.feedSched(7L, "Morning");
        Item item = ServiceTestSupport.item(5L, "Hay");
        existing.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(9L, existing, item, 1.0));

        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(existing));

        feedSchedService.deleteFeedSched(7L);

        verify(feedSchedRepository).delete(existing);
        verify(storageService).syncAmountInUseForItem(5L);
    }

    @Test
    void addHorseAndItemToFeedSched_applyValidationsAndSyncStorage() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Item hay = ServiceTestSupport.item(5L, "Hay");
        FeedSched existing = ServiceTestSupport.feedSched(7L, "Morning");
        existing.getFeedSchedItems().add(ServiceTestSupport.feedSchedItem(9L, existing, hay, 1.0));

        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(hay));
        when(feedSchedRepository.save(existing)).thenReturn(existing);

        feedSchedService.addHorseToFeedSched(7L, 2L);
        feedSchedService.addItemToFeedSched(7L, 5L, 2.0);

        verify(storageService, times(2)).syncAmountInUseForItem(5L);
    }

    @Test
    void createChangeRequest_throwsWhenBeddingItemUsedForFeed() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        FeedSched existing = ServiceTestSupport.feedSched(7L, "Morning");
        Item bedding = ServiceTestSupport.item(5L, "Straw");
        bedding.setItemType(ItemType.BEDDING);
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedMorning(true);
        dto.setItemIds(List.of(5L));

        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(bedding));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> feedSchedService.createChangeRequest(7L, dto, auth));

        assertEquals("Etetéshez alom típusú tétel nem választható.", exception.getMessage());
    }

    @Test
    void approveAndRejectChangeRequest_manageRequestLifecycle() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Item item = ServiceTestSupport.item(5L, "Hay");
        FeedSched existing = ServiceTestSupport.feedSched(7L, "Morning");
        FeedSchedChangeRequest request = new FeedSchedChangeRequest();
        request.setId(11L);
        request.setFeedSched(existing);
        request.setRequestedMorning(true);
        request.setRequestedDescription("Approved");
        request.setRequestedHorseIds("2");
        request.setRequestedItemAmounts("5:1.5");

        when(feedSchedChangeRequestRepository.findById(11L)).thenReturn(Optional.of(request));
        when(feedSchedRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(feedSchedRepository.save(existing)).thenReturn(existing);

        FeedSched approved = feedSchedService.approveChangeRequest(11L);

        assertSame(existing, approved);
        verify(feedSchedChangeRequestRepository).delete(request);

        FeedSchedChangeRequest reject = new FeedSchedChangeRequest();
        reject.setId(12L);
        when(feedSchedChangeRequestRepository.findById(12L)).thenReturn(Optional.of(reject));

        feedSchedService.rejectChangeRequest(12L);

        verify(feedSchedChangeRequestRepository).delete(reject);
    }

    @Test
    void parseHelpers_returnStructuredValues() {
        assertEquals(List.of(1L, 2L), feedSchedService.parseIds("1, 2"));
        assertEquals(List.of(), feedSchedService.parseIds(" "));
        List<FeedSchedItemAmountDTO> items = feedSchedService.parseItemAmounts("5:1.5, 6:");
        assertEquals(2, items.size());
        assertEquals(5L, items.get(0).getItemId());
        assertEquals(1.5, items.get(0).getAmount());
        assertEquals(6L, items.get(1).getItemId());
        assertSame(null, items.get(1).getAmount());
    }
}
