package io.github.im2back.agent.policy.retrieval;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

import java.util.List;

@RegisterAiService
public interface PolicyCategoryClassifier {

    @SystemMessage("""
            Classifique a mensagem do usuário nas categorias de políticas disponíveis.

            Uma mensagem pode pertencer a mais de uma categoria.

            Retorne somente as categorias semanticamente relacionadas à mensagem.
            """)
    List<PolicyCategory> classify(@UserMessage String message);
}