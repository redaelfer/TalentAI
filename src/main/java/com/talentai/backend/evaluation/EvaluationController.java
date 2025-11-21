package com.talentai.backend.evaluation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EvaluationController {

    private final EvaluationRepository repository;

    // Récupérer les évaluations d'une offre spécifique
    @GetMapping("/offer/{offerId}")
    public List<EvaluationDTO> getByOffer(@PathVariable Long offerId) {
        List<Evaluation> evaluations = repository.findByOfferId(offerId);

        // On transforme les données brutes en un format simple pour le React
        return evaluations.stream()
                .map(e -> new EvaluationDTO(
                        e.getCandidate().getId(),
                        e.getCandidate().getFullName(), // Nom récupéré directement du Candidat
                        e.getCandidate().getEmail(),    // Email récupéré directement
                        e.getScore()
                ))
                .collect(Collectors.toList());
    }

    // Petit objet (DTO) pour structurer la réponse JSON proprement
    public record EvaluationDTO(
            Long candidateId,
            String candidateName,
            String email,
            int score
    ) {}
}