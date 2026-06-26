package com.sashimi.instructorapplication.infrastructure;

import com.sashimi.instructorapplication.application.port.DocxPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DocxAdapter implements DocxPort {

    @Override
    public List<String> extractMainCareers(byte[] fileBytes) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(fileBytes))) {
            List<String> careers = new ArrayList<>();
            boolean inCareerSection = false;

            for (IBodyElement element : doc.getBodyElements()) {
                if (element instanceof XWPFParagraph para) {
                    String text = para.getText().trim();
                    if (text.contains("주요 이력") || text.contains("주요이력")) {
                        inCareerSection = true;
                    }
                } else if (element instanceof XWPFTable table && inCareerSection) {
                    for (XWPFTableRow row : table.getRows()) {
                        if (row.getTableCells().size() < 2) continue;
                        String content = row.getCell(1).getText().trim();
                        if (content.isEmpty() || content.contains("내용") || content.contains("자격증, 수상")) continue;
                        careers.add(content);
                        if (careers.size() >= 5) break;
                    }
                    break;
                }
            }

            log.info("주요 이력 추출 결과: {}", careers);
            return careers;

        } catch (Exception e) {
            log.error("이력서 docx 파싱 중 예외 발생", e);
            return List.of();
        }
    }
}
