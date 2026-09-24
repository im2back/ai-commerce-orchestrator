package io.github.im2back.agent.policy.retrieval;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.github.im2back.observability.ObservabilityLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PolicyContentRetriever {

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    PolicyCategoryClassifier policyCategoryClassifier;

    @Inject
    ObservabilityLogger observabilityLogger;

    @Inject
    EmbeddingModel embeddingModel;

    @Produces
    public ContentRetriever contentRetriever() {

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .dynamicFilter(query -> createFilter(query.text()))
                .maxResults(5)
                .minScore(0.7)
                .build();

        return query -> {

            observabilityLogger.info(
                    "rag.retrieval.started",
                    "Recuperacao RAG iniciada",
                    "query", query.text()
            );

            var contents = retriever.retrieve(query);

            observabilityLogger.info(
                    "rag.retrieval.completed",
                    "Recuperacao RAG concluida",
                    "query", query.text(),
                    "totalContents", contents.size()
            );

            contents.forEach(content ->
                    observabilityLogger.info(
                            "rag.content.retrieved",
                            "Conteudo recuperado pelo RAG",
                            "text", content.textSegment().text(),
                            "metadata", content.textSegment().metadata().toString()
                    )
            );

            return contents;
        };
    }

    private Filter createFilter(String userMessage) {

        List<PolicyCategory> categories =
                policyCategoryClassifier.classify(userMessage);

        if (categories == null || categories.isEmpty()) {
            return null;
        }

        List<String> metadataCategories = categories.stream()
                .map(PolicyCategory::metadataValue)
                .toList();

        return MetadataFilterBuilder.metadataKey("categoria")
                .isIn(metadataCategories);
    }
}