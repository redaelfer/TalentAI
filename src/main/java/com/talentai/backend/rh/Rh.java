package com.talentai.backend.rh;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Infos de Connexion
    @Column(unique = true)
    private String username;
    private String password;
    private String email;

    // Infos Personnelles du Recruteur
    private String fullName;
    private String poste; // ex: "Responsable RH", "CTO"

    // Infos de l'Entreprise
    private String nomEntreprise;      // ex: "Capgemini"
    private String adresseEntreprise;  // ex: "123 Bd Anfa, Casablanca"
    private String siteWebEntreprise;  // ex: "https://capgemini.com"
    private String telephoneEntreprise;
}