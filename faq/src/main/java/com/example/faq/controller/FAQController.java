package com.example.faq.controller;

import com.example.faq.model.request.FAQRequest;
import com.example.faq.model.response.FAQResponse;
import com.example.faq.model.response.PageResponse;
import com.example.faq.service.FAQService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for FAQ endpoints
 * Handles HTTP requests for FAQ operations
 */
@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // For development - configure properly in production
public class FAQController {

    private final FAQService faqService;

    /**
     * POST /api/faqs - Create new FAQ
     */
    @PostMapping
    public ResponseEntity<FAQResponse> createFAQ(@Valid @RequestBody FAQRequest request) {
        log.info("POST /api/faqs - Creating new FAQ");
        FAQResponse response = faqService.createFAQ(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/faqs/:id - Get FAQ by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FAQResponse> getFAQById(@PathVariable Long id) {
        log.info("GET /api/faqs/{} - Fetching FAQ by ID", id);
        FAQResponse response = faqService.getFAQById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/faqs - Get all FAQs with pagination and filters
     * Query params:
     * - page: page number (default: 0)
     * - size: page size (default: 10)
     * - category: filter by category (optional)
     * - q: search text (optional)
     */
    @GetMapping
    public ResponseEntity<PageResponse<FAQResponse>> getAllFAQs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q) {

        log.info("GET /api/faqs - page: {}, size: {}, category: {}, search: {}",
                page, size, category, q);

        PageResponse<FAQResponse> response;

        // Search by category and text
        if (category != null && !category.isEmpty() && q != null && !q.isEmpty()) {
            response = faqService.searchFAQsByCategoryAndText(category, q, page, size);
        }
        // Search by text only
        else if (q != null && !q.isEmpty()) {
            response = faqService.searchFAQs(q, page, size);
        }
        // Filter by category only
        else if (category != null && !category.isEmpty()) {
            response = faqService.getFAQsByCategory(category, page, size);
        }
        // Get all FAQs
        else {
            response = faqService.getAllFAQs(page, size);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/faqs/search?q=text - Search FAQs by text
     * This is an alternative endpoint for search functionality
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<FAQResponse>> searchFAQs(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/faqs/search - search: {}, page: {}, size: {}", q, page, size);
        PageResponse<FAQResponse> response = faqService.searchFAQs(q, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/faqs/:id - Update existing FAQ
     */
    @PutMapping("/{id}")
    public ResponseEntity<FAQResponse> updateFAQ(
            @PathVariable Long id,
            @Valid @RequestBody FAQRequest request) {

        log.info("PUT /api/faqs/{} - Updating FAQ", id);
        FAQResponse response = faqService.updateFAQ(id, request);
        return ResponseEntity.ok(response);
    }
    /**
     * DELETE /api/faqs/:id - Delete FAQ by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        log.info("DELETE /api/faqs/{} - Deleting FAQ", id);
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }
}