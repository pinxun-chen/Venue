package com.example.demo.service.impl;

import java.nio.file.Path;

import com.example.demo.model.entity.FileDetail;
import com.example.demo.model.entity.FileFooter;
import com.example.demo.repository.FileDetailRepository;
import com.example.demo.repository.FileFooterRepository;
import com.example.demo.service.ReplyCsvImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReplyCsvImportServiceImpl implements ReplyCsvImportService {

	@Autowired
	private FileFooterRepository fileFooterRepository;

	@Autowired
	private FileDetailRepository fileDetailRepository;

	private final boolean enableValidation = true;

	@Value("${app.reply.charset:UTF-8}")
	private String csvCharset;

	@Override
	@Transactional
	public Integer importReplyCsv(Path path) {
		
	    Charset cs = "MS950".equalsIgnoreCase(csvCharset) ?
	    		Charset.forName("MS950") : StandardCharsets.UTF_8;

	    try (BufferedReader br = Files.newBufferedReader(path, cs)) {
	        FileFooter footer = null;
	        List<FileDetail> toInsert = new ArrayList<>();

	        int dCountAll = 0;   // 原始檔案中的 D 筆數（含重複、最終不插入者）
	        int dSumAll   = 0;   // 原始檔案中的 D 請款金額加總（含重複）
	        int inserted  = 0;   // 真正要插入的筆數
	        int skippedDup = 0;  // 因重複略過的筆數

	        String line;
	        int lineNo = 0;

	        while ((line = br.readLine()) != null) {
	            lineNo++;
	            line = line.trim();
	            if (line.isEmpty()) continue;

	            // 這裡以逗號分隔
	            String[] cols = line.split(",", -1);
	            if (cols.length == 0) continue;

	            String tag = stripBom(cols[0]); // 用 tag 來判斷

	            if ("F".equalsIgnoreCase(tag)) {
	                // F, <總筆數>, <總金額>, <檔案日期>, <請款日期>
	                footer = FileFooter.builder()
	                        .totalCount(parseInt(cols[1]))
	                        .totalAmount(parseInt(cols[2]))
	                        .fileDate(cols[3])
	                        .payDate(cols[4])
	                        .build();

	            } else if ("D".equalsIgnoreCase(tag)) {
	                // D, <交易唯一>, <交易時間>, <商店>, <訂單>, <交易金額>, <折扣>, <請款金額>
	                String tradeUniqueId = cols[1];
	                int tradeAmount   = parseInt(cols[5]);
	                int discountAmount= parseInt(cols[6]);
	                int payAmount     = parseInt(cols[7]);

	                // 不論是否重複，統計都要 +1 / +金額（用來與 F 比對）
	                dCountAll++;
	                dSumAll += payAmount;

	                if (fileDetailRepository.existsByTradeUniqueId(tradeUniqueId)) {
	                    skippedDup++;
	                    continue; // 重複則不新增
	                }

	                FileDetail d = FileDetail.builder()
	                        .tradeUniqueId(tradeUniqueId)
	                        .tradeTime(cols[2])
	                        .merchantNo(cols[3])
	                        .orderNo(cols[4])
	                        .tradeAmount(tradeAmount)
	                        .discountAmount(discountAmount)
	                        .payAmount(payAmount)
	                        .build();

	                toInsert.add(d);
	                inserted++;
	            }
	        }

	        if (footer == null) {
	            throw new IllegalArgumentException("找不到檔尾(F)資料: " + path.getFileName());
	        }

	        // 先存 F 取得 id，再關聯 D
	        footer = fileFooterRepository.save(footer);
	        for (FileDetail d : toInsert) {
	        	d.setFooter(footer);
	        }
	        
	        fileDetailRepository.saveAll(toInsert);

	        // 拿「原始檔案的 D 統計」對 F，比較貼近對帳邏輯
	        if (enableValidation) {
	            if (!footer.getTotalCount().equals(dCountAll)) {
	                throw new IllegalStateException(
	                        "F 總筆數(" + footer.getTotalCount() + ") ≠ D 筆數(" + dCountAll + ") " +
	                        "[實際新增=" + inserted + ", 重複略過=" + skippedDup + "]"
	                );
	            }
	            if (!footer.getTotalAmount().equals(dSumAll)) {
	                throw new IllegalStateException(
	                        "F 總金額(" + footer.getTotalAmount() + ") != D 請款加總(" + dSumAll + ")"
	                );
	            }
	        }

	        return footer.getId();

	    } catch (Exception e) {
	        throw new RuntimeException("匯入 CSV 失敗（" + path.getFileName() + "）: " + e.getMessage(), e);
	    }
	}

	
	private String stripBom(String s) {
	    if (s == null) return null;
	    // \uFEFF 是 UTF-8 BOM
	    return s.replace("\uFEFF", "");
	}

	private int parseInt(String s) {
		return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
	}

}
