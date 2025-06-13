package com.pedrosilva.chatclient;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
public class PdfDocumentReader {
    private final ResourcePatternResolver resourceResolver;

    public PdfDocumentReader(ResourcePatternResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    List<Document> getDocsFromPdf() throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath:/pdfs/*.pdf");
        return Stream.of(resources).map(pdf -> new PagePdfDocumentReader(pdf, PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build()).read())
                .flatMap(List::stream)
                .toList();
    }
}