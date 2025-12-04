package com.talentai.backend.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@Service
public class AiService {

    private final WebClient client;
    private final String ollamaModel;

    // Injection de l'URL et du Modèle depuis application.yml ou les variables d'environnement
    public AiService(
            @Value("${ollama.url:http://localhost:11434}") String ollamaUrl,
            @Value("${ollama.model:llama3}") String ollamaModel
    ) {
        this.client = WebClient.builder()
                .baseUrl(ollamaUrl)
                .build();
        this.ollamaModel = ollamaModel;
    }

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

        return callOllama(prompt);
    }

    public String generateInterviewQuestions(String cvText, String jobDescription) {
        String prompt = """
                Tu es un expert technique. Analyse ce CV par rapport à l'offre.
                Identifie les compétences manquantes ou faibles dans le CV.
                Génère 5 questions d'entretien techniques précises pour tester ces lacunes.
                Formate la réponse strictement en liste HTML (<ul><li>Question...</li></ul>).
                Ne mets pas de texte d'introduction, juste la liste HTML.
                
                Offre :
                %s
                
                CV :
                %s
                """.formatted(jobDescription, cvText);

        return callOllama(prompt);
    }

    private String callOllama(String prompt) {
        Map<String, Object> body = Map.of(
                "model", this.ollamaModel, // Utilisation de la variable injectée
                "prompt", prompt,
                "stream", false
        );

        try {
            Map<String, Object> response = client.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("response")) {
                return response.get("response").toString().trim();
            }
        } catch (Exception e) {
            System.err.println("Erreur Ollama (" + this.ollamaModel + "): " + e.getMessage());
            return "Erreur lors de la génération.";
        }
        return "";
    }
}