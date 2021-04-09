package com.packt.productservice.mapper;

import com.packt.common.api.core.product.Product;
import com.packt.productservice.persistence.ProductEntity;
import org.modelmapper.ModelMapper;
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

        modelMapper.createTypeMap(ProductEntity.class, Product.class).addMappings(mapper -> {
            mapper.map(ProductEntity::getProductId, Product::setProductId);
            mapper.map(ProductEntity::getName, Product::setName);
            mapper.map(ProductEntity::getWeight, Product::setWeight);
        });

        modelMapper.createTypeMap(Product.class, ProductEntity.class).addMappings(mapper -> {
            mapper.map(Product::getProductId, ProductEntity::setProductId);
            mapper.map(Product::getName, ProductEntity::setName);
            mapper.map(Product::getWeight, ProductEntity::setWeight);
        });

        return modelMapper;
    }
}
