package edu.pte.ttk.istallo_kezelo.service;

import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.model.enums.Sex;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

final class ServiceTestSupport {

    private ServiceTestSupport() {
    }

    static Authentication auth(String username, String... roles) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new)
            .toList();
        return new UsernamePasswordAuthenticationToken(username, "n/a", authorities);
    }

    static User user(Long id, String username, UserType userType) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setUserLname("Last");
        user.setUserFname("First");
        user.setEmail(username + "@example.com");
        user.setPhone("123");
        user.setPassword("encoded");
        user.setUserType(userType);
        return user;
    }

    static Stable stable(Long id, String stableName) {
        Stable stable = new Stable();
        ReflectionTestUtils.setField(stable, "id", id);
        stable.setStableName(stableName);
        return stable;
    }

    static Horse horse(Long id, String horseName, User owner, Stable stable) {
        Horse horse = new Horse();
        ReflectionTestUtils.setField(horse, "id", id);
        horse.setHorseName(horseName);
        horse.setDob(LocalDate.of(2018, 1, 1));
        horse.setSex(Sex.M);
        horse.setOwner(owner);
        horse.setStable(stable);
        horse.setPassportNum("PASS-" + id);
        horse.setMicrochipNum("CHIP-" + id);
        horse.setAdditional("notes");
        return horse;
    }

    static Item item(Long id, String name) {
        Item item = new Item();
        ReflectionTestUtils.setField(item, "id", id);
        item.setName(name);
        item.setItemType(ItemType.FEED);
        item.setItemCategory(ItemCategory.CONSUMABLE);
        return item;
    }

    static Storage storage(Long id, Item item, double amountStored, double amountInUse) {
        Storage storage = new Storage();
        ReflectionTestUtils.setField(storage, "id", id);
        storage.setItem(item);
        storage.setAmountStored(amountStored);
        storage.setAmountInUse(amountInUse);
        return storage;
    }

    static FeedSched feedSched(Long id, String description) {
        FeedSched feedSched = new FeedSched();
        ReflectionTestUtils.setField(feedSched, "id", id);
        feedSched.setDescription(description);
        feedSched.setFeedMorning(true);
        return feedSched;
    }

    static FeedSchedItem feedSchedItem(Long id, FeedSched feedSched, Item item, double amount) {
        FeedSchedItem link = new FeedSchedItem();
        ReflectionTestUtils.setField(link, "id", id);
        link.setFeedSched(feedSched);
        link.setItem(item);
        link.setAmount(amount);
        return link;
    }

    static HorseFeedSched horseFeedSched(Long id, Horse horse, FeedSched feedSched) {
        HorseFeedSched link = new HorseFeedSched();
        ReflectionTestUtils.setField(link, "id", id);
        link.setHorse(horse);
        link.setFeedSched(feedSched);
        return link;
    }

    static Shot shot(Long id, String shotName) {
        Shot shot = new Shot();
        ReflectionTestUtils.setField(shot, "id", id);
        shot.setShotName(shotName);
        shot.setDate(LocalDate.of(2026, 1, 15));
        shot.setFrequencyUnit("MONTHS");
        shot.setFrequencyValue(6);
        return shot;
    }

    static HorseShot horseShot(Long id, Horse horse, Shot shot) {
        HorseShot link = new HorseShot();
        ReflectionTestUtils.setField(link, "id", id);
        link.setHorse(horse);
        link.setShot(shot);
        return link;
    }

    static Treatment treatment(Long id, String treatmentName) {
        Treatment treatment = new Treatment();
        ReflectionTestUtils.setField(treatment, "id", id);
        treatment.setTreatmentName(treatmentName);
        treatment.setDescription("description");
        treatment.setDate(LocalDate.of(2026, 2, 1));
        return treatment;
    }

    static HorseTreatment horseTreatment(Long id, Horse horse, Treatment treatment) {
        HorseTreatment link = new HorseTreatment();
        ReflectionTestUtils.setField(link, "id", id);
        link.setHorse(horse);
        link.setTreatment(treatment);
        return link;
    }

    static FarrierApp farrierApp(Long id, String farrierName) {
        FarrierApp farrierApp = new FarrierApp();
        ReflectionTestUtils.setField(farrierApp, "id", id);
        farrierApp.setFarrierName(farrierName);
        farrierApp.setFarrierPhone("555");
        farrierApp.setAppointmentDate(LocalDate.of(2026, 3, 1));
        farrierApp.setShoes(Boolean.TRUE);
        return farrierApp;
    }

    static HorseFarrierApp horseFarrierApp(Long id, Horse horse, FarrierApp farrierApp) {
        HorseFarrierApp link = new HorseFarrierApp();
        ReflectionTestUtils.setField(link, "id", id);
        link.setHorse(horse);
        link.setFarrierApp(farrierApp);
        return link;
    }

    static CalendarEvent calendarEvent(Long id, Horse horse, EventType eventType, LocalDate eventDate) {
        CalendarEvent event = new CalendarEvent();
        event.setId(id);
        event.setHorse(horse);
        event.setEventType(eventType);
        event.setEventDate(eventDate);
        event.setRelatedEntityId(99L);
        return event;
    }
}
