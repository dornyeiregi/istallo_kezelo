package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
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

    @InjectMocks
    private StableService stableService;

    @Test
    void updateStable_changesStableName() {
        Stable stable = ServiceTestSupport.stable(1L, "Old");
        Stable updated = new Stable("New");

        when(stableRepository.findById(1L)).thenReturn(Optional.of(stable));
        when(stableRepository.save(stable)).thenReturn(stable);

        Stable result = stableService.updateStable(1L, updated);

        assertEquals("New", result.getStableName());
    }
}
