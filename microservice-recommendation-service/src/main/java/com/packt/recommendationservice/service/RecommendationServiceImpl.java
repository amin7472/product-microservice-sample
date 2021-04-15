package com.packt.recommendationservice.service;

import com.mongodb.DuplicateKeyException;
import com.packt.common.api.core.recommendation.Recommendation;
import com.packt.common.api.core.recommendation.RecommendationService;
import com.packt.common.util.exceptions.InvalidInputException;
import com.packt.common.util.http.ServiceUtil;
import com.packt.recommendationservice.persistence.RecommendationEntity;
import com.packt.recommendationservice.persistence.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final ServiceUtil serviceUtil;
    private final ModelMapper mapper;
    private final RecommendationRepository recommendationRepository;

    public RecommendationServiceImpl(ServiceUtil serviceUtil, ModelMapper mapper, RecommendationRepository recommendationRepository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.recommendationRepository = recommendationRepository;
    }


    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            RecommendationEntity entity = mapper.map(body, RecommendationEntity.class);
            Mono<Recommendation> newEntity = recommendationRepository
                    .save(entity)
                    .log()
                    .onErrorMap(DuplicateKeyException.class,
                            ex -> new InvalidInputException("Duplicate key, Product Id: "
                                    + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId()))
                    .map(e -> mapper.map(e, Recommendation.class));

            log.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return newEntity.block();

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
        }
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
        return recommendationRepository
                .findByProductId(productId)
                .log()
                .map(recommendationEntity -> mapper.map(recommendationEntity, Recommendation.class))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    @Override
    public void deleteRecommendations(int productId) {
        log.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId));
    }
}
