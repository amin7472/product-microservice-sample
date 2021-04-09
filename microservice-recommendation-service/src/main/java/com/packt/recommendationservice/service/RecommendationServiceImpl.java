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

import java.util.List;
import java.util.stream.Collectors;

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
            RecommendationEntity newEntity = recommendationRepository.save(entity);

            log.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return mapper.map(newEntity, Recommendation.class);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<RecommendationEntity> entityList = recommendationRepository.findByProductId(productId);
        List<Recommendation> list = entityList
                .stream()
                .map(recommendationEntity -> mapper.map(recommendationEntity, Recommendation.class))
                .collect(Collectors.toList());
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        log.debug("getRecommendations: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRecommendations(int productId) {
        log.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId));
    }
}
