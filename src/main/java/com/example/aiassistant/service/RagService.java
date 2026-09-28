package com.example.aiassistant.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagService {

    @Value("${rag.documents-path}")
    private String documentsPath;

    @Value("${rag.chunk-size}")
    private int chunkSize;

    @Value("${rag.chunk-overlap}")
    private int chunkOverlap;

    @Value("${rag.max-results}")
    private int maxResults;

    @Value("${rag.min-score}")
    private double minScore;

    private EmbeddingModel embeddingModel;
    private EmbeddingStore<TextSegment> embeddingStore;

    @PostConstruct
    public void init() {
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        embeddingStore = new InMemoryEmbeddingStore<>();
        loadDocuments();
    }

    private void loadDocuments() {
        try {
            Path path = Path.of(documentsPath);
            if (!path.toFile().exists()) {
                log.warn("Thư mục {} không tồn tại. Bỏ qua RAG.", documentsPath);
                return;
            }

            List<Document> documents = FileSystemDocumentLoader.loadDocuments(path);
            if (documents.isEmpty()) {
                log.warn("Không có tài liệu nào trong thư mục {}", documentsPath);
                return;
            }

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(DocumentSplitters.recursive(chunkSize, chunkOverlap))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();

            ingestor.ingest(documents);
            log.info("Đã load {} tài liệu vào RAG store", documents.size());
        } catch (Exception e) {
            log.error("Lỗi load tài liệu RAG", e);
        }
    }

    public String findContext(String question) {
        try {
            var queryEmbedding = embeddingModel.embed(question).content();
            var searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(maxResults)
                    .minScore(minScore)
                    .build();
            var matches = embeddingStore.search(searchRequest).matches();

            // LOG ĐỂ DEBUG
            log.debug("RAG tìm được {} chunks cho câu hỏi: {}", matches.size(), question);
            matches.forEach(m -> log.debug("Score: {} | Text: {}",
                    m.score(),
                    m.embedded().text().substring(0, Math.min(200, m.embedded().text().length()))));

            if (matches.isEmpty())
                return "";

            return matches.stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n\n---\n\n"));
        } catch (Exception e) {
            log.error("Lỗi tìm context RAG", e);
            return "";
        }
    }
}