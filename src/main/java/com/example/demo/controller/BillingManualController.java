package com.example.demo.controller;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.ApiResponse;
import com.example.demo.service.BillingExportService;
import com.example.demo.service.MailHelper;

@RestController
@RequestMapping("/api/export")
public class BillingManualController {
	
	@Autowired
    private BillingExportService exportService;
	
	@Autowired
    private MailHelper mailHelper;
    
    @Value("${report.recipient}") 
    private String recipient;

    @GetMapping("/billing/run")
    public ResponseEntity<ApiResponse<String>> run(@RequestParam String billDate) throws Exception {
        LocalDate d = LocalDate.parse(billDate, DateTimeFormatter.BASIC_ISO_DATE);
        byte[] csv = exportService.generateCsvFor(d);
        Path file = exportService.writeCsvToFile(d, csv);
        mailHelper.sendWithAttachment(
            Arrays.stream(recipient.split(",")).map(String::trim).toList(),
            "[手動日結] " + billDate,
            "附件為 " + billDate + " 的結算 CSV",
            file
        );
        return ResponseEntity.ok(ApiResponse.success("日結寄送成功", file.toString()));
    }
}
