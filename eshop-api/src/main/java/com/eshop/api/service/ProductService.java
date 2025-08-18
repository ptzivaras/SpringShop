package com.eshop.api.service;

import com.eshop.api.dto.*;
import org.springframework.data.domain.*;
import com.eshop.api.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Page<com.eshop.api.dto.ProductResponse> search(String search, Long categoryId, Pageable pageable);
    com.eshop.api.dto.ProductResponse get(Long id);
    com.eshop.api.dto.ProductResponse create(com.eshop.api.dto.ProductRequest request);
    com.eshop.api.dto.ProductResponse update(Long id, com.eshop.api.dto.ProductRequest request);
    void delete(Long id);
    com.eshop.api.dto.ProductResponse uploadImage(Long id, MultipartFile file);

}
