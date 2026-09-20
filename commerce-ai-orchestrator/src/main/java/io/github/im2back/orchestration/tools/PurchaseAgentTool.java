package io.github.im2back.orchestration.tools;

import dev.langchain4j.agent.tool.Tool;
import io.github.im2back.agent.purchase.PurchaseAgent;
import io.github.im2back.memory.context.ConversationContext;
import io.github.im2back.observability.ObservabilityLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Bridge que expõe o PurchaseAgent como ferramenta para o orquestrador.
 */
@ApplicationScoped
public class PurchaseAgentTool {

    @Inject
    PurchaseAgent purchaseAgent;

    @Inject
    ObservabilityLogger observabilityLogger;

    @Inject
    ConversationContext context;

    @Tool("""
    Agente responsável por operações relacionadas ao processo de COMPRA.

    Use para:
    - registrar uma nova compra;
    - efetivar uma compra envolvendo cliente e produtos;
    - realizar a baixa de estoque como parte do processo de compra;
    - emitir a nota/fatura relacionada ao registro da compra;
    - desfazer ou estornar uma compra;
    - coordenar operações de cliente, produto e estoque quando necessárias para concluir ou reverter uma compra.

    Não use para:
    - consultar compras anteriores sem intenção de realizar uma nova operação sobre elas;
    - consultar histórico de compras de um cliente;
    - cadastrar ou consultar clientes fora do contexto de uma compra;
    - consultar ou manter produtos/estoque fora do contexto de uma compra.

    Repasse a mensagem do usuário exatamente como recebida,
    sem reformular, resumir ou completar.
    """)
    public String compra(String solicitacaoDoUsuario) {
        observabilityLogger.info(

                "agent.selected",
                "Subagente selecionado",
                "sourceAgent", "OrchestratorAgent",
                "agent", "PurchaseAgent",
                "tool", "purchase"
        );

        String resposta = purchaseAgent.handle(
                context.subMemoryId("compra"),
                solicitacaoDoUsuario,
                context.getUsername()
        );

        observabilityLogger.info(
                "tool.completed",
                "Subagente respondeu",
                "agent", "PurchaseAgent",
                "tool", "compra",
                "status", "success",
                "response", resposta
        );

        return resposta;
    }
}
