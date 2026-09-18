package io.github.im2back.orchestration.memory.persistense;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DatabaseChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryRepository repository;

    public DatabaseChatMemoryStore(ChatMemoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        String id = memoryId.toString();

        List<ChatMessage> messages = repository.findByIdOptional(id)
                .map(entity ->
                        ChatMessageDeserializer.messagesFromJson(entity.messages))
                .orElseGet(List::of);

        return messages;
    }

    @Override
    @Transactional
    public void updateMessages(
            Object memoryId,
            List<ChatMessage> messages) {

        String id = memoryId.toString();

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

            return;
        }

        entity.messages = messagesJson;
        entity.updatedAt = LocalDateTime.now();

    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {

        String id = memoryId.toString();

        boolean deleted = repository.deleteById(id);

    }
}