package io.github.im2back.memory.persistense.context;

import jakarta.enterprise.context.RequestScoped;

/**
 * Contexto por requisição, preenchido pelo controller.
 *
 * Serve para propagar o memoryId e o usuário autenticado até os beans-bridge
 * que expõem os subagentes como ferramentas, já que o orquestrador não
 * repassa esses metadados nos argumentos gerados pela LLM.
 */
@RequestScoped
public class ConversationContext {

    private String memoryId;
    private String username;

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Deriva um memoryId isolado por subagente, para que cada fluxo
     * (cliente, estoque, compra) mantenha o próprio histórico de confirmação.
     */
    public String subMemoryId(String agentSuffix) {
        return memoryId + ":" + agentSuffix;
    }
}
