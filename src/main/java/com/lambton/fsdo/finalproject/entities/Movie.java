package com.lambton.fsdo.finalproject.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "favorite_movies")
public class Movie {

    @Id
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String overview;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "vote_average")
    private Double voteAverage;

    @Column(name = "vote_count")
    private Integer voteCount;

    private String language;

    @Column(name = "original_title")
    private String originalTitle;

    private Boolean adult;

    private Double popularity;

    // Constructors
    public Movie() {}

    public Movie(Long tmdbId, String title, String overview, String posterPath,
                 String backdropPath, LocalDate releaseDate, Double voteAverage,
                 Integer voteCount, String language, String originalTitle,
                 Boolean adult, Double popularity) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.language = language;
        this.originalTitle = originalTitle;
        this.adult = adult;
        this.popularity = popularity;
    }

    // Getters and Setters
    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    // Helper method to get full poster URL
    public String getFullPosterUrl(String baseUrl) {
        return posterPath != null ? baseUrl + "/w500" + posterPath : null;
    }

    // Helper method to get full backdrop URL
    public String getFullBackdropUrl(String baseUrl) {
        return backdropPath != null ? baseUrl + "/w1280" + backdropPath : null;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "tmdbId=" + tmdbId +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", voteAverage=" + voteAverage +
                '}';
    }
}