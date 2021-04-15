package com.packt.productservice.rest;

import com.packt.common.api.core.product.Product;
import com.packt.common.api.core.product.ProductService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public Mono<Product> getProduct(@PathVariable("productId") int productId) {
        return productService.getProduct(productId);
    }


    @PostMapping()
    public Product createProduct(@RequestBody Product body) {
        return productService.createProduct(body);
    }


    @DeleteMapping({"/{productId}"})
    public void deleteProduct(@PathVariable int productId) {
        productService.deleteProduct(productId);

    }
}
