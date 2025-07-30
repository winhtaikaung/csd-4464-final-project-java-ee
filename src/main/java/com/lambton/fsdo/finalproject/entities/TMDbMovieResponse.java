package com.lambton.fsdo.finalproject.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

// Response wrapper for movie lists
class TMDbMovieResponse {
    private Integer page;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_results")
    private Integer totalResults;

    private List<TMDbMovieDto> results;

    // Constructors
    public TMDbMovieResponse() {}

    // Getters and Setters
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<TMDbMovieDto> getResults() {
        return results;
    }

    public void setResults(List<TMDbMovieDto> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "TMDbMovieResponse{" +
                "page=" + page +
                ", totalPages=" + totalPages +
                ", totalResults=" + totalResults +
                ", resultsCount=" + (results != null ? results.size() : 0) +
                '}';
    }
}
