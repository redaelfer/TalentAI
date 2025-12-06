package com.talentai.backend.evaluation;

import com.talentai.backend.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EvaluationController {

    private final EvaluationRepository repository;
    private final AiService aiService;

    @PostMapping("/{id}/questions")
    @Transactional
    public String generateQuestions(@PathVariable Long id) throws IOException {
        Evaluation eval = repository.findById(id).orElseThrow();

        String cvText = "";
        if (eval.getCandidate().getCvFile() != null) {
            try (PDDocument document = Loader.loadPDF(eval.getCandidate().getCvFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                cvText = stripper.getText(document);
            } catch (Exception e) {
                System.err.println("Erreur lecture PDF: " + e.getMessage());
            }
        }

        String questions = aiService.generateInterviewQuestions(cvText, eval.getOffer().getDescription());
        eval.setInterviewQuestions(questions);
        repository.save(eval);

        return questions;
    }

    @PostMapping("/{id}/summary")
    @Transactional
    public String generateSummary(@PathVariable Long id) {
        Evaluation eval = repository.findById(id).orElseThrow();

        String cvText = "";
        if (eval.getCandidate().getCvFile() != null) {
            try (PDDocument document = Loader.loadPDF(eval.getCandidate().getCvFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                cvText = stripper.getText(document);
            } catch (Exception e) {
                System.err.println("Erreur lecture PDF: " + e.getMessage());
            }
        }

        String summary = aiService.generateSummary(cvText, eval.getOffer().getDescription());
        eval.setSummary(summary);
        repository.save(eval);
        return summary;
    }

    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestBody String status) {
        Evaluation eval = repository.findById(id).orElseThrow();
        eval.setStatus(status.replace("\"", ""));
        repository.save(eval);
    }

    @GetMapping("/offer/{offerId}")
    @Transactional(readOnly = true)
    public List<EvaluationDTO> getByOffer(@PathVariable Long offerId) {
        List<Evaluation> evaluations = repository.findByOfferId(offerId);

        return evaluations.stream()
                .map(e -> new EvaluationDTO(
                        e.getId(),
                        e.getCandidate().getId(),
                        e.getCandidate().getFullName(),
                        e.getCandidate().getEmail(),
                        e.getCandidate().getTitre(),
                        e.getCandidate().getTelephone(),
                        e.getScore(),
                        e.getStatus() != null ? e.getStatus() : "NEW",
                        e.getInterviewQuestions(),
                        e.getSummary(),
                        e.getCandidate().getSkills(),
                        e.getCandidate().getYearsOfExperience()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/candidate/{candidateId}/offer-ids")
    public List<Long> getAppliedOfferIds(@PathVariable Long candidateId) {
        return repository.findByCandidateId(candidateId).stream()
                .map(eval -> eval.getOffer().getId())
                .collect(Collectors.toList());
    }

    @GetMapping("/candidate/{candidateId}/applications")
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getCandidateApplications(@PathVariable Long candidateId) {
        List<Evaluation> evaluations = repository.findByCandidateId(candidateId);

        return evaluations.stream()
                .map(e -> new ApplicationDTO(
                        e.getId(),
                        e.getOffer().getTitle(),
                        e.getOffer().getDescription(),
                        e.getOffer().getRh() != null ? e.getOffer().getRh().getNomEntreprise() : "Entreprise",
                        e.getStatus(),
                        e.getInterviewQuestions()
                ))
                .collect(Collectors.toList());
    }

    public record EvaluationDTO(
            Long id,
            Long candidateId,
            String candidateName,
            String email,
            String titre,
            String telephone,
            int score,
            String status,
            String interviewQuestions,
            String summary,
            String skills,
            Integer yearsOfExperience
    ) {}

    public record ApplicationDTO(
            Long evaluationId,
            String offerTitle,
            String offerDescription,
            String companyName,
            String status,
            String interviewQuestions
    ) {}
}