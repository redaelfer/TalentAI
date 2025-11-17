package com.talentai.backend.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final WebClient client;

    public AiService() {
        this.client = WebClient.builder()
                .baseUrl("http://localhost:11434") // port par défaut d’Ollama
                .build();
    }

    /**
     * Appelle Ollama pour évaluer la compatibilité entre un CV et une offre.
     */
    public String scoreCv(String cvText, String jobDescription) {
        String prompt = """
                Tu es un recruteur technique expert.
                Tâche : Évalue la compatibilité entre le CV fourni et l'offre d'emploi.
                Critères : Compétences techniques, expérience professionnelle, formation.
                
                CV:
                %s
                
                Offre d'emploi:
                %s
                
                Réponse : Donne UNIQUEMENT un score en nombre entier de 0 à 100.
                Ne dis rien d'autre. Pas de "Le score est :", juste le nombre.
                Exemple de réponse : 85
                """.formatted(cvText, jobDescription);

        Map<String, Object> body = Map.of(
                "model", "llama3", // Assurez-vous d'avoir 'llama3' d'installé sur Ollama
                "prompt", prompt,
                "stream", false // On demande une réponse complète, pas un flux
        );

        try {
            Map<String, Object> response = client.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // .block() est ok pour un service interne simple

            if (response != null && response.containsKey("response")) {
                String aiResponse = response.get("response").toString().trim();
                System.out.println("Réponse brute d'Ollama: " + aiResponse); // Pour le débogage
                return aiResponse;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à Ollama: " + e.getMessage());
            return "0"; // Retourne 0 en cas d'erreur de communication
        }

        return "0";
    }
}