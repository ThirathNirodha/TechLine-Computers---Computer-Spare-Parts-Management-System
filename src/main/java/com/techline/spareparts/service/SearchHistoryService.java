package com.techline.spareparts.service;

import com.techline.spareparts.entity.SearchHistory;
import com.techline.spareparts.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional
    public void recordSearch(String term) {
        if (term == null || term.isBlank()) return;
        SearchHistory sh = SearchHistory.builder()
                .searchTerm(term.trim())
                .searchedAt(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(sh);
    }

    public List<String> getFrequentlySearched(int limit) {
        return searchHistoryRepository.findFrequentlySearchedTerms(PageRequest.of(0, limit));
    }

    public List<SearchHistory> getRecentSearches() {
        return searchHistoryRepository.findTop10ByOrderBySearchedAtDesc();
    }
}
