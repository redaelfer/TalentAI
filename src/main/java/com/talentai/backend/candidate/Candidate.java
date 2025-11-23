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
    private String username;
    private String password;

    private String fullName;
    private String email;
    private String titre;
    private String telephone;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 1000000)
    private byte[] cvFile;
    private String cvFileName;
    private String cvContentType;
}