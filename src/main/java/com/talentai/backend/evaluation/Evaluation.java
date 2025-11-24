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

    private int score;

    // NOUVEAU : Statut du workflow (NEW, INTERVIEW, ACCEPTED, REJECTED)
    private String status;

    // NOUVEAU : Questions générées par l'IA pour l'entretien
    @Column(length = 5000)
    private String interviewQuestions;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;
}