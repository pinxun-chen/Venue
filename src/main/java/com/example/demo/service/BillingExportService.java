package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class BillingExportService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	// 從 application.properties 讀取設定值
	@Value("${report.merchant-code}")
	private String merchantCode;
	
	@Value("${report.output-dir}")
	private String outputDir; // 輸出檔案的資料夾路徑

	// 日期格式：yyyyMMdd
	private static final DateTimeFormatter D8 = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

	// 產生指定日期的 CSV 報表（傳回 byte[] 格式）
	public byte[] generateCsvFor(LocalDate billDate) throws IOException {
		// 查詢範圍：當天 00:00 ~ 隔天 00:00
		LocalDate start = billDate;
		LocalDate end = billDate.plusDays(1);

		// 彙總 SQL ：計算當日交易彙總資料
		String sql = """
				SELECT COUNT(*) AS total_count,
				       COALESCE(SUM(pay_amount), 0) AS sum_before,
				       0 AS sum_discount,
				       COALESCE(SUM(pay_amount), 0) AS sum_actual
				FROM payments
				WHERE pay_datetime >= ? AND pay_datetime < ?
				  AND pay_status = 2
				    """;

		final int[] totalCount = { 0 };
		final long[] sumBefore = { 0 };
		final long[] sumDiscount = { 0 };
		final long[] sumActual = { 0 };

		// 執行查詢
		jdbcTemplate.query(sql, ps -> {
			ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
			ps.setTimestamp(2, Timestamp.valueOf(end.atStartOfDay()));
		}, rs -> {
			// 讀取查詢結果
			totalCount[0] = rs.getInt("total_count");
			sumBefore[0] = rs.getLong("sum_before");
			sumDiscount[0] = rs.getLong("sum_discount");
			sumActual[0] = rs.getLong("sum_actual");
		});

		// 轉成 yyyyMMdd 格式字串
		String fileDate = billDate.format(D8);
		String billDateStr = fileDate;

		// 建 CSV 檔案內容（UTF-8 + BOM，避免 Excel 開啟亂碼）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }); // BOM
		
		try (OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
				PrintWriter pw = new PrintWriter(osw)) {

			// H,<檔案日期>,<請款日期>
			pw.printf("H,%s,%s%n", fileDate, billDateStr);

			// D,<交易日期>,<商店代號>,<交易金額(折扣前總金額)>,<折扣金額>,<實際交易金額(總金額)>
			pw.printf("D,%s,%s,%d,%d,%d%n", billDateStr, merchantCode, sumBefore[0], sumDiscount[0], sumActual[0]);

			// F,<總筆數>
			pw.printf("F,%d%n", totalCount[0]);
		}
		// 回傳 CSV 內容（byte 陣列）
		return baos.toByteArray();
	}

	/** 把 CSV bytes 落地到檔案，回傳完整路徑 */
	public Path writeCsvToFile(LocalDate billDate, byte[] csvBytes) throws IOException {
		String bill = billDate.format(D8);
		// 取商店主碼（P10012-1 -> P10012），若沒設就 fallback 成 P10012
		String merchantForFile = (merchantCode != null && !merchantCode.isBlank()) ? merchantCode.split("-")[0]
				: "P10012";

		// 最終檔案名稱
		String fname = String.format("PRACTISE_%s_%s.csv", bill, merchantForFile);

		// 子資料夾名稱：日期
		String subDir = bill;
		Path dir = Paths.get(outputDir, subDir);
		Files.createDirectories(dir);

		// 寫入檔案（若存在則覆蓋）
		Path file = dir.resolve(fname);
		Files.write(file, csvBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		
		return file;
	}

}
