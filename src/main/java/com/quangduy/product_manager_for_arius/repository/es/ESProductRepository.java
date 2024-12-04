package com.quangduy.product_manager_for_arius.repository.es;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.es.ESProduct;

@Repository
public interface ESProductRepository extends ElasticsearchRepository<ESProduct, String> {
    // @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
    // Page<ESProduct> findByName(String name, Pageable pageable);

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"color\", \"tags.name\", \"category.name\"], \"fuzziness\": \"AUTO\"}}")
    Page<ESProduct> searchByQuery(String query, Pageable pageable);
}
