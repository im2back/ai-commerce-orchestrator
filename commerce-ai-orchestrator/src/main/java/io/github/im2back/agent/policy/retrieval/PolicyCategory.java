package io.github.im2back.agent.policy.retrieval;

public enum PolicyCategory {

    POLITICA_DE_DEVOLUCAO("politica_de_devolucao"),
    POLITICA_DE_PAGAMENTO("politica_de_pagamento"),
    COMPRAS_A_PRAZO("compras_a_prazo"),
    POLITICA_DE_PRECOS("politica_de_precos"),
    RESERVA_DE_PRODUTOS("reserva_de_produtos"),
    DISPONIBILIDADE_DE_ESTOQUE("disponibilidade_de_estoque"),
    CANCELAMENTO_DE_COMPRA("cancelamento_de_compra"),
    CADASTRO_DO_CLIENTE("cadastro_do_cliente"),
    ATENDIMENTO_E_DUVIDAS("atendimento_e_duvidas");

    private final String metadataValue;

    PolicyCategory(String metadataValue) {
        this.metadataValue = metadataValue;
    }

    public String metadataValue() {
        return metadataValue;
    }
}