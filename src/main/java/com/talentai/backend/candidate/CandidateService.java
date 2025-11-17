package com.talentai.backend.candidate;

import com.talentai.backend.ai.AiService;
import com.talentai.backend.evaluation.Evaluation;
import com.talentai.backend.evaluation.EvaluationRepository;
import com.talentai.backend.offer.Offer;
import com.talentai.backend.offer.OfferRepository;
import com.talentai.backend.user.User;
import com.talentai.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor // Gère l'injection des 'final'
public class CandidateService {

    // Répertoires (Repositories) nécessaires
    private final CandidateRepository candidateRepository;
    private final OfferRepository offerRepository;
    private final EvaluationRepository evaluationRepository;
    private final AiService aiService;
    private final UserRepository userRepository; // <-- LIGNE AJOUTÉE

    public List<Candidate> all() {
        return candidateRepository.findAll();
    }

    public Candidate one(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
    }

    public Candidate create(CandidateRequest req) {
        Candidate candidate = Candidate.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .titre(req.getTitre())
                .telephone(req.getTelephone())
                .build();
        return candidateRepository.save(candidate);
    }

    public Candidate uploadCv(Long id, MultipartFile file) throws IOException {
        Candidate candidate = one(id);
        candidate.setCvFile(file.getBytes());
        candidate.setCvFileName(file.getOriginalFilename());
        candidate.setCvContentType(file.getContentType());
        return candidateRepository.save(candidate);
    }

    /**
     * Évalue le CV d'un candidat pour une offre (appelé par POST /api/candidates/{id}/evaluate)
     */
    public int evaluateCv(Long candidateId, String jobDescription, Long offerId) throws IOException {
        Candidate candidate = one(candidateId);
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + offerId));

        if (candidate.getCvFile() == null) {
            throw new RuntimeException("CV not found for candidate, cannot evaluate.");
        }

        // --- DÉBUT DE LA NOUVELLE LOGIQUE ---

        // 1. Extraire le texte du PDF
        String cvText = extractTextFromPdf(candidate.getCvFile());
        if (cvText.isBlank()) {
            System.err.println("Avertissement: Impossible d'extraire le texte du PDF pour le candidat ID: " + candidateId);
            // On peut retourner 0 directement si le CV est illisible
            return 0;
        }

        // 2. Appeler Ollama (votre AiService)
        String scoreResponse = aiService.scoreCv(cvText, jobDescription);

        // 3. Nettoyer et convertir la réponse de l'IA en nombre
        int score = 0;
        try {
            // Supprime tout ce qui n'est pas un chiffre (au cas où Llama répond "Score: 85")
            String cleanResponse = scoreResponse.replaceAll("[^\\d]", "");
            if (!cleanResponse.isEmpty()) {
                score = Integer.parseInt(cleanResponse);
            }
        } catch (NumberFormatException e) {
            System.err.println("Ollama a retourné une réponse non numérique: " + scoreResponse);
            // score reste 0 si le parsing échoue
        }
        // --- FIN DE LA NOUVELLE LOGIQUE ---


        // 4. Sauvegarder cette évaluation dans la base de données
        Evaluation evaluation = Evaluation.builder()
                .candidate(candidate)
                .offer(offer)
                .score(score)
                .build();

        evaluationRepository.save(evaluation);

        return score;
    }

    // --- DÉBUT DE LA CORRECTION ---
    public Candidate updateCandidate(Long id, CandidateRequest candidateRequest) {
        // Au lieu de "one(id)", on utilise "findById" qui ne lève pas d'exception
        Candidate existingCandidate = candidateRepository.findById(id).orElse(null);

        if (existingCandidate == null) {
            // Le profil n'existe pas, on le CRÉE
            // 1. Trouver l'utilisateur parent
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec id: " + id + ". Impossible de créer le profil candidat."));

            // 2. Créer le nouveau profil candidat
            existingCandidate = new Candidate();
            existingCandidate.setId(user.getId()); // L'ID est partagé
            existingCandidate.setUser(user);     // Lier à l'utilisateur
        }

        // Que le profil soit nouveau ou ancien, on applique les mises à jour
        existingCandidate.setFullName(candidateRequest.getFullName());
        existingCandidate.setEmail(candidateRequest.getEmail());
        existingCandidate.setTitre(candidateRequest.getTitre());
        existingCandidate.setTelephone(candidateRequest.getTelephone());

        return candidateRepository.save(existingCandidate);
    }
    // --- FIN DE LA CORRECTION ---


    /**
     * Méthode privée pour lire le texte d'un PDF en utilisant PDFBox 3.x
     */
    private String extractTextFromPdf(byte[] pdfData) throws IOException {
        if (pdfData == null || pdfData.length == 0) {
            return "";
        }

        // On passe 'pdfData' (un byte[]) directement
        try (PDDocument document = Loader.loadPDF(pdfData)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction du texte PDF: " + e.getMessage());
            return ""; // Retourne une chaîne vide en cas d'échec
        }
    }
}