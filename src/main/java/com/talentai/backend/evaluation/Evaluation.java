package com.talentai.backend.evaluation;

import com.talentai.backend.candidate.Candidate;
import com.talentai.backend.offer.Offer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score; // Score donné par l'IA (0-100)

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate; // Lien direct vers le candidat

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer; // Lien vers l'offre
}