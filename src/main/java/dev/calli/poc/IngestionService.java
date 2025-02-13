package dev.calli.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class IngestionService implements CommandLineRunner {
    private final VectorStore vectorStore;
    private final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final ResourcePatternResolver resourcePatternResolver;

    public IngestionService(VectorStore vectorStore, ResourcePatternResolver resourcePatternResolver) {
        this.vectorStore = vectorStore;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public void run(String... args) throws IOException {
        List<Document> documents = new ArrayList<>();
        Resource[] resources = resourcePatternResolver.getResources("classpath:/docs/*");
        Arrays.stream(resources).forEach(resource -> {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            documents.addAll(tikaDocumentReader.read());
        });
        vectorStore.add(new TokenTextSplitter().split(documents));
        log.info("VectorStore Loaded with data!");
    }
}
