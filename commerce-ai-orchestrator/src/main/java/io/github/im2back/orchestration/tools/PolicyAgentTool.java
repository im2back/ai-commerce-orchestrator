package io.github.im2back.orchestration.tools;

import io.github.im2back.agent.inventory.InventoryAgent;
import io.github.im2back.memory.context.ConversationContext;
import io.github.im2back.observability.ObservabilityLogger;
import jakarta.inject.Inject;

public class PolicyAgentTool {

    @Inject
    PolicyAgent policyAgent;

    @Inject
    ObservabilityLogger observabilityLogger;

    @Inject
    ConversationContext context;

    @Tool("""
Agente responsável por consultas relacionadas a POLÍTICAS e REGRAS DO ESTABELECIMENTO.

Use para:
- consultar políticas de devolução e troca de produtos;
- consultar regras para compras a crédito;
- consultar prazos e condições de pagamento de compras a crédito;
- consultar regras e limites relacionados ao crédito de clientes;
- consultar políticas de preços e promoções;
- consultar regras para reserva de produtos;
- consultar políticas relacionadas à disponibilidade de estoque;
- consultar regras e condições para cancelamento de compras;
- consultar regras relacionadas ao cadastro de clientes;
- esclarecer dúvidas gerais sobre políticas, normas e regras comerciais do estabelecimento.

Não use para:
- cadastrar, consultar ou alterar dados de clientes;
- consultar dados atuais de produtos ou quantidade disponível em estoque;
- cadastrar, atualizar ou remover produtos;
- realizar movimentações de estoque;
- registrar ou efetivar uma nova compra;
- executar operações transacionais em nome do usuário.

Repasse a mensagem do usuário exatamente como recebida,
sem reformular, resumir ou completar.
""")
    public String policies(String solicitacaoDoUsuario) {

        observabilityLogger.info(

                "agent.selected",
                "Subagente selecionado",
                "sourceAgent", "OrchestratorAgent",
                "agent", "PolicyAgent",
                "tool", "estoque"
        );
        String resposta = policyAgent.handle(
                context.subMemoryId("policy"),
                solicitacaoDoUsuario,
                context.getUsername()
        );

        observabilityLogger.info(
                "tool.completed",
                "Subagente respondeu",
                "agent", "PolicyAgent",
                "tool", "estoque",
                "status", "success",
                "response", resposta
        );

        return resposta;
    }
}
