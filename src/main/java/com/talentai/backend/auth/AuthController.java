package com.talentai.backend.auth;

import com.talentai.backend.candidate.Candidate; // <-- 1. AJOUTER CET IMPORT
import com.talentai.backend.user.Role;
import com.talentai.backend.user.User;
import com.talentai.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserRepository userRepository;
    // Pas de PasswordEncoder...

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // S'assurer que le rôle n'est pas nul
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_CANDIDAT); // Définit un rôle par défaut
        }

        // --- 2. AJOUTER CETTE LOGIQUE ---
        // Si c'est un candidat, on crée son profil Candidat vide et on le lie
        if (user.getRole() == Role.ROLE_CANDIDAT) {
            Candidate newCandidate = Candidate.builder()
                    .email(user.getEmail()) // Pré-remplit l'email
                    .fullName(user.getUsername()) // Pré-remplit le nom
                    .build();

            user.setCandidate(newCandidate); // Lier l'utilisateur au candidat
            newCandidate.setUser(user);     // Lier le candidat à l'utilisateur (essentiel pour la relation)
        }
        // --- FIN DE L'AJOUT ---

        // ⚠️ ATTENTION: Le mot de passe est enregistré en clair (non crypté)
        // En sauvegardant l'utilisateur, JPA sauvegardera aussi le candidat grâce au "CascadeType.ALL"
        User savedUser = userRepository.save(user);

        // On renvoie le DTO LoginResponse
        LoginResponse response = new LoginResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginDetails) {

        Optional<User> userOptional = userRepository.findByUsername(loginDetails.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = userOptional.get();

        if (!loginDetails.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}