package com.talentai.backend.candidate;

import lombok.Data;

@Data
public class CandidateRequest {
    private String fullName;
    private String email;
    private String titre;
    private String telephone;
}