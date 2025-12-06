package com.talentai.backend.offer;

import com.talentai.backend.rh.Rh;
import com.talentai.backend.rh.RhRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private RhRepository rhRepository;

    @InjectMocks
    private OfferService offerService;

    @Test
    void shouldCreateOfferSuccessfully() {
        Long rhId = 1L;
        OfferRequest request = new OfferRequest(
                "Dev Java", "Desc", "Java, Spring", "CDI", "40k", "Junior", "Fulltime", rhId
        );

        Rh mockRh = new Rh();
        mockRh.setId(rhId);

        Offer savedOffer = Offer.builder()
                .id(10L)
                .title(request.title())
                .rh(mockRh)
                .build();

        when(rhRepository.findById(rhId)).thenReturn(Optional.of(mockRh));
        when(offerRepository.save(any(Offer.class))).thenReturn(savedOffer);

        Offer result = offerService.create(request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Dev Java", result.getTitle());
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    void shouldThrowExceptionWhenRhNotFound() {
        OfferRequest request = new OfferRequest(
                "Dev Java", "Desc", "Java", "CDI", "40k", "Junior", "Fulltime", 99L
        );

        when(rhRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            offerService.create(request);
        });

        assertTrue(exception.getMessage().contains("RH non trouvé"));
        verify(offerRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllOffers() {
        when(offerRepository.findAll()).thenReturn(List.of(new Offer(), new Offer()));

        List<Offer> offers = offerService.findAll();

        assertEquals(2, offers.size());
        verify(offerRepository).findAll();
    }
}