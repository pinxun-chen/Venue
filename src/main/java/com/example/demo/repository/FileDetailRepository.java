package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.FileDetail;

public interface FileDetailRepository extends JpaRepository<FileDetail, Integer> {

	boolean existsByTradeUniqueId(String tradeUniqueId);
}
