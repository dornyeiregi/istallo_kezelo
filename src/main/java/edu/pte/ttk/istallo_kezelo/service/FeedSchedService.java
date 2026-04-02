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
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedChangeRequestRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.springframework.security.core.Authentication;


/**
 * Etetési ütemtervek és módosítási kérelmek kezelésére szolgáló szolgáltatás.
 */
@Service
public class FeedSchedService {

    private final FeedSchedRepository feedSchedRepository;
    private final FeedSchedChangeRequestRepository feedSchedChangeRequestRepository;
    private final HorseRepository horseRepository;
    private final ItemRepository itemRepository;
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final MailService mailService;

    /**
     * Létrehozza a szolgáltatást a szükséges repository-kkal.
     *
     * @param feedSchedRepository              etetési ütemterv repository
     * @param feedSchedChangeRequestRepository változtatási kérelmek repository
     * @param horseRepository                  ló repository
     * @param itemRepository                   tétel repository
     * @param storageService                   tároló szolgáltatás
     * @param userRepository                   felhasználó repository
     * @param mailService                      e-mail szolgáltatás
     */
    public FeedSchedService(FeedSchedRepository feedSchedRepository,
                            FeedSchedChangeRequestRepository feedSchedChangeRequestRepository,
                            HorseRepository horseRepository,
                            ItemRepository itemRepository,
                            StorageService storageService,
                            UserRepository userRepository,
                            MailService mailService) {
        this.feedSchedRepository = feedSchedRepository;
        this.feedSchedChangeRequestRepository = feedSchedChangeRequestRepository;
        this.horseRepository = horseRepository;
        this.itemRepository = itemRepository;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * Etetési ütemterv létrehozása.
     *
     * @param dto ütemterv adatok
     * @return létrehozott ütemterv
     */
    @Transactional
    public FeedSched createFeedSched(FeedSchedDTO dto) {
        if (!isAnyTimeSelected(dto)) {
            throw new RuntimeException("Etetési időpont megadása kötelező.");
        }
        FeedSched feedSched = new FeedSched();
        feedSched.setFeedMorning(Boolean.TRUE.equals(dto.getFeedMorning()));
        feedSched.setFeedNoon(Boolean.TRUE.equals(dto.getFeedNoon()));
        feedSched.setFeedEvening(Boolean.TRUE.equals(dto.getFeedEvening()));
        feedSched.setDescription(dto.getDescription());
        feedSched = feedSchedRepository.save(feedSched);
        if (dto.getHorseIds() != null) {
            for (Long horseId : dto.getHorseIds()) {
                addHorseToFeedSched(feedSched.getId(), horseId);
            }
        }
        if (dto.getItems() != null) {
            for (FeedSchedItemAmountDTO item : dto.getItems()) {
                if (item.getAmount() == null) {
                    throw new RuntimeException("Mennyiség kötelező.");
                }
                addItemToFeedSched(feedSched.getId(), item.getItemId(), item.getAmount());
            }
        } else if (dto.getItemIds() != null) {
            throw new RuntimeException("Mennyiség kötelező.");
        }
        return feedSched;
    }

    /**
     * Etetési ütemterv létrehozás kérelemként (owner számára).
     *
     * @param dto  ütemterv adatok
     * @param auth hitelesítési adatok
     * @return mentett kérelem
     */
    @Transactional
    public FeedSchedChangeRequest createFeedSchedRequest(FeedSchedDTO dto, Authentication auth) {
        if (!isAnyTimeSelected(dto)) {
            throw new RuntimeException("Etetési időpont megadása kötelező.");
        }
        FeedSched feedSched = new FeedSched();
        feedSched.setFeedMorning(false);
        feedSched.setFeedNoon(false);
        feedSched.setFeedEvening(false);
        feedSched.setDescription(null);
        FeedSched created = feedSchedRepository.save(feedSched);
        return createChangeRequest(created.getId(), dto, auth);
    }

    /**
     * Összes etetési ütemterv lekérése.
     *
     * @return ütemtervek listája
     */
    public List<FeedSched> getAllFeedScheds(){
        return feedSchedRepository.findAll();
    }

    /**
     * Etetési ütemterv lekérése azonosító alapján.
     *
     * @param id ütemterv azonosító
     * @return ütemterv
     */
    public FeedSched getFeedSchedById(Long id){
        return feedSchedRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
    }

    /**
     * Etetési ütemtervek lekérése ló azonosító alapján.
     *
     * @param id ló azonosító
     * @return ütemtervek listája
     */
    public List<FeedSched> getFeedSchedByHorseId(Long id){
        return feedSchedRepository.findByHorseFeedScheds_Horse_Id(id);
    }

    /**
     * Etetési ütemtervek lekérése ló név alapján.
     *
     * @param horseName ló neve
     * @return ütemtervek listája
     */
    public List<FeedSched> getFeedSchedByHorseName(String horseName){
        return feedSchedRepository.findByHorseFeedScheds_Horse_HorseName(horseName);   
    }

    /**
     * Etetési ütemterv frissítése (owner esetén kérelemként).
     *
     * @param id   ütemterv azonosító
     * @param dto  módosítási adatok
     * @param auth hitelesítési adatok
     * @return igaz, ha közvetlenül alkalmazva lett
     */
    @Transactional
    public boolean updateFeedSched(Long id, FeedSchedDTO dto, Authentication auth){
        if (auth != null && hasRole(auth, "OWNER")) {
            createChangeRequest(id, dto, auth);
            return false;
        }
        applyFeedSchedUpdate(id, dto);
        return true;
    }

    /**
     * Változtatási kérelem létrehozása meglévő ütemtervre.
     *
     * @param feedSchedId ütemterv azonosító
     * @param dto         módosítási adatok
     * @param auth        hitelesítési adatok
     * @return mentett kérelem
     */
    @Transactional
    public FeedSchedChangeRequest createChangeRequest(Long feedSchedId, FeedSchedDTO dto, Authentication auth) {
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        User requester = userRepository.findByUsername(auth.getName());
        FeedSchedChangeRequest request = new FeedSchedChangeRequest();
        request.setFeedSched(feedSched);
        request.setRequestedBy(requester);
        request.setRequestedAt(java.time.LocalDateTime.now());
        request.setRequestedMorning(dto.getFeedMorning());
        request.setRequestedNoon(dto.getFeedNoon());
        request.setRequestedEvening(dto.getFeedEvening());
        request.setRequestedDescription(dto.getDescription());
        request.setRequestedHorseIds(joinIds(dto.getHorseIds()));
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (FeedSchedItemAmountDTO item : dto.getItems()) {
                Item existing = itemRepository.findById(item.getItemId())
                    .orElseThrow(() -> new RuntimeException("Takarmány nem található."));
                ensureNotBedding(existing);
            }
            request.setRequestedItemAmounts(joinItemAmounts(dto.getItems()));
            request.setRequestedItemIds(joinIds(
                dto.getItems().stream().map(FeedSchedItemAmountDTO::getItemId).toList()
            ));
        } else {
            if (dto.getItemIds() != null) {
                for (Long itemId : dto.getItemIds()) {
                    Item existing = itemRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Takarmány nem található."));
                    ensureNotBedding(existing);
                }
            }
            request.setRequestedItemIds(joinIds(dto.getItemIds()));
        }
        FeedSchedChangeRequest saved = feedSchedChangeRequestRepository.save(request);
        mailService.sendToAdmins("Etetési módosítási kérés", "Új etetési módosítási kérés érkezett.");
        return saved;
    }

    /**
     * Etetési ütemterv tényleges frissítése.
     *
     * @param id  ütemterv azonosító
     * @param dto módosítási adatok
     * @return frissített ütemterv
     */
    @Transactional
    public FeedSched applyFeedSchedUpdate(Long id, FeedSchedDTO dto){
        FeedSched existingFeedSched = feedSchedRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        List<Long> existingItemIds = existingFeedSched.getFeedSchedItems().stream()
                .map(link -> link.getItem().getId())
                .toList();
        if (dto.getFeedMorning() != null) {
            existingFeedSched.setFeedMorning(dto.getFeedMorning());
        }
        if (dto.getFeedNoon() != null) {
            existingFeedSched.setFeedNoon(dto.getFeedNoon());
        }
        if (dto.getFeedEvening() != null) {
            existingFeedSched.setFeedEvening(dto.getFeedEvening());
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
                    ensureNotBedding(item);

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
                    ensureNotBedding(item);
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

    /**
     * Etetési ütemterv törlése.
     *
     * @param id ütemterv azonosító
     */
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

    /**
     * Ló hozzáadása etetési ütemtervhez.
     *
     * @param feedSchedId ütemterv azonosító
     * @param horseId     ló azonosító
     */
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

    /**
     * Tétel hozzáadása etetési ütemtervhez.
     *
     * @param feedSchedId ütemterv azonosító
     * @param itemId      tétel azonosító
     * @param amount      mennyiség
     */
    @Transactional
    public void addItemToFeedSched(Long feedSchedId, Long itemId, Double amount) {
        if (amount == null) {
            throw new RuntimeException("Mennyiség kötelező.");
        }
        FeedSched feedSched = feedSchedRepository.findById(feedSchedId)
            .orElseThrow(() -> new RuntimeException("Etetési napló nem található."));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Takarmány nem található."));
        ensureNotBedding(item);
        FeedSchedItem link = new FeedSchedItem();
        link.setItem(item);
        link.setFeedSched(feedSched);
        link.setAmount(amount);
        feedSched.getFeedSchedItems().add(link);
        feedSchedRepository.save(feedSched);
        storageService.syncAmountInUseForItem(itemId);
    }

    /**
     * Összes változtatási kérelem lekérése.
     *
     * @return kérelmek listája
     */
    public List<FeedSchedChangeRequest> getAllChangeRequests() {
        return feedSchedChangeRequestRepository.findAllByOrderByRequestedAtDesc();
    }

    /**
     * Saját változtatási kérelmek lekérése.
     *
     * @param auth hitelesítési adatok
     * @return kérelmek listája
     */
    public List<FeedSchedChangeRequest> getMyChangeRequests(Authentication auth) {
        if (auth == null) return java.util.List.of();
        User user = userRepository.findByUsername(auth.getName());
        if (user == null) return java.util.List.of();
        return feedSchedChangeRequestRepository.findAllByRequestedBy_IdOrderByRequestedAtDesc(user.getId());
    }

    /**
     * Változtatási kérelem jóváhagyása és alkalmazása.
     *
     * @param requestId kérelem azonosító
     * @return frissített ütemterv
     */
    @Transactional
    public FeedSched approveChangeRequest(Long requestId) {
        FeedSchedChangeRequest request = feedSchedChangeRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Kérés nem található."));
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedMorning(request.getRequestedMorning());
        dto.setFeedNoon(request.getRequestedNoon());
        dto.setFeedEvening(request.getRequestedEvening());
        dto.setDescription(request.getRequestedDescription());
        dto.setHorseIds(parseIds(request.getRequestedHorseIds()));
        List<FeedSchedItemAmountDTO> requestedItems = parseItemAmounts(request.getRequestedItemAmounts());
        if (!requestedItems.isEmpty()) {
            dto.setItems(requestedItems);
        } else {
            dto.setItemIds(parseIds(request.getRequestedItemIds()));
        }
        FeedSched updated = applyFeedSchedUpdate(request.getFeedSched().getId(), dto);
        feedSchedChangeRequestRepository.delete(request);
        return updated;
    }

    /**
     * Változtatási kérelem elutasítása.
     *
     * @param requestId kérelem azonosító
     */
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

    private void ensureNotBedding(Item item) {
        if (item.getItemType() == ItemType.BEDDING) {
            throw new RuntimeException("Etetéshez alom típusú tétel nem választható.");
        }
    }

    private String joinIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return ids.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
    }

    private String joinItemAmounts(List<FeedSchedItemAmountDTO> items) {
        if (items == null || items.isEmpty()) return null;
        return items.stream()
            .map(item -> {
                String amount = item.getAmount() != null ? item.getAmount().toString() : "";
                return item.getItemId() + ":" + amount;
            })
            .collect(java.util.stream.Collectors.joining(","));
    }

    private boolean isAnyTimeSelected(FeedSchedDTO dto) {
        return Boolean.TRUE.equals(dto.getFeedMorning())
            || Boolean.TRUE.equals(dto.getFeedNoon())
            || Boolean.TRUE.equals(dto.getFeedEvening());
    }

    /**
     * CSV azonosító lista feldolgozása Long listává.
     *
     * @param csv vesszővel elválasztott lista
     * @return azonosítók listája
     */
    public List<Long> parseIds(String csv) {
        if (csv == null || csv.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(csv.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .map(Long::valueOf)
            .toList();
    }

    /**
     * CSV tétel:mennyiség lista feldolgozása.
     *
     * @param csv vesszővel elválasztott lista
     * @return tétel-mennyiség lista
     */
    public List<FeedSchedItemAmountDTO> parseItemAmounts(String csv) {
        if (csv == null || csv.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(csv.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .map(pair -> {
                String[] parts = pair.split(":", 2);
                Long itemId = Long.valueOf(parts[0].trim());
                Double amount = null;
                if (parts.length > 1 && !parts[1].isBlank()) {
                    amount = Double.valueOf(parts[1].trim());
                }
                return new FeedSchedItemAmountDTO(itemId, amount);
            })
            .toList();
    }
}
