package com.talentai.backend.candidate;

import com.talentai.backend.user.User; // <-- 1. AJOUTER CET IMPORT
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Candidate {
    @Id
    private Long id; // L'ID sera fourni par l'Utilisateur

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indique que cette relation UTILISE la clé primaire (partage l'ID)
    @JoinColumn(name = "id") // Le nom de la colonne PK/FK sera "id"
    private User user;
    // --- FIN DE L'AJOUT ---

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