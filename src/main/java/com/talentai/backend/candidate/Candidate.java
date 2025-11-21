package com.talentai.backend.candidate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username; // Login du candidat
    private String password; // Mot de passe du candidat

    private String fullName;
    private String email;
    private String titre;
    private String telephone;

    @Lob
    @Column(length = 1000000)
    private byte[] cvFile;
    private String cvFileName;
    private String cvContentType;
}