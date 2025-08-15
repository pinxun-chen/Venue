package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Optional<Payment> findByOrderNo(String orderNo);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Payment p SET p.tradeSeq = :tradeSeq, p.entryDate = :entryDate WHERE p.vnoticeNo = :vnoticeNo")
    int updateSeqAndEntryDateByVnoticeNo(@Param("vnoticeNo") String vnoticeNo,
                                         @Param("tradeSeq")  String tradeSeq,
                                         @Param("entryDate") String entryDate);
}
