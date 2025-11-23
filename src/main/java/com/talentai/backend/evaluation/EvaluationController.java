package com.talentai.backend.evaluation;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public List<EvaluationDTO> getByOffer(@PathVariable Long offerId) {

        List<Evaluation> evaluations = repository.findByOfferId(offerId);

        return evaluations.stream()
                .map(e -> new EvaluationDTO(
                        e.getCandidate().getId(),       // ID du candidat
                        e.getCandidate().getFullName(), // Nom (via la relation)
                        e.getCandidate().getEmail(),    // Email (via la relation)
                        e.getCandidate().getTitre(),      // Récupère le titre
                        e.getCandidate().getTelephone(),  // Récupère le téléphone
                        e.getScore()                    // Score IA
                ))
                .collect(Collectors.toList());
    }

    public record EvaluationDTO(
            Long candidateId,
            String candidateName,
            String email,
            String titre,
            String telephone,
            int score
    ) {}
}