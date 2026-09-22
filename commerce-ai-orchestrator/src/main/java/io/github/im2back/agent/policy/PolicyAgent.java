package io.github.im2back.agent.policy;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface PolicyAgent {

    @UserMessage("""
            Solicitação do usuário: {message}

            Usuário autenticado: {username}
            """)
    String handle(
            @MemoryId String memoryId,
            String message,
            String username
    );

}
