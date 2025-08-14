package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "file_detail",
    indexes = { @Index(name = "idx_trade_unique_id", columnList = "trade_unique_id") },
    uniqueConstraints = { @UniqueConstraint(name = "uk_trade_unique_id", columnNames = "trade_unique_id") }
)
@Data
@Builder
public class FileDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 外鍵：一筆檔尾對多筆明細
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "footer_id", nullable = false)
    private FileFooter footer;

    @Column(name = "trade_unique_id", nullable = false, length = 50)
    private String tradeUniqueId;        // D: 交易唯一值

    @Column(name = "trade_time", nullable = false, length = 14)
    private String tradeTime;            // D: 交易時間

    @Column(name = "merchant_no", length = 20)
    private String merchantNo;           // D: 商店代號

    @Column(name = "order_no", length = 20)
    private String orderNo;              // D: 交易序號 

    @Column(name = "trade_amount", nullable = false)
    private Integer tradeAmount;         // D: 交易金額(折扣前)

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;      // D: 折扣金額

    @Column(name = "pay_amount", nullable = false)
    private Integer payAmount;           // D: 實際交易金額
}
