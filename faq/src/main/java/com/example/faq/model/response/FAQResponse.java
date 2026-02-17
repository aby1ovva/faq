package com.example.faq.model.response;

import com.example.faq.entity.FAQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for FAQ response
 * Used in GET requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQResponse {

    private Long id;
    private String question;
    private String answer;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert FAQ entity to FAQResponse DTO
     */
    public static FAQResponse fromEntity(FAQ faq) {
        return FAQResponse.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(faq.getCategory())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }
}