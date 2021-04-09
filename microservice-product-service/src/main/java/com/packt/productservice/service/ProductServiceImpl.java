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
        try {
            ProductEntity entity = mapper.map(body, ProductEntity.class);
            ProductEntity newEntity = repository.save(entity);

            LOG.debug("createProduct: entity created for productId: {}", body.getProductId());
            return mapper.map(newEntity, Product.class);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
        }
    }

    @Override
    public Product getProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        ProductEntity entity = repository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));

        Product response = mapper.map(entity, Product.class);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getProduct: found productId: {}", response.getProductId());

        return response;
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
    }
}
