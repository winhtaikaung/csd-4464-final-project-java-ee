package com.lambton.fsdo.finalproject.services;

import com.lambton.fsdo.finalproject.entities.Movie;
import com.lambton.fsdo.finalproject.entities.TMDbMovieDto;
import com.lambton.fsdo.finalproject.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final TMDbService tmdbService;

    public MovieService(MovieRepository movieRepository, TMDbService tmdbService) {
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    /**
     * Get trending movies from TMDb API
     */
    public List<TMDbMovieDto> getTrendingMovies() {
        logger.info("Fetching trending movies");
        return tmdbService.getTrendingMovies();
    }

    /**
     * Search movies using TMDb API
     */
    public List<TMDbMovieDto> searchMovies(String query) {
        logger.info("Searching movies with query: {}", query);
        return tmdbService.searchMovies(query);
    }

    /**
     * Get movie details from TMDb API
     */
    public TMDbMovieDto getMovieDetails(Long movieId) {
        logger.info("Fetching movie details for ID: {}", movieId);
        return tmdbService.getMovieDetails(movieId);
    }

    /**
     * Get popular movies from TMDb API
     */
    public List<TMDbMovieDto> getPopularMovies() {
        logger.info("Fetching popular movies");
        return tmdbService.getPopularMovies();
    }

    /**
     * Get now playing movies from TMDb API
     */
    public List<TMDbMovieDto> getNowPlayingMovies() {
        logger.info("Fetching now playing movies");
        return tmdbService.getNowPlayingMovies();
    }

    /**
     * Add a movie to favorites
     */
    public boolean addToFavorites(Long tmdbId) {
        try {
            if (movieRepository.existsByTmdbId(tmdbId)) {
                logger.info("Movie with TMDb ID {} is already in favorites", tmdbId);
                return false;
            }

            TMDbMovieDto movieDto = tmdbService.getMovieDetails(tmdbId);
            if (movieDto == null) {
                logger.error("Could not fetch movie details for TMDb ID: {}", tmdbId);
                return false;
            }

            Movie movie = convertToEntity(movieDto);
            movieRepository.save(movie);

            logger.info("Added movie '{}' to favorites", movie.getTitle());
            return true;

        } catch (Exception e) {
            logger.error("Error adding movie to favorites with TMDb ID: {}", tmdbId, e);
            return false;
        }
    }

    /**
     * Remove a movie from favorites
     */
    public boolean removeFromFavorites(Long tmdbId) {
        try {
            Optional<Movie> movieOpt = movieRepository.findByTmdbId(tmdbId);
            if (movieOpt.isPresent()) {
                movieRepository.delete(movieOpt.get());
                logger.info("Removed movie '{}' from favorites", movieOpt.get().getTitle());
                return true;
            } else {
                logger.info("Movie with TMDb ID {} not found in favorites", tmdbId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error removing movie from favorites with TMDb ID: {}", tmdbId, e);
            return false;
        }
    }

    /**
     * Check if a movie is in favorites
     */
    public boolean isInFavorites(Long tmdbId) {
        return movieRepository.existsByTmdbId(tmdbId);
    }

    /**
     * Get all favorite movies
     */
    public List<Movie> getFavoriteMovies() {
        logger.info("Fetching all favorite movies");
        return movieRepository.findAllByOrderByVoteAverageDesc();
    }

    /**
     * Get favorite movies ordered by release date
     */
    public List<Movie> getFavoriteMoviesByReleaseDate() {
        logger.info("Fetching favorite movies ordered by release date");
        return movieRepository.findAllByOrderByReleaseDateDesc();
    }

    /**
     * Search favorite movies by title
     */
    public List<Movie> searchFavoriteMovies(String title) {
        logger.info("Searching favorite movies with title: {}", title);
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Get favorite movies count
     */
    public long getFavoriteMoviesCount() {
        return movieRepository.countFavoriteMovies();
    }

    /**
     * Get top rated favorite movies
     */
    public List<Movie> getTopRatedFavorites() {
        return movieRepository.findTopRatedMovies();
    }

    /**
     * Get favorite movies with minimum rating
     */
    public List<Movie> getFavoriteMoviesByMinRating(Double minRating) {
        return movieRepository.findMoviesByMinimumRating(minRating);
    }

    /**
     * Get TMDb image base URL
     */
    public String getImageBaseUrl() {
        return tmdbService.getImageBaseUrl();
    }

    /**
     * Convert TMDbMovieDto to Movie entity
     */
    private Movie convertToEntity(TMDbMovieDto dto) {
        Movie movie = new Movie();
        movie.setTmdbId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setPosterPath(dto.getPosterPath());
        movie.setBackdropPath(dto.getBackdropPath());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setVoteAverage(dto.getVoteAverage());
        movie.setVoteCount(dto.getVoteCount());
        movie.setLanguage(dto.getOriginalLanguage());
        movie.setOriginalTitle(dto.getOriginalTitle());
        movie.setAdult(dto.getAdult());
        movie.setPopularity(dto.getPopularity());
        return movie;
    }
}