package com.example.demo.service;

import com.example.demo.model.dto.GetPaymentByKeyResp.PaymentData;

public interface PaymentService {

	public void savePaymentRecord(PaymentData data);
}
