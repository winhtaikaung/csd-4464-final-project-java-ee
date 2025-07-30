package com.lambton.fsdo.finalproject.controllers;




import com.lambton.fsdo.finalproject.entities.Movie;
import com.lambton.fsdo.finalproject.entities.TMDbMovieDto;
import com.lambton.fsdo.finalproject.services.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Home page - Display trending movies
     */
    @GetMapping
    public String home(Model model) {
        logger.info("Loading home page with trending movies");

        List<TMDbMovieDto> trendingMovies = movieService.getTrendingMovies();

        model.addAttribute("movies", trendingMovies);
        model.addAttribute("pageTitle", "Trending Movies");
        model.addAttribute("imageBaseUrl", movieService.getImageBaseUrl());
        model.addAttribute("currentSection", "trending");

        return "index";
    }

    /**
     * Popular movies page
     */
    @GetMapping("/popular")
    public String popular(Model model) {
        logger.info("Loading popular movies page");

        List<TMDbMovieDto> popularMovies = movieService.getPopularMovies();

        model.addAttribute("movies", popularMovies);
        model.addAttribute("pageTitle", "Popular Movies");
        model.addAttribute("imageBaseUrl", movieService.getImageBaseUrl());
        model.addAttribute("currentSection", "popular");

        return "index";
    }

    /**
     * Now playing movies page
     */
    @GetMapping("/now-playing")
    public String nowPlaying(Model model) {
        logger.info("Loading now playing movies page");

        List<TMDbMovieDto> nowPlayingMovies = movieService.getNowPlayingMovies();

        model.addAttribute("movies", nowPlayingMovies);
        model.addAttribute("pageTitle", "Now Playing");
        model.addAttribute("imageBaseUrl", movieService.getImageBaseUrl());
        model.addAttribute("currentSection", "now-playing");

        return "index";
    }

    /**
     * Search movies
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query, Model model) {
        logger.info("Searching movies with query: {}", query);

        List<TMDbMovieDto> searchResults = List.of();
        String pageTitle = "Search Movies";

        if (query != null && !query.trim().isEmpty()) {
            searchResults = movieService.searchMovies(query);
            pageTitle = "Search Results for: " + query;
        }

        model.addAttribute("movies", searchResults);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("searchQuery", query);
        model.addAttribute("imageBaseUrl", movieService.getImageBaseUrl());
        model.addAttribute("currentSection", "search");

        return "index";
    }

    /**
     * Movie detail page
     */
    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        logger.info("Loading movie details for ID: {}", id);

        TMDbMovieDto movie = movieService.getMovieDetails(id);
        if (movie == null) {
            logger.warn("Movie not found with ID: {}", id);
            return "redirect:/";
        }

        boolean isInFavorites = movieService.isInFavorites(id);

        model.addAttribute("movie", movie);
        model.addAttribute("isInFavorites", isInFavorites);
        model.addAttribute("imageBaseUrl", movieService.getImageBaseUrl());

        return "movie-detail";
    }

    /**
     * Favorites page
     */
    @GetMapping("/favorites")
    public String favorites(
            @RequestParam(value = "sort", defaultValue = "rating") String sortBy,
            @RequestParam(value = "search", required = false) String searchQuery,
            Model model) {

        logger.info("Loading favorites page with sort: {} and search: {}", sortBy, searchQuery);

        List<Movie> favoriteMovies;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            favoriteMovies = movieService.searchFavoriteMovies(searchQuery.trim());
        } else {
            favoriteMovies = switch (sortBy) {
                case "date" -> movieService.getFavoriteMoviesByReleaseDate();
                case "rating" -> movieService.getFavoriteMovies();
                default -> movieService.getFavoriteMovies();
            };
        }

        long totalFavorites = movieService.getFavoriteMoviesCount();

        model.addAttribute("favoriteMovies", favoriteMovies);
        model.addAttribute("totalFavorites", totalFavorites);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("imageBaseUrl", movieService.getImageBaseUrl());

        return "favorites";
    }

    /**
     * Add movie to favorites (AJAX)
     */
    @PostMapping("/favorites/add/{id}")
    @ResponseBody
    public String addToFavorites(@PathVariable Long id) {
        logger.info("Adding movie to favorites with ID: {}", id);

        boolean success = movieService.addToFavorites(id);
        return success ? "success" : "already_exists";
    }

    /**
     * Remove movie from favorites (AJAX)
     */
    @DeleteMapping("/favorites/remove/{id}")
    @ResponseBody
    public String removeFromFavorites(@PathVariable Long id) {
        logger.info("Removing movie from favorites with ID: {}", id);

        boolean success = movieService.removeFromFavorites(id);
        return success ? "success" : "not_found";
    }

    /**
     * Toggle favorite status (AJAX)
     */
    @PostMapping("/favorites/toggle/{id}")
    @ResponseBody
    public String toggleFavorite(@PathVariable Long id) {
        logger.info("Toggling favorite status for movie ID: {}", id);

        if (movieService.isInFavorites(id)) {
            boolean removed = movieService.removeFromFavorites(id);
            return removed ? "removed" : "error";
        } else {
            boolean added = movieService.addToFavorites(id);
            return added ? "added" : "error";
        }
    }

    /**
     * Check if movie is in favorites (AJAX)
     */
    @GetMapping("/favorites/check/{id}")
    @ResponseBody
    public boolean checkFavoriteStatus(@PathVariable Long id) {
        return movieService.isInFavorites(id);
    }

    /**
     * Error page
     */
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("errorMessage", "Something went wrong. Please try again.");
        return "error";
    }

    /**
     * Handle exceptions
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, RedirectAttributes redirectAttributes) {
        logger.error("Unexpected error occurred", e);
        redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "redirect:/";
    }
}