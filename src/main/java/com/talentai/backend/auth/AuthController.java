package com.talentai.backend.auth;

import com.talentai.backend.candidate.Candidate;
import com.talentai.backend.candidate.CandidateRepository;
import com.talentai.backend.rh.Rh;
import com.talentai.backend.rh.RhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final CandidateRepository candidateRepository;
    private final RhRepository rhRepository;

    // ==========================================
    // ZONE CANDIDAT
    // ==========================================

    @PostMapping("/candidate/login")
    public ResponseEntity<?> loginCandidate(@RequestBody Map<String, String> loginDetails) {
        String username = loginDetails.get("username");
        String password = loginDetails.get("password");

        Optional<Candidate> candidateOpt = candidateRepository.findByUsername(username);

        if (candidateOpt.isEmpty() || !candidateOpt.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants candidat incorrects"));
        }

        Candidate c = candidateOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", c.getId(),
                "username", c.getUsername(),
                "role", "ROLE_CANDIDAT"
        ));
    }

    @PostMapping("/candidate/register")
    public ResponseEntity<?> registerCandidate(@RequestBody Candidate candidate) {
        if (candidateRepository.findByUsername(candidate.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Ce nom d'utilisateur existe déjà.");
        }
        // On initialise les champs vides pour éviter les null pointers plus tard
        if (candidate.getFullName() == null) candidate.setFullName(candidate.getUsername());

        candidateRepository.save(candidate);
        return ResponseEntity.ok(Map.of("message", "Candidat inscrit avec succès"));
    }

    // ==========================================
    // ZONE RH (RECRUTEUR)
    // ==========================================

    @PostMapping("/rh/login")
    public ResponseEntity<?> loginRh(@RequestBody Map<String, String> loginDetails) {
        String username = loginDetails.get("username");
        String password = loginDetails.get("password");

        Optional<Rh> rhOpt = rhRepository.findByUsername(username);

        if (rhOpt.isEmpty() || !rhOpt.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants RH incorrects"));
        }

        Rh rh = rhOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", rh.getId(),
                "username", rh.getUsername(),
                "role", "ROLE_RH",
                "nomEntreprise", rh.getNomEntreprise() != null ? rh.getNomEntreprise() : ""
        ));
    }

    // C'est l'endpoint que vous m'avez demandé pour le RH
    @PostMapping("/rh/register")
    public ResponseEntity<?> registerRh(@RequestBody Rh rh) {
        if (rhRepository.findByUsername(rh.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Ce nom d'utilisateur RH existe déjà.");
        }
        rhRepository.save(rh);
        return ResponseEntity.ok(Map.of("message", "Compte RH créé avec succès pour " + rh.getNomEntreprise()));
    }
}