package io.github.im2back.orchestration.agent.tools;

import dev.langchain4j.agent.tool.Tool;
import io.github.im2back.agent.inventory.InventoryAgent;
import io.github.im2back.memory.context.ConversationContext;
import io.github.im2back.observability.ObservabilityLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Bridge que expõe o InventoryAgent como ferramenta para o orquestrador.
 */
@ApplicationScoped
public class InventoryAgentTool {

    @Inject
    InventoryAgent inventoryAgent;

    @Inject
    ObservabilityLogger observabilityLogger;

    @Inject
    ConversationContext context;

    @Tool("""
    Agente responsável por operações relacionadas a PRODUTOS e ESTOQUE.

    Use para:
    - listar ou consultar produtos;
    - cadastrar produtos;
    - atualizar dados de produtos;
    - remover produtos;
    - consultar quantidade disponível em estoque;
    - dar baixa no estoque;
    - estornar movimentações de estoque;
    - repor estoque;
    - realizar manutenções de produto ou estoque fora do contexto de uma nova compra.

    Não use para:
    - cadastrar, consultar ou alterar dados de clientes;
    - consultar histórico de compras de clientes;
    - registrar ou efetivar uma nova compra.

    Repasse a mensagem do usuário exatamente como recebida,
    sem reformular, resumir ou completar.
    """)
    public String estoque(String solicitacaoDoUsuario) {

        observabilityLogger.info(

                "agent.selected",
                "Subagente selecionado",
                "sourceAgent", "OrchestratorAgent",
                "agent", "EstoqueAgent",
                "tool", "estoque"
        );
        String resposta = inventoryAgent.handle(
                context.subMemoryId("estoque"),
                solicitacaoDoUsuario,
                context.getUsername()
        );

        observabilityLogger.info(
                "tool.completed",
                "Subagente respondeu",
                "agent", "InventoryAgent",
                "tool", "estoque",
                "status", "success",
                "response", resposta
        );

        return resposta;
    }
}
