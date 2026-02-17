package com.example.faq.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating FAQ
 * Used in POST and PUT requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQRequest {

    @NotBlank(message = "Вопрос не может быть пустым")
    @Size(max = 2000, message = "Вопрос не может быть длиннее 2000 символов")
    private String question;

    @NotBlank(message = "Ответ не может быть пустым")
    @Size(max = 2000, message = "Ответ не может быть длиннее 2000 символов")
    private String answer;

    @Size(max = 50, message = "Категория не может быть длиннее 50 символов")
    private String category;
}