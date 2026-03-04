package com.techline.spareparts.repository;

import com.techline.spareparts.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT sh.searchTerm FROM SearchHistory sh GROUP BY sh.searchTerm ORDER BY COUNT(sh) DESC")
    List<String> findFrequentlySearchedTerms(Pageable pageable);

    List<SearchHistory> findTop10ByOrderBySearchedAtDesc();
}
