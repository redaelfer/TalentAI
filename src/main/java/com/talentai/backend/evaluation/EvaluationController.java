package com.talentai.backend.evaluation;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORT IMPORTANT
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EvaluationController {

    private final EvaluationRepository repository;

    @GetMapping("/offer/{offerId}")
    @Transactional(readOnly = true) // <--- OBLIGATOIRE POUR LIRE LE CANDIDAT (CV) SANS ERREUR
    public List<EvaluationDTO> getByOffer(@PathVariable Long offerId) {
        List<Evaluation> evaluations = repository.findByOfferId(offerId);

        // On transforme la donnée brute en un format simple pour le React
        return evaluations.stream()
                .map(e -> new EvaluationDTO(
                        e.getCandidate().getId(),
                        e.getCandidate().getFullName(),
                        e.getCandidate().getEmail(),
                        e.getScore()
                ))
                .collect(Collectors.toList());
    }

    // Objet simple pour le frontend
    public record EvaluationDTO(
            Long candidateId,
            String candidateName,
            String email,
            int score
    ) {}
}