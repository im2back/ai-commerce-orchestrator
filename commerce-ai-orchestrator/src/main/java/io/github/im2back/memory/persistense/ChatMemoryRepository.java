package io.github.im2back.memory.persistense;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ChatMemoryRepository
        implements PanacheRepositoryBase<ChatMemoryEntity, String> {

    public Optional<ChatMemoryEntity> findByMemoryId(String memoryId) {
        return find("memoryId", memoryId)
                .firstResultOptional();
    }

    public boolean deleteByMemoryId(String memoryId) {
        return delete("memoryId", memoryId) > 0;
    }
}