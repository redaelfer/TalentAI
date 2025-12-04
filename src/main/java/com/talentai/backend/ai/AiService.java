package com.talentai.backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String scoreCv(String cvText, String jobDescription) {
        String systemPrompt = """
                Tu es un recruteur technique expert.
                Tâche : Évalue la compatibilité entre le CV fourni et l'offre d'emploi.
                Critères : Compétences techniques, expérience professionnelle, formation.
                
                Réponse : Donne UNIQUEMENT un score en nombre entier de 0 à 100.
                Ne dis rien d'autre. Pas de "Le score est :", juste le nombre.
                Exemple de réponse : 85
                """;

        String userPrompt = """
                CV:
                %s
                
                Offre d'emploi:
                %s
                """.formatted(cvText, jobDescription);

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            System.err.println("Erreur IA (Score) : " + e.getMessage());
            return "0"; // Valeur par défaut
        }
    }

    public String generateInterviewQuestions(String cvText, String jobDescription) {
        String systemPrompt = """
                Tu es un expert technique. Analyse ce CV par rapport à l'offre.
                Identifie les compétences manquantes ou faibles dans le CV.
                Génère 5 questions d'entretien techniques précises pour tester ces lacunes.
                Formate la réponse strictement en liste HTML (<ul><li>Question...</li></ul>).
                Ne mets pas de texte d'introduction, juste la liste HTML.
                """;

        String userPrompt = """
                Offre :
                %s
                
                CV :
                %s
                """.formatted(jobDescription, cvText);

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            System.err.println("Erreur IA (Questions) : " + e.getMessage());
            return "<ul><li>Erreur lors de la génération des questions.</li></ul>";
        }
    }
}