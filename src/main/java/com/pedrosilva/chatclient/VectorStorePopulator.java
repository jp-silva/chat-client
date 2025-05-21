package com.pedrosilva.chatclient;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VectorStorePopulator {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final PdfDocumentReader pdfDocumentReader;

    public VectorStorePopulator(VectorStore vectorStore, JdbcTemplate jdbcTemplate, PdfDocumentReader pdfDocumentReader) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.pdfDocumentReader = pdfDocumentReader;

        this.init();
    }

    void init() {
        jdbcTemplate.update("delete from vector_store");

        List<Document> documents = pdfDocumentReader.getDocsFromPdfWithCatalog();
        var textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(documents));
    }
}
