package com.quangduy.product_manager_for_arius.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.es.ESProduct;

@Repository
public interface ESProductRepository extends ElasticsearchRepository<ESProduct, String> {

}
