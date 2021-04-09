package com.packt.reviewservice.mapper;

import com.packt.common.api.core.review.Review;
import com.packt.reviewservice.persistence.ReviewEntity;
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
        modelMapper.createTypeMap(Review.class, ReviewEntity.class).addMappings(mapper -> {
            mapper.map(Review::getProductId, ReviewEntity::setProductId);
            mapper.map(Review::getAuthor, ReviewEntity::setAuthor);
            mapper.map(Review::getContent, ReviewEntity::setContent);
            mapper.map(Review::getReviewId, ReviewEntity::setReviewId);
            mapper.map(Review::getSubject, ReviewEntity::setSubject);
        });
        modelMapper.createTypeMap(ReviewEntity.class, Review.class).addMappings(mapper -> {
            mapper.map(ReviewEntity::getProductId, Review::setProductId);
            mapper.map(ReviewEntity::getAuthor, Review::setAuthor);
            mapper.map(ReviewEntity::getContent, Review::setContent);
            mapper.map(ReviewEntity::getReviewId, Review::setReviewId);
            mapper.map(ReviewEntity::getSubject, Review::setSubject);
        });


        return modelMapper;
    }
}
