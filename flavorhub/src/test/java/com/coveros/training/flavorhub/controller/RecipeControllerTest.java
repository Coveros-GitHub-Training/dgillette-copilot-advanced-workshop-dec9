package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for RecipeController rating endpoint
 */
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private RecipeService recipeService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Recipe testRecipe;
    
    @BeforeEach
    void setUp() {
        testRecipe = new Recipe("Pasta", "Italian pasta dish", 10, 15, 4, "Easy", "Italian");
        testRecipe.setId(1L);
        testRecipe.setAverageRating(4.5);
        testRecipe.setRatingCount(10);
    }
    
    @Test
    void testRateRecipe_WhenValidRating_ThenReturnsOk() throws Exception {
        // Arrange
        Recipe updatedRecipe = new Recipe("Pasta", "Italian pasta dish", 10, 15, 4, "Easy", "Italian");
        updatedRecipe.setId(1L);
        updatedRecipe.setAverageRating(4.54);
        updatedRecipe.setRatingCount(11);
        
        when(recipeService.addRating(eq(1L), eq(5))).thenReturn(updatedRecipe);
        
        String requestBody = "{\"rating\": 5}";
        
        // Act & Assert
        mockMvc.perform(put("/api/recipes/1/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.averageRating").value(4.54))
                .andExpect(jsonPath("$.ratingCount").value(11));
    }
    
    @Test
    void testRateRecipe_WhenInvalidRatingTooLow_ThenReturnsBadRequest() throws Exception {
        // Arrange
        when(recipeService.addRating(eq(1L), eq(0)))
            .thenThrow(new IllegalArgumentException("Rating must be between 1 and 5"));
        
        String requestBody = "{\"rating\": 0}";
        
        // Act & Assert
        mockMvc.perform(put("/api/recipes/1/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRateRecipe_WhenInvalidRatingTooHigh_ThenReturnsBadRequest() throws Exception {
        // Arrange
        when(recipeService.addRating(eq(1L), eq(6)))
            .thenThrow(new IllegalArgumentException("Rating must be between 1 and 5"));
        
        String requestBody = "{\"rating\": 6}";
        
        // Act & Assert
        mockMvc.perform(put("/api/recipes/1/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRateRecipe_WhenRecipeNotFound_ThenReturnsNotFound() throws Exception {
        // Arrange
        when(recipeService.addRating(eq(999L), eq(5)))
            .thenThrow(new RuntimeException("Recipe not found"));
        
        String requestBody = "{\"rating\": 5}";
        
        // Act & Assert
        mockMvc.perform(put("/api/recipes/999/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testRateRecipe_WhenValidRating1_ThenReturnsOk() throws Exception {
        // Arrange - test edge case with rating of 1
        Recipe updatedRecipe = new Recipe("Pasta", "Italian pasta dish", 10, 15, 4, "Easy", "Italian");
        updatedRecipe.setId(1L);
        updatedRecipe.setAverageRating(4.18);
        updatedRecipe.setRatingCount(11);
        
        when(recipeService.addRating(eq(1L), eq(1))).thenReturn(updatedRecipe);
        
        String requestBody = "{\"rating\": 1}";
        
        // Act & Assert
        mockMvc.perform(put("/api/recipes/1/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
}
