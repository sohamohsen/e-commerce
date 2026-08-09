package com.task.ecommerce.recommendtion;

import com.task.ecommerce.ai.AiClient;
import com.task.ecommerce.entity.CartItem;
import com.task.ecommerce.recommendtion.dto.ProductRecommendtion;
import com.task.ecommerce.recommendtion.dto.RecommendationResponse;
import com.task.ecommerce.entity.OrderItem;
import com.task.ecommerce.entity.Product;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.repository.CartItemRepository;
import com.task.ecommerce.repository.OrderItemRepository;
import com.task.ecommerce.repository.OrderRepository;
import com.task.ecommerce.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final AiClient aiClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<RecommendationResponse> getRecommendations(Integer userId) {

        List<Integer> purchasedProductIds = getPurchasedProductIds(userId);

        List<String> purchasedNames = purchasedProductIds.stream()
                .map(productRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(opt -> opt.get().getName())
                .distinct()
                .limit(10)
                .toList();

        List<Product> candidateProducts = productRepository.findTop30ByIsActiveTrueAndQuantityGreaterThanOrderByCreatedAtDesc(0)
                .stream()
                .filter(p -> !purchasedProductIds.contains(p.getId()))
                .toList();

        if (purchasedNames.isEmpty()) {
            return fallbackRecommendations(candidateProducts);
        }

        String prompt = buildPrompt(purchasedNames, candidateProducts);

        try {
            String aiResponse = aiClient.generateText(prompt);
            return parseAiResponse(aiResponse, candidateProducts);
        } catch (Exception e) {
            log.error("AI recommendation failed, falling back", e);
            return fallbackRecommendations(candidateProducts);
        }
    }

    private List<Integer> getPurchasedProductIds(Integer userId) {
        List<Integer> orderIds = orderRepository.findOrderIdsByUserId(userId);
        if (orderIds.isEmpty()) return List.of();

        return orderItemRepository.findByOrderIdIn(orderIds)
                .stream()
                .map(OrderItem::getProductId)
                .distinct()
                .toList();
    }

    private String buildPrompt(List<String> purchasedNames, List<Product> candidates) {
        String purchased = String.join(", ", purchasedNames);
        String catalog = candidates.stream()
                .map(p -> p.getId() + ": " + p.getName() + " (" + p.getCategoryId() + ")")
                .collect(Collectors.joining("\n"));

        return """
            You are a product recommendation engine for an e-commerce catalog system.
            This is anonymized product data, not personal information.

            Items in the shopping history: %s

            Catalog items available (id: name (categoryId)):
            %s

            Task: pick the 5 catalog item IDs most similar in category/type to the shopping history items.

            Respond with ONLY a JSON array, no introduction, no explanation, no text before or after:
            [{"productId": 1, "reason": "short reason here"}]
            """.formatted(purchased, catalog);
    }

    private List<RecommendationResponse> parseAiResponse(String aiResponse, List<Product> candidates) {
        try {
            String jsonOnly = extractJsonArray(aiResponse);

            List<RecommendationResponse> parsed = objectMapper.readValue(
                    jsonOnly, objectMapper.getTypeFactory().constructCollectionType(List.class, RecommendationResponse.class)
            );

            var validIds = candidates.stream().map(Product::getId).collect(Collectors.toSet());
            return parsed.stream()
                    .filter(r -> validIds.contains(r.productId()))
                    .toList();

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", aiResponse, e);
            return fallbackRecommendations(candidates);
        }
    }

    private String extractJsonArray(String text) {
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');

        if (start == -1 || end == -1 || end < start) {
            throw new IllegalArgumentException("No JSON array found in AI response");
        }

        return text.substring(start, end + 1);
    }

    private List<RecommendationResponse> fallbackRecommendations(List<Product> candidates) {
        return candidates.stream()
                .limit(5)
                .map(p -> new RecommendationResponse(p.getId(), "Popular product"))
                .toList();
    }

    public List<ProductRecommendtion> getProductRecommendations(Integer userId, Integer productId) {

        if (cartItemRepository.findByUserIdAndProductId(userId, productId).isEmpty()) {
            throw new BadRequestException("Product is not in your cart.");
        }

        List<Integer> productIds =
                cartItemRepository.findCollaborativeRecommendationsForProduct(userId, productId);

        List<Product> products =
                productRepository.findAllByIdIn(productIds);

        return products.stream()
                .map(product -> new ProductRecommendtion(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription()
                ))
                .toList();
    }
}
