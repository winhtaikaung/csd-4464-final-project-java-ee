package com.lambton.fsdo.finalproject.services;
import com.lambton.fsdo.finalproject.entities.TMDbMovieDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TMDbService {

    private static final Logger logger = LoggerFactory.getLogger(TMDbService.class);

    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    @Value("${tmdb.api.image-base-url}")
    private String imageBaseUrl;

    public TMDbService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get trending movies for the day
     */
    public List<TMDbMovieDto> getTrendingMovies() {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl + "/trending/movie/day")
                    .queryParam("api_key", apiKey)
                    .toUriString();

            logger.debug("Fetching trending movies from: {}", url);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractMoviesFromResponse(response.getBody());

        } catch (RestClientException e) {
            logger.error("Error fetching trending movies", e);
            return Collections.emptyList();
        }
    }

    /**
     * Search movies by query
     */
    public List<TMDbMovieDto> searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl + "/search/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("query", query.trim())
                    .queryParam("include_adult", false)
                    .toUriString();

            logger.debug("Searching movies with query '{}' from: {}", query, url);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractMoviesFromResponse(response.getBody());

        } catch (RestClientException e) {
            logger.error("Error searching movies with query: {}", query, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get movie details by ID
     */
    public TMDbMovieDto getMovieDetails(Long movieId) {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl + "/movie/" + movieId)
                    .queryParam("api_key", apiKey)
                    .toUriString();

            logger.debug("Fetching movie details for ID {} from: {}", movieId, url);

            ResponseEntity<TMDbMovieDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    TMDbMovieDto.class
            );

            return response.getBody();

        } catch (RestClientException e) {
            logger.error("Error fetching movie details for ID: {}", movieId, e);
            return null;
        }
    }

    /**
     * Get popular movies
     */
    public List<TMDbMovieDto> getPopularMovies() {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl + "/movie/popular")
                    .queryParam("api_key", apiKey)
                    .toUriString();

            logger.debug("Fetching popular movies from: {}", url);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractMoviesFromResponse(response.getBody());

        } catch (RestClientException e) {
            logger.error("Error fetching popular movies", e);
            return Collections.emptyList();
        }
    }

    /**
     * Get now playing movies
     */
    public List<TMDbMovieDto> getNowPlayingMovies() {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl + "/movie/now_playing")
                    .queryParam("api_key", apiKey)
                    .toUriString();

            logger.debug("Fetching now playing movies from: {}", url);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractMoviesFromResponse(response.getBody());

        } catch (RestClientException e) {
            logger.error("Error fetching now playing movies", e);
            return Collections.emptyList();
        }
    }

    /**
     * Get the image base URL for building full image URLs
     */
    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    /**
     * Extract movies list from API response
     */
    @SuppressWarnings("unchecked")
    private List<TMDbMovieDto> extractMoviesFromResponse(Map<String, Object> responseBody) {
        if (responseBody == null || !responseBody.containsKey("results")) {
            return Collections.emptyList();
        }

        try {
            List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");
            return results.stream()
                    .map(this::mapToMovieDto)
                    .toList();
        } catch (Exception e) {
            logger.error("Error extracting movies from response", e);
            return Collections.emptyList();
        }
    }

    /**
     * Map raw movie data to TMDbMovieDto
     */
    private TMDbMovieDto mapToMovieDto(Map<String, Object> movieData) {
        TMDbMovieDto movie = new TMDbMovieDto();

        movie.setId(getLongValue(movieData, "id"));
        movie.setTitle(getStringValue(movieData, "title"));
        movie.setOverview(getStringValue(movieData, "overview"));
        movie.setPosterPath(getStringValue(movieData, "poster_path"));
        movie.setBackdropPath(getStringValue(movieData, "backdrop_path"));
        movie.setVoteAverage(getDoubleValue(movieData, "vote_average"));
        movie.setVoteCount(getIntegerValue(movieData, "vote_count"));
        movie.setOriginalLanguage(getStringValue(movieData, "original_language"));
        movie.setOriginalTitle(getStringValue(movieData, "original_title"));
        movie.setAdult(getBooleanValue(movieData, "adult"));
        movie.setPopularity(getDoubleValue(movieData, "popularity"));

        // Handle release date
        String releaseDateStr = getStringValue(movieData, "release_date");
        if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
            try {
                movie.setReleaseDate(java.time.LocalDate.parse(releaseDateStr));
            } catch (Exception e) {
                logger.warn("Could not parse release date: {}", releaseDateStr);
            }
        }

        return movie;
    }

    // Helper methods for safe type conversion
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }
}