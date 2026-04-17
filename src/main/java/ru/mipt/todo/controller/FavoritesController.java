package ru.mipt.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.Set;

/**
 * Контроллер для избранных задач (через HttpSession)
 * и настроек отображения (через Cookie).
 */
@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Избранные задачи и настройки")
public class FavoritesController {

    private static final String FAVORITES_KEY = "favorites";
    private static final String VIEW_COOKIE = "view_mode";

    @PostMapping("/{taskId}")
    @Operation(summary = "Добавить задачу в избранное")
    public ResponseEntity<Set<Long>> addFavorite(@PathVariable Long taskId, HttpSession session) {
        Set<Long> favorites = getFavorites(session);
        favorites.add(taskId);
        session.setAttribute(FAVORITES_KEY, favorites);
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Убрать задачу из избранного")
    public ResponseEntity<Set<Long>> removeFavorite(@PathVariable Long taskId, HttpSession session) {
        Set<Long> favorites = getFavorites(session);
        favorites.remove(taskId);
        session.setAttribute(FAVORITES_KEY, favorites);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping
    @Operation(summary = "Получить список избранных")
    public ResponseEntity<Set<Long>> getFavoritesList(HttpSession session) {
        return ResponseEntity.ok(getFavorites(session));
    }

    @PostMapping("/view/{mode}")
    @Operation(summary = "Установить режим отображения (compact/detailed)")
    public ResponseEntity<String> setViewMode(@PathVariable String mode,
                                              HttpServletResponse response) {
        if (!"compact".equals(mode) && !"detailed".equals(mode)) {
            return ResponseEntity.badRequest().body("Mode must be 'compact' or 'detailed'");
        }
        Cookie cookie = new Cookie(VIEW_COOKIE, mode);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return ResponseEntity.ok("View mode set to: " + mode);
    }

    @GetMapping("/view")
    @Operation(summary = "Получить текущий режим отображения")
    public ResponseEntity<String> getViewMode(
            @CookieValue(name = VIEW_COOKIE, defaultValue = "detailed") String viewMode) {
        return ResponseEntity.ok(viewMode);
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getFavorites(HttpSession session) {
        Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_KEY);
        if (favorites == null) {
            favorites = new HashSet<>();
        }
        return favorites;
    }
}
