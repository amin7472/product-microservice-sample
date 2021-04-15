package com.packt.productservice.service;

import com.mongodb.DuplicateKeyException;
import com.packt.common.api.core.product.Product;
import com.packt.common.api.core.product.ProductService;
import com.packt.common.util.exceptions.InvalidInputException;
import com.packt.common.util.exceptions.NotFoundException;
import com.packt.common.util.http.ServiceUtil;
import com.packt.productservice.persistence.ProductEntity;
import com.packt.productservice.persistence.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ModelMapper mapper;

    public ProductServiceImpl(ServiceUtil serviceUtil, ProductRepository repository, ModelMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Product createProduct(Product body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());

        ProductEntity entity = mapper.map(body, ProductEntity.class);
        Mono<Product> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .map(e -> mapper.map(e, Product.class));

        return newEntity.block();
    }

    @Override
    public Mono<Product> getProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log()
                .map(productEntity -> mapper.map(productEntity, Product.class))
                .map(product -> {
                    product.setServiceAddress(serviceUtil.getServiceAddress());
                    return product;
                });
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        repository
                .findByProductId(productId)
                .log()
                .map(productEntity -> repository.delete(productEntity))
                .flatMap(voidMono -> voidMono)
                .block();
    }
}
