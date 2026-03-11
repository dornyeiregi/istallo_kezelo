package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedChangeRequestRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.springframework.security.core.Authentication;


@Service
public class FeedSchedService {

    private final FeedSchedRepository feedSchedRepository;
    private final FeedSchedChangeRequestRepository feedSchedChangeRequestRepository;
    private final HorseRepository horseRepository;
    private final ItemRepository itemRepository;
    private final StorageService storageService;
    private final UserRepository userRepository;

    public FeedSchedService(FeedSchedRepository feedSchedRepository,
                            FeedSchedChangeRequestRepository feedSchedChangeRequestRepository,
                            HorseRepository horseRepository,
                            ItemRepository itemRepository,
                            StorageService storageService,
                            UserRepository userRepository) {
        this.feedSchedRepository = feedSchedRepository;
        this.feedSchedChangeRequestRepository = feedSchedChangeRequestRepository;
        this.horseRepository = horseRepository;
        this.itemRepository = itemRepository;
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    @Transactional
    public FeedSched createFeedSched(FeedSchedDTO dto) {
        FeedSched feedSched = new FeedSched();
        feedSched.setFeedTime(dto.getFeedTime());
        feedSched.setDescription(dto.getDescription());
        feedSched = feedSchedRepository.save(feedSched);
        if (dto.getHorseIds() != null) {
            for (Long horseId : dto.getHorseIds()) {
                addHorseToFeedSched(feedSched.getId(), horseId);
            }
        }
        if (dto.getItems() != null) {
            for (FeedSchedItemAmountDTO item : dto.getItems()) {
                addItemToFeedSched(feedSched.getId(), item.getItemId(), item.getAmount());
            }
        } else if (dto.getItemIds() != null) {
            for (Long itemId : dto.getItemIds()) {
                addItemToFeedSched(feedSched.getId(), itemId, 0.0);
            }
        }
        return feedSched;
    }

    public List<FeedSched> getAllFeedScheds(){
        return feedSchedRepository.findAll();
    }

    public FeedSched getFeedSchedById(Long id){
        return feedSchedRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
    }

    public List<FeedSched> getFeedSchedByHorseId(Long id){
        return feedSchedRepository.findByHorseFeedScheds_Horse_Id(id);
    }

    public List<FeedSched> getFeedSchedByHorseName(String horseName){
        return feedSchedRepository.findByHorseFeedScheds_Horse_HorseName(horseName);   
    }

    @Transactional
    public boolean updateFeedSched(Long id, FeedSchedDTO dto, Authentication auth){
        if (auth != null && hasRole(auth, "OWNER")) {
            createChangeRequest(id, dto, auth);
            return false;
        }
        applyFeedSchedUpdate(id, dto);
        return true;
    }

    @Transactional
    public FeedSchedChangeRequest createChangeRequest(Long feedSchedId, FeedSchedDTO dto, Authentication auth) {
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        User requester = userRepository.findByUsername(auth.getName());
        FeedSchedChangeRequest request = new FeedSchedChangeRequest();
        request.setFeedSched(feedSched);
        request.setRequestedBy(requester);
        request.setRequestedAt(java.time.LocalDateTime.now());
        request.setRequestedHorseIds(joinIds(dto.getHorseIds()));
        request.setRequestedItemIds(joinIds(dto.getItemIds()));
        return feedSchedChangeRequestRepository.save(request);
    }

    @Transactional
    public FeedSched applyFeedSchedUpdate(Long id, FeedSchedDTO dto){
        FeedSched existingFeedSched = feedSchedRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        List<Long> existingItemIds = existingFeedSched.getFeedSchedItems().stream()
                .map(link -> link.getItem().getId())
                .toList();
        if (dto.getFeedTime() != null) {
            existingFeedSched.setFeedTime(dto.getFeedTime());
        }
        if (dto.getDescription() != null) {
            existingFeedSched.setDescription(dto.getDescription());
        }
        if (dto.getHorseIds() != null) {
            existingFeedSched.getHorseFeedScheds().clear();
            for(Long horseId : dto.getHorseIds()){
                Horse horse = horseRepository.findById(horseId)
                    .orElseThrow(() -> new RuntimeException("Ló nem található."));
                HorseFeedSched link = new HorseFeedSched();
                link.setHorse(horse);
                link.setFeedSched(existingFeedSched);
                existingFeedSched.getHorseFeedScheds().add(link);
            }
        }
        if (dto.getItems() != null || dto.getItemIds() != null) {
            existingFeedSched.getFeedSchedItems().clear();
            if (dto.getItems() != null) {
                for (FeedSchedItemAmountDTO itemDto : dto.getItems()) {
                    if (itemDto.getAmount() == null) {
                        throw new RuntimeException("Mennyiség kötelező.");
                    }
                    Item item = itemRepository.findById(itemDto.getItemId())
                        .orElseThrow(() -> new RuntimeException("Takarmány nem található."));

                    FeedSchedItem link = new FeedSchedItem();
                    link.setItem(item);
                    link.setFeedSched(existingFeedSched);
                    link.setAmount(itemDto.getAmount());
                    existingFeedSched.getFeedSchedItems().add(link);
                }
            } else if (dto.getItemIds() != null) {
                for (Long itemId : dto.getItemIds()) {
                    Item item = itemRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Takarmány nem található."));
                    FeedSchedItem link = new FeedSchedItem();
                    link.setItem(item);
                    link.setFeedSched(existingFeedSched);
                    link.setAmount(0.0);
                    existingFeedSched.getFeedSchedItems().add(link);
                }
            }
        }
        FeedSched saved = feedSchedRepository.save(existingFeedSched);
        List<Long> newItemIds;
        if (dto.getItems() != null) {
            newItemIds = dto.getItems().stream().map(FeedSchedItemAmountDTO::getItemId).toList();
        } else {
            newItemIds = dto.getItemIds() != null ? dto.getItemIds() : existingItemIds;
        }
        java.util.Set<Long> affectedItemIds = new java.util.HashSet<>(existingItemIds);
        affectedItemIds.addAll(newItemIds);
        for (Long itemId : affectedItemIds) {
            storageService.syncAmountInUseForItem(itemId);
        }
        return saved;
    }

    @Transactional
    public void deleteFeedSched(Long id){
        FeedSched feedSched = feedSchedRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        List<Long> itemIds = feedSched.getFeedSchedItems().stream()
                .map(link -> link.getItem().getId())
                .toList();
        feedSched.getHorseFeedScheds().clear();
        feedSched.getFeedSchedItems().clear();
        feedSchedRepository.delete(feedSched);
        for (Long itemId : itemIds) {
            storageService.syncAmountInUseForItem(itemId);
        }
    }

    @Transactional
    public void addHorseToFeedSched(Long feedSchedId, Long horseId) {
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
        HorseFeedSched link = new HorseFeedSched();
        link.setHorse(horse);
        link.setFeedSched(feedSched);
        feedSched.getHorseFeedScheds().add(link);
        feedSchedRepository.save(feedSched);
        for (FeedSchedItem itemLink : feedSched.getFeedSchedItems()) {
            storageService.syncAmountInUseForItem(itemLink.getItem().getId());
        }
    }

    @Transactional
    public void addItemToFeedSched(Long feedSchedId, Long itemId, Double amount) {
        if (amount == null) {
            throw new RuntimeException("Mennyiség kötelező.");
        }
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Takarmány nem található."));
        FeedSchedItem link = new FeedSchedItem();
        link.setItem(item);
        link.setFeedSched(feedSched);
        link.setAmount(amount);
        feedSched.getFeedSchedItems().add(link);
        feedSchedRepository.save(feedSched);
        storageService.syncAmountInUseForItem(itemId);
    }

    public List<FeedSchedChangeRequest> getAllChangeRequests() {
        return feedSchedChangeRequestRepository.findAllByOrderByRequestedAtDesc();
    }

    @Transactional
    public FeedSched approveChangeRequest(Long requestId) {
        FeedSchedChangeRequest request = feedSchedChangeRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Kérés nem található."));
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setHorseIds(parseIds(request.getRequestedHorseIds()));
        dto.setItemIds(parseIds(request.getRequestedItemIds()));
        FeedSched updated = applyFeedSchedUpdate(request.getFeedSched().getId(), dto);
        feedSchedChangeRequestRepository.delete(request);
        return updated;
    }

    @Transactional
    public void rejectChangeRequest(Long requestId) {
        FeedSchedChangeRequest request = feedSchedChangeRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Kérés nem található."));
        feedSchedChangeRequestRepository.delete(request);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private String joinIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return ids.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
    }

    public List<Long> parseIds(String csv) {
        if (csv == null || csv.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(csv.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .map(Long::valueOf)
            .toList();
    }
}
