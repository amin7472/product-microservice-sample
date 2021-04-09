package com.packt.recommendationservice.mapper;

import com.packt.common.api.core.recommendation.Recommendation;
import com.packt.recommendationservice.persistence.RecommendationEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {


    /**
     * create been for mapping class
     *
     * @return {@link ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(Recommendation.class, RecommendationEntity.class).addMappings(mapper -> {
            mapper.map(Recommendation::getAuthor, RecommendationEntity::setAuthor);
            mapper.map(Recommendation::getContent, RecommendationEntity::setContent);
            mapper.map(Recommendation::getRate, RecommendationEntity::setRating);
            mapper.map(Recommendation::getRecommendationId, RecommendationEntity::setRecommendationId);
            mapper.map(Recommendation::getProductId, RecommendationEntity::setProductId);
            mapper.skip(RecommendationEntity::setId);
        });

        modelMapper.createTypeMap(RecommendationEntity.class, Recommendation.class).addMappings(mapper -> {
            mapper.map(RecommendationEntity::getAuthor, Recommendation::setAuthor);
            mapper.map(RecommendationEntity::getContent, Recommendation::setContent);
            mapper.map(RecommendationEntity::getRating, Recommendation::setRate);
            mapper.map(RecommendationEntity::getRecommendationId, Recommendation::setRecommendationId);
            mapper.map(RecommendationEntity::getProductId, Recommendation::setProductId);
        });

        return modelMapper;
    }
}
