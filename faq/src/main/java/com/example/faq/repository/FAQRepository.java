package com.example.faq.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.faq.entity.FAQ;

/**
 * Repository interface for FAQ entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {

    /**
     * Find FAQs by category with pagination
     */
    Page<FAQ> findByCategory(String category, Pageable pageable);

    /**
     * Search FAQs by question or answer text
     */
    @Query("SELECT f FROM FAQ f WHERE " +
           "LOWER(f.question) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<FAQ> searchByQuestionOrAnswer(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Search FAQs by category and text
     */
    @Query("SELECT f FROM FAQ f WHERE " +
           "f.category = :category AND (" +
           "LOWER(f.question) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<FAQ> searchByCategoryAndText(
            @Param("category") String category,
            @Param("searchText") String searchText,
            Pageable pageable
    );

    /**
     * Count FAQs by category
     */
    long countByCategory(String category);
}