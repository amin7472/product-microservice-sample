package com.packt.recommendationservice.rest;

import com.packt.common.api.core.recommendation.Recommendation;
import com.packt.common.api.core.recommendation.RecommendationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping()
    public Recommendation createRecommendation(@RequestBody Recommendation body) {
        return recommendationService.createRecommendation(body);
    }

    @GetMapping()
    public Flux<Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productId) {
        return recommendationService.getRecommendations(productId);
    }

    @DeleteMapping()
    public void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId) {
        recommendationService.deleteRecommendations(productId);
    }
}
