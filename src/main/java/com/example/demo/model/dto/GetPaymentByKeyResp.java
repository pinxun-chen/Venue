package com.example.demo.model.dto;

import lombok.Data;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
@Data
public class GetPaymentByKeyResp {
    private int status;
    private String message;
    private List<PaymentData> data;

    @Data
    public static class PaymentData {
        private String order_no;
        private String channel_name;
        private String pay_status;
        private String vnotice_no;
        private String order_amount;
        private String pay_amount;
        private String pay_datetime;
        private String item_name;
        private String pay_status_desc;
        private String channel_id;
    }
}

