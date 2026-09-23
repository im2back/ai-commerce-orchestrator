package io.github.im2back.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PolicySectionSplitter implements DocumentSplitter {

    @Override
    public List<TextSegment> split(Document document) {

        List<TextSegment> segments = new ArrayList<>();

        String[] sections = document.text().split("(?m)^##\\s+");

        for (String section : sections) {

            if (section.isBlank()) {
                continue;
            }

            String[] parts = section.split("\\R", 2);

            if (parts.length < 2) {
                continue;
            }

            String title = parts[0].trim();
            String content = parts[1].trim();

            // Ignora o conteúdo anterior à primeira seção ##
            if (!title.matches("^\\d+\\..*")) {
                continue;
            }

            Metadata metadata = new Metadata();

            metadata.put("dominio", "policy");
            metadata.put("categoria", normalizeCategory(title));

            String chunk = title + "\n\n" + content;

            segments.add(
                    TextSegment.from(chunk, metadata)
            );
        }

        return segments;
    }

    private String normalizeCategory(String title) {

        return Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceFirst("^\\d+\\.\\s*", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");
    }
}