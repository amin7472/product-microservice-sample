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

import static reactor.core.publisher.Mono.error;

import java.util.Random;

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
    public Mono<Product> getProduct(int productId, int delay, int faultPercent) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        return repository.findByProductId(productId)
                .switchIfEmpty(error(new NotFoundException("No product found for productId: " + productId)))
                .log()
                .map(e -> mapper.map(e, Product.class))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
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

    private void simulateDelay(int delay) {
        LOG.info("Sleeping for {} seconds...", delay);
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
        }
        LOG.info("Moving on...");
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            LOG.info("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.info("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }
    }

    private final Random randomNumberGenerator = new Random();
    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new RuntimeException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}
