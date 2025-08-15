package com.example.demo.service.impl;

import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.ReplyCsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ReplyCsvImportServiceImpl implements ReplyCsvImportService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${app.reply.charset:UTF-8}")
    private String csvCharset;

    /** 從 PRACTISE_REPLY CSV 讀出 (vnotice_no -> trade_seq) 與 F 的 entry_date，更新到 payments */
    @Override
    @Transactional
    public Integer importReplyCsv(Path path) {
        Charset cs = "MS950".equalsIgnoreCase(csvCharset) ? Charset.forName("MS950") : StandardCharsets.UTF_8;

        String entryDate = null; // F 第5欄
        // 用 Map 收集：key = vnotice_no(D第2欄)，value = trade_seq(D第5欄)
        Map<String, String> rows = new LinkedHashMap<>();

        try (BufferedReader br = Files.newBufferedReader(path, cs)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length == 0) continue;

                String tag = stripBom(cols[0]);

                if ("D".equalsIgnoreCase(tag)) {
                    String vnoticeNo = cols[1].trim(); // 交易唯一值
                    String tradeSeq  = cols[4].trim(); // 交易序號
                    if (!vnoticeNo.isEmpty()) {
                        rows.put(vnoticeNo, tradeSeq);
                    }
                } else if ("F".equalsIgnoreCase(tag)) {
                    entryDate = cols[4].trim();        // 入帳日期 yyyyMMdd
                }
            }

            if (entryDate == null || entryDate.isEmpty()) {
                throw new IllegalArgumentException("找不到檔尾(F)入帳日期：" + path.getFileName());
            }

            // 逐筆更新 payments
            int updated = 0, notFound = 0;
            for (Map.Entry<String, String> e : rows.entrySet()) {
                String vnoticeNo = e.getKey();
                String tradeSeq  = e.getValue();

                int cnt = paymentRepository.updateSeqAndEntryDateByVnoticeNo(vnoticeNo, tradeSeq, entryDate);
                if (cnt == 1) updated++;
                else notFound++; // 找不到對應 vnotice_no 的付款單
            }

            // 你也可以把 notFound 寫到 log 便於追蹤
            // log.info("CSV {} 更新完成：updated={}, notFound={}", path.getFileName(), updated, notFound);

            return updated; // 回傳成功更新的筆數

        } catch (Exception e) {
            throw new RuntimeException("匯入 CSV 失敗（" + path.getFileName() + "）: " + e.getMessage(), e);
        }
    }

    private String stripBom(String s) {
        if (s == null) return null;
        return s.replace("\uFEFF", "");
    }
}
