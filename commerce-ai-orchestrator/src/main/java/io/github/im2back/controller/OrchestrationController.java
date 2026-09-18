package io.github.im2back.controller;

import io.github.im2back.orchestration.agent.OrchestratorAgent;
import io.github.im2back.memory.context.ConversationContext;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.concurrent.ConcurrentHashMap;

@Path("/assistant")
public class OrchestrationController {

    @Inject
    OrchestratorAgent orchestrator;

    @Inject
    ConversationContext context;

    private static final long DEDUPE_TTL_MS = 60_000;
    private static final ConcurrentHashMap<String, Long> PROCESSED_KEY_IDS = new ConcurrentHashMap<>();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String ask(@Context HttpHeaders httpHeaders, @Context HttpServerRequest request,
                      @HeaderParam("X-User-Name") String userName, @HeaderParam("debug") String debugKeyId, String question) {

        // DEDUPE por key-id: se este id já foi processado há pouco, é a MESMA mensagem
        // chegando de novo (Evolution/n8n reenviando). Cortamos aqui, de forma ATÔMICA
        // (putIfAbsent), para não rodar o agente 2x nem corromper a memória.
        if (debugKeyId != null && !debugKeyId.isBlank()) {
            long agora = System.currentTimeMillis();
            // limpa ids antigos para a memória não crescer sem limite
            PROCESSED_KEY_IDS.entrySet().removeIf(e -> agora - e.getValue() > DEDUPE_TTL_MS);

            if (PROCESSED_KEY_IDS.putIfAbsent(debugKeyId, agora) != null) {
                throw new WebApplicationException(
                        "Mensagem duplicada (key-id ja processado): " + debugKeyId,
                        Response.Status.CONFLICT);
            }
        }

        // memoryId não pode ser nulo: o ChatMemoryService usa como chave de mapa
        // (NullPointerException em computeIfAbsent se vier null). Se o header
        // X-User-Name não for enviado, usamos um valor padrão.
        String user = (userName == null || userName.isBlank()) ? "anonimo" : userName;

        // Propaga usuário/memoryId para os bridges que chamam os subagentes.
        context.setUsername(user);
        context.setMemoryId(user);

        String resposta = orchestrator.chat(user, question);

        return resposta;
    }
}
