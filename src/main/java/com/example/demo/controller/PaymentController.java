package com.example.demo.controller;

import com.example.demo.model.dto.GetPaymentByKeyResp;
import com.example.demo.service.PaymentService;
import com.example.demo.response.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	// 接收 getPaymentByKey 的結果並儲存
	@PostMapping("/sync")
	public ResponseEntity<ApiResponse<Void>> syncPaymentResult(@RequestBody GetPaymentByKeyResp resp) {
		if (resp.getStatus() == 0 && resp.getData() != null) {
			for (GetPaymentByKeyResp.PaymentData data : resp.getData()) {
				paymentService.savePaymentRecord(data);
			}
			return ResponseEntity.ok(ApiResponse.success("付款資料同步成功", null));
		} else {
			return ResponseEntity.badRequest().body(ApiResponse.error(400, "付款資料同步失敗"));
		}
	}
}
