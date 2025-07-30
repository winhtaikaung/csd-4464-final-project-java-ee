package com.lambton.fsdo.finalproject.repositories;


import com.lambton.fsdo.finalproject.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Find a movie by its TMDb ID
     */
    Optional<Movie> findByTmdbId(Long tmdbId);

    /**
     * Check if a movie exists by TMDb ID
     */
    boolean existsByTmdbId(Long tmdbId);

    /**
     * Find movies by title containing search term (case insensitive)
     */
    List<Movie> findByTitleContainingIgnoreCase(String title);

    /**
     * Find all movies ordered by vote average descending
     */
    List<Movie> findAllByOrderByVoteAverageDesc();

    /**
     * Find all movies ordered by release date descending
     */
    List<Movie> findAllByOrderByReleaseDateDesc();

    /**
     * Find movies with vote average greater than or equal to specified rating
     */
    @Query("SELECT m FROM Movie m WHERE m.voteAverage >= :minRating ORDER BY m.voteAverage DESC")
    List<Movie> findMoviesByMinimumRating(Double minRating);

    /**
     * Count total favorite movies
     */
    @Query("SELECT COUNT(m) FROM Movie m")
    long countFavoriteMovies();

    /**
     * Find top rated favorite movies (limit to top N)
     */
    @Query("SELECT m FROM Movie m ORDER BY m.voteAverage DESC")
    List<Movie> findTopRatedMovies();
}