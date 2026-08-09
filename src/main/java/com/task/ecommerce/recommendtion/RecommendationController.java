package com.task.ecommerce.recommendtion;

import com.task.ecommerce.recommendtion.dto.ProductRecommendtion;
import com.task.ecommerce.recommendtion.dto.RecommendationResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@AuthenticationPrincipal User user) {

        List<RecommendationResponse> recommendations = recommendationService.getRecommendations(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Recommendations fetched successfully.")
                .data(recommendations)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product-recommendations")
    public ResponseEntity<?> getRecommendationsForProduct(
            @AuthenticationPrincipal User user) {

        List<ProductRecommendtion> recommendationsProducts = recommendationService.getProductRecommendations(user.getId());

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product recommendations fetched successfully.")
                .data(recommendationsProducts)
                .build();

        return ResponseEntity.ok(response);
    }
}