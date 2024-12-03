package com.quangduy.product_manager_for_arius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, String>, JpaSpecificationExecutor<Tag> {
    List<Tag> findByIdIn(List<String> listId);
}
