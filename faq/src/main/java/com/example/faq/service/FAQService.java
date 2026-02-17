package com.example.faq.service;

import com.example.faq.model.request.FAQRequest;
import com.example.faq.model.response.FAQResponse;
import com.example.faq.model.response.PageResponse;

/**
 * Service interface for FAQ operations
 * Defines business logic methods
 */
public interface FAQService {

    /**
     * Create a new FAQ
     */
    FAQResponse createFAQ(FAQRequest request);

    /**
     * Get FAQ by ID
     */
    FAQResponse getFAQById(Long id);

    /**
     * Get all FAQs with pagination
     */
    PageResponse<FAQResponse> getAllFAQs(int page, int size);

    /**
     * Get FAQs by category with pagination
     */
    PageResponse<FAQResponse> getFAQsByCategory(String category, int page, int size);

    /**
     * Search FAQs by text in question or answer
     */
    PageResponse<FAQResponse> searchFAQs(String searchText, int page, int size);

    /**
     * Search FAQs by category and text
     */
    PageResponse<FAQResponse> searchFAQsByCategoryAndText(String category, String searchText, int page, int size);

    /**
     * Update existing FAQ
     */
    FAQResponse updateFAQ(Long id, FAQRequest request);

    /**
     * Delete FAQ by ID
     */
    void deleteFAQ(Long id);

    /**
     * Check if FAQ exists by ID
     */
    boolean existsById(Long id);
}