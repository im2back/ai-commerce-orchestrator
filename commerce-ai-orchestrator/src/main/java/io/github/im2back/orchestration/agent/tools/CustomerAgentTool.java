package io.github.im2back.orchestration.agent.tools;

import dev.langchain4j.agent.tool.Tool;
import io.github.im2back.agent.customer.CustomerAgent;
import io.github.im2back.memory.context.ConversationContext;
import io.github.im2back.observability.ObservabilityLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Bridge que expõe o CustomerAgent como ferramenta para o orquestrador.
 */
@ApplicationScoped
public class CustomerAgentTool {

    @Inject
    CustomerAgent customerAgent;

    @Inject
    ObservabilityLogger observabilityLogger;

    @Inject
    ConversationContext context;

    @Tool("""
    Agente responsável por operações relacionadas a CLIENTES.

    Use para:
    - cadastrar clientes;
    - consultar clientes;
    - consultar dados cadastrais;
    - consultar histórico de compras;
    - gerar ou reenviar nota/fatura de compras existentes.

    Não use para:
    - registrar uma nova compra;
    - consultar ou alterar estoque de forma isolada.

    Repasse a mensagem do usuário exatamente como recebida.
    """)
    public String cliente(String solicitacaoDoUsuario) {

        observabilityLogger.info(
                "agent.selected",
                "Subagente selecionado",
                "sourceAgent", "OrchestratorAgent",
                "agent", "CustomerAgent",
                "tool", "cliente"
        );

        String resposta = customerAgent.handle(
                context.subMemoryId("cliente"),
                solicitacaoDoUsuario,
                context.getUsername()
        );

        observabilityLogger.info(
                "tool.completed",
                "Subagente respondeu",
                "agent", "CustomerAgent",
                "tool", "cliente",
                "status", "success",
                "response", resposta
        );

        return resposta;
    }
}
