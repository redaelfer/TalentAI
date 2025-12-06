package com.talentai.backend.rh;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String poste;
    private String nomEntreprise;
    private String adresseEntreprise;
    private String siteWebEntreprise;
    private String telephoneEntreprise;
}