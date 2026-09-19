package io.github.im2back.memory.persistense;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import io.github.im2back.observability.ObservabilityLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DatabaseChatMemoryStore implements ChatMemoryStore {

    @Inject
    ObservabilityLogger observabilityLogger;

    private final ChatMemoryRepository repository;

    public DatabaseChatMemoryStore(ChatMemoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        String id = memoryId.toString();

        observabilityLogger.info(
                "memory.get.started",
                "Recuperando memória da conversa",
                "source", "DatabaseChatMemoryStore",
                "memoryId", id
        );

        List<ChatMessage> messages = repository.findByIdOptional(id)
                .map(entity ->
                        ChatMessageDeserializer.messagesFromJson(entity.messages))
                .orElseGet(List::of);

        observabilityLogger.info(
                "memory.get.completed",
                "Memória da conversa recuperada",
                "source", "DatabaseChatMemoryStore",
                "memoryId", id,
                "messageCount", messages.size(),
                "found", !messages.isEmpty()
        );

        return messages;
    }

    @Override
    @Transactional
    public void updateMessages(
            Object memoryId,
            List<ChatMessage> messages) {

        String id = memoryId.toString();

        observabilityLogger.info(
                "memory.update.started",
                "Atualizando memória da conversa",
                "source", "DatabaseChatMemoryStore",
                "memoryId", id,
                "messageCount", messages.size()
        );

        String messagesJson =
                ChatMessageSerializer.messagesToJson(messages);

        ChatMemoryEntity entity =
                repository.findById(id);

        if (entity == null) {

            entity = new ChatMemoryEntity();
            entity.memoryId = id;
            entity.messages = messagesJson;
            entity.updatedAt = LocalDateTime.now();

            repository.persist(entity);

            observabilityLogger.info(
                    "memory.created",
                    "Memória da conversa criada",
                    "source", "DatabaseChatMemoryStore",
                    "memoryId", id,
                    "messageCount", messages.size()
            );

            return;
        }

        entity.messages = messagesJson;
        entity.updatedAt = LocalDateTime.now();

        observabilityLogger.info(
                "memory.updated",
                "Memória da conversa atualizada",
                "source", "DatabaseChatMemoryStore",
                "memoryId", id,
                "messageCount", messages.size()
        );
    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {

        String id = memoryId.toString();

        observabilityLogger.info(
                "memory.delete.started",
                "Removendo memória da conversa",
                "source", "DatabaseChatMemoryStore",
                "memoryId", id
        );

        boolean deleted = repository.deleteById(id);

        observabilityLogger.info(
                "memory.delete.completed",
                "Remoção da memória da conversa finalizada",
                "source", "DatabaseChatMemoryStore",
                "memoryId", id,
                "deleted", deleted
        );
    }
}