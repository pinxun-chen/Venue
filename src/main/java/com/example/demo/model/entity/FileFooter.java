package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_footer")
@Data
@Builder
public class FileFooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;              // F: 總筆數

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;             // F: 總金額

    @Column(name = "file_date", nullable = false, length = 8)
    private String fileDate;                 // F: 檔案日期 (yyyyMMdd)

    @Column(name = "pay_date", nullable = false, length = 8)
    private String payDate;                  // F: 請款日期 (yyyyMMdd)

    @OneToMany(mappedBy = "footer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FileDetail> details = new ArrayList<>();
}
