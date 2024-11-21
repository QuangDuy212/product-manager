package com.quangduy.product_manager_for_arius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quangduy.product_manager_for_arius.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, String> {

}
