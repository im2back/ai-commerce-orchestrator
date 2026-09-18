package io.github.im2back.memory.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import io.github.im2back.memory.persistense.DatabaseChatMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ChatMemoryConfig {

    @Produces
    public ChatMemoryProvider chatMemoryProvider(
            DatabaseChatMemoryStore databaseChatMemoryStore) {

        return memoryId ->
                MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(databaseChatMemoryStore)
                        .build();
    }
}