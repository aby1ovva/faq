package com.example.faq.service;

import com.example.faq.entity.FAQ;
import com.example.faq.exception.ResourceNotFoundException;
import com.example.faq.model.request.FAQRequest;
import com.example.faq.model.response.FAQResponse;
import com.example.faq.model.response.PageResponse;
import com.example.faq.repository.FAQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FAQService
 * Contains business logic for FAQ operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;

    @Override
    @Transactional
    public FAQResponse createFAQ(FAQRequest request) {
        log.info("Creating new FAQ with question: {}", request.getQuestion());

        FAQ faq = FAQ.builder()
                .question(request.getQuestion().trim())
                .answer(request.getAnswer().trim())
                .category(request.getCategory() != null ? request.getCategory().trim() : null)
                .build();

        FAQ savedFAQ = faqRepository.save(faq);
        log.info("FAQ created successfully with ID: {}", savedFAQ.getId());

        return FAQResponse.fromEntity(savedFAQ);
    }

    @Override
    @Transactional(readOnly = true)
    public FAQResponse getFAQById(Long id) {
        log.info("Fetching FAQ with ID: {}", id);

        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", id));

        return FAQResponse.fromEntity(faq);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FAQResponse> getAllFAQs(int page, int size) {
        log.info("Fetching all FAQs - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FAQ> faqPage = faqRepository.findAll(pageable);

        return buildPageResponse(faqPage);
    }
    @Override
    @Transactional(readOnly = true)
    public PageResponse<FAQResponse> getFAQsByCategory(String category, int page, int size) {
        log.info("Fetching FAQs by category: {} - page: {}, size: {}", category, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FAQ> faqPage = faqRepository.findByCategory(category, pageable);

        return buildPageResponse(faqPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FAQResponse> searchFAQs(String searchText, int page, int size) {
        log.info("Searching FAQs with text: {} - page: {}, size: {}", searchText, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FAQ> faqPage = faqRepository.searchByQuestionOrAnswer(searchText, pageable);

        return buildPageResponse(faqPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FAQResponse> searchFAQsByCategoryAndText(
            String category, String searchText, int page, int size) {
        log.info("Searching FAQs by category: {} and text: {} - page: {}, size: {}",
                category, searchText, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FAQ> faqPage = faqRepository.searchByCategoryAndText(category, searchText, pageable);

        return buildPageResponse(faqPage);
    }

    @Override
    @Transactional
    public FAQResponse updateFAQ(Long id, FAQRequest request) {
        log.info("Updating FAQ with ID: {}", id);

        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", id));

        faq.setQuestion(request.getQuestion().trim());
        faq.setAnswer(request.getAnswer().trim());
        faq.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);

        FAQ updatedFAQ = faqRepository.save(faq);
        log.info("FAQ updated successfully with ID: {}", updatedFAQ.getId());

        return FAQResponse.fromEntity(updatedFAQ);
    }

    @Override
    @Transactional
    public void deleteFAQ(Long id) {
        log.info("Deleting FAQ with ID: {}", id);

        if (!faqRepository.existsById(id)) {
            throw new ResourceNotFoundException("FAQ", id);
        }

        faqRepository.deleteById(id);
        log.info("FAQ deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return faqRepository.existsById(id);
    }

    /**
     * Helper method to build PageResponse from Page<FAQ>
     */
    private PageResponse<FAQResponse> buildPageResponse(Page<FAQ> faqPage) {
        List<FAQResponse> content = faqPage.getContent().stream()
                .map(FAQResponse::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<FAQResponse>builder()
                .content(content)
                .page(faqPage.getNumber())
                .size(faqPage.getSize())
                .totalElements(faqPage.getTotalElements())
                .totalPages(faqPage.getTotalPages())
                .first(faqPage.isFirst())
                .last(faqPage.isLast())
                .build();
    }
}