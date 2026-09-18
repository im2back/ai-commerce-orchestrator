package io.github.im2back.orchestration.memory.persistense;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_memory")
public class ChatMemoryEntity {

    @Id
    @Column(name = "memory_id", nullable = false)
    public String memoryId;

    @Column(name = "messages", columnDefinition = "TEXT", nullable = false)
    public String messages;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}