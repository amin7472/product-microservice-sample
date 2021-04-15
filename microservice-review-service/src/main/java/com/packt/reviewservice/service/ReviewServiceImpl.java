package com.packt.reviewservice.service;

import com.packt.common.api.core.review.Review;
import com.packt.common.api.core.review.ReviewService;
import com.packt.common.util.exceptions.InvalidInputException;
import com.packt.common.util.http.ServiceUtil;
import com.packt.reviewservice.persistence.ReviewEntity;
import com.packt.reviewservice.persistence.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.reactivestreams.Publisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINE;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ModelMapper mapper;
    private final Scheduler scheduler;


    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewRepository repository, ModelMapper mapper, Scheduler scheduler) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.scheduler = scheduler;
    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity entity = mapper.map(body, ReviewEntity.class);
            ReviewEntity newEntity = repository.save(entity);

            log.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.map(newEntity, Review.class);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    @Override
    public Flux<Review> getReviews(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        log.info("Will get reviews for product with id={}", productId);

        return asyncFlux(() -> Flux.fromIterable(getByProductId(productId))).log(null, FINE);
    }

    @Override
    public void deleteReviews(int productId) {
        log.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }

    protected List<Review> getByProductId(int productId) {

        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = entityList
                .stream()
                .map(reviewEntity -> mapper.map(reviewEntity, Review.class))
                .collect(Collectors.toList());
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        log.debug("getReviews: response size: {}", list.size());
        return list;
    }

    private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }
}
