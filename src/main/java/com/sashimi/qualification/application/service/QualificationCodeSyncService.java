package com.sashimi.qualification.application.service;

import com.sashimi.qualification.infrastructure.persistence.QualificationCodeJpaEntity;
import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationCodeRepository;
import com.sashimi.qualification.infrastructure.publicdata.QualificationCodeApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class QualificationCodeSyncService {

    private static final String SUCCESS_CODE = "00";

    private final QualificationCodeApiClient apiClient;
    private final SpringDataQualificationCodeRepository repository;

    public QualificationCodeSyncService(
            QualificationCodeApiClient apiClient,
            SpringDataQualificationCodeRepository repository
    ) {
        this.apiClient = apiClient;
        this.repository = repository;
    }

    public int sync() {
        String responseText = apiClient.fetchCodes();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            Document document = factory
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(responseText)));

            String resultCode = getTextContent(
                    document,
                    "resultCode"
            );

            String resultMsg = getTextContent(
                    document,
                    "resultMsg"
            );

            if (!SUCCESS_CODE.equals(resultCode)) {
                throw new IllegalStateException(
                        "Qualification code API failed: "
                                + resultCode
                                + " / "
                                + resultMsg
                );
            }

            NodeList items = document.getElementsByTagName(
                    "item"
            );

            int syncedCount = 0;
            LocalDateTime syncedAt = LocalDateTime.now();

            for (int index = 0; index < items.getLength(); index++) {
                Node itemNode = items.item(index);

                if (itemNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element item = (Element) itemNode;

                upsertCode(
                        item,
                        syncedAt
                );

                syncedCount++;
            }

            log.info(
                    "Qualification codes synced: syncedCount={}",
                    syncedCount
            );

            return syncedCount;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to sync qualification codes",
                    e
            );
        }
    }

    private void upsertCode(
            Element item,
            LocalDateTime syncedAt
    ) {
        String jmCd = childText(item, "jmcd");
        if (jmCd == null || jmCd.isBlank()) {
            return;
        }
        String qualificationName = childText(item, "jmfldnm");
        String qualificationTypeCode = childText(item, "qualgbcd");
        String qualificationTypeName = childText(item, "qualgbnm");
        String seriesCode = childText(item, "seriescd");
        String seriesName = childText(item, "seriesnm");
        String majorFieldCode = childText(item, "obligfldcd");
        String majorFieldName = childText(item, "obligfldnm");
        String middleFieldCode = childText(item, "mdobligfldcd");
        String middleFieldName = childText(item, "mdobligfldnm");

        QualificationCodeJpaEntity entity = repository.findByJmCd(
                        jmCd
                )
                .orElseGet(() ->
                        new QualificationCodeJpaEntity(
                                jmCd,
                                qualificationName,
                                qualificationTypeCode,
                                qualificationTypeName,
                                seriesCode,
                                seriesName,
                                majorFieldCode,
                                majorFieldName,
                                middleFieldCode,
                                middleFieldName,
                                syncedAt
                        )
                );

        entity.updateFromSync(
                qualificationName,
                qualificationTypeCode,
                qualificationTypeName,
                seriesCode,
                seriesName,
                majorFieldCode,
                majorFieldName,
                middleFieldCode,
                middleFieldName,
                syncedAt
        );

        repository.save(entity);
    }

    private String getTextContent(
            Document document,
            String tagName
    ) {
        NodeList nodes = document.getElementsByTagName(
                tagName
        );

        if (nodes.getLength() == 0) {
            return "";
        }

        return nodes.item(0).getTextContent();
    }

    private String childText(
            Element element,
            String tagName
    ) {
        NodeList nodes = element.getElementsByTagName(
                tagName
        );

        if (nodes.getLength() == 0) {
            return "";
        }

        return nodes.item(0).getTextContent();
    }
}