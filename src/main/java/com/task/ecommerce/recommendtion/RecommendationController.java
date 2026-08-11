package com.task.ecommerce.recommendtion;

import com.task.ecommerce.recommendtion.dto.ProductRecommendtion;
import com.task.ecommerce.recommendtion.dto.RecommendationResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.utils.ReturnObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Recommendations", description = "Authenticated customer endpoints for personalized and cart-item product recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(
            summary = "Get personalized recommendations",
            description = "Returns AI-assisted recommendations based on the customer's purchase history. Uses popular active products as a fallback."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication token is missing or invalid")
    })
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

    @Operation(
            summary = "Get recommendations for a cart product",
            description = "Returns products commonly purchased with the specified product. The product must belong to the authenticated customer's cart; products already in the cart are excluded."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product recommendations returned successfully"),
            @ApiResponse(responseCode = "400", description = "The product is not in the customer's cart"),
            @ApiResponse(responseCode = "401", description = "Authentication token is missing or invalid")
    })
    @GetMapping("/product-recommendations")
    public ResponseEntity<?> getRecommendationsForProduct(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID of a product currently in the authenticated customer's cart", example = "1", required = true)
            @RequestParam Integer productId) {

        List<ProductRecommendtion> recommendationsProducts = recommendationService.getProductRecommendations(user.getId(), productId);

        ReturnObject response = ReturnObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Product recommendations fetched successfully.")
                .data(recommendationsProducts)
                .build();

        return ResponseEntity.ok(response);
    }
}
