package com.packt.reviewservice.rest;

import com.packt.common.api.core.review.Review;
import com.packt.common.api.core.review.ReviewService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(
            value = {"/review"},
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public Review createReview(@RequestBody Review body) {
        return reviewService.createReview(body);
    }

    @GetMapping(
            value = {"/review"},
            produces = {"application/json"}
    )
    public Flux<Review> getReviews(@RequestParam(value = "productId", required = true) int productId) {
        return reviewService.getReviews(productId);
    }

    @DeleteMapping({"/review"})
    void deleteReviews(@RequestParam(value = "productId", required = true) int productId) {
        reviewService.deleteReviews(productId);
    }
}
