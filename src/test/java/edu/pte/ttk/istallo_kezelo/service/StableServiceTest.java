package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StableServiceTest {

    @Mock
    private StableRepository stableRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private StableService stableService;

    @Test
    void saveStable_savesAndSyncsStorage() {
        Stable stable = new Stable("Main");
        when(stableRepository.save(stable)).thenReturn(stable);

        Stable result = stableService.saveStable(stable);

        assertSame(stable, result);
        verify(storageService).syncAllAmountsInUse();
    }

    @Test
    void requireBeddingItem_returnsNullForNullId() {
        assertNull(stableService.requireBeddingItem(null));
    }

    @Test
    void requireBeddingItem_returnsBeddingItem() {
        Item item = ServiceTestSupport.item(2L, "Straw");
        item.setItemType(ItemType.BEDDING);
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        Item result = stableService.requireBeddingItem(2L);

        assertSame(item, result);
    }

    @Test
    void requireBeddingItem_throwsWhenWrongType() {
        Item item = ServiceTestSupport.item(2L, "Hay");
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> stableService.requireBeddingItem(2L));

        assertEquals("Az alomhoz csak ALOM típusú tétel választható.", exception.getMessage());
    }

    @Test
    void getMethods_delegateToRepository() {
        Stable stable = ServiceTestSupport.stable(1L, "Main");
        when(stableRepository.findAll()).thenReturn(List.of(stable));
        when(stableRepository.findById(1L)).thenReturn(Optional.of(stable));
        when(stableRepository.findByStableName("Main")).thenReturn(stable);

        assertEquals(List.of(stable), stableService.getAllStables());
        assertEquals(Optional.of(stable), stableService.getStableById(1L));
        assertSame(stable, stableService.getStableByName("Main"));
    }

    @Test
    void updateStable_changesStableName() {
        Stable stable = ServiceTestSupport.stable(1L, "Old");
        Stable updated = new Stable("New");

        when(stableRepository.findById(1L)).thenReturn(Optional.of(stable));
        when(stableRepository.save(stable)).thenReturn(stable);

        Stable result = stableService.updateStable(1L, updated);

        assertEquals("New", result.getStableName());
    }

    @Test
    void deleteStableById_deletesStable() {
        stableService.deleteStableById(5L);

        verify(stableRepository).deleteById(5L);
    }
}
