package io.github.froideexplica.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.github.froideexplica.dto.inputdata.NewProductDTO;
import io.github.froideexplica.dto.inputdata.ProductMassiveReplenishmentDTO;
import io.github.froideexplica.dto.inputdata.PurchasedItemDTO;
import io.github.froideexplica.dto.inputdata.UndoPurchaseDTO;
import io.github.froideexplica.dto.outputdata.MassiveReplenishmentResponseDTO;
import io.github.froideexplica.dto.outputdata.ProductDTO;
import io.github.froideexplica.dto.outputdata.UpdatedStockResponseDTO;
import io.github.froideexplica.dto.outputdata.toll.ProductCreatedSuccessDTO;
import io.github.froideexplica.dto.outputdata.toll.ProductOperationErrorDTO;
import io.github.froideexplica.dto.outputdata.toll.ProductOperationResult;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductTools {

    @Inject
    ProductService productService;

    @Tool(
            description = """
        Lista todos os produtos cadastrados no estoque.

        Use esta ferramenta quando o usuário solicitar listar, visualizar,
        mostrar ou consultar todos os produtos disponíveis no catálogo
        ou no estoque.

        A ferramenta retorna os dados cadastrados de cada produto, incluindo:
        id, nome, preço, código e quantidade disponível em estoque.

        Esta operação é indicada quando o usuário deseja obter uma visão geral
        dos produtos cadastrados, sem informar um código específico.
        """
    )
    public List<ProductDTO> listAllProducts() {
        return productService.listAllProducts();
    }
    @Tool(description = "Localiza um produto pelo ID e retorna seus dados (nome, preço, código e quantidade). Use quando pedirem para buscar/consultar um produto e o identificador informado for o ID numérico. Somente leitura.")
    public ProductDTO findProductById(
            @ToolArg(description = "ID do produto cadastrado no banco de dados.")
            Long id
     ) {
        return productService.findProductById(id);
    }

    @Tool(
            description = """
        Localiza um produto cadastrado a partir do seu código único.

        Use esta ferramenta quando o usuário solicitar localizar, buscar,
        consultar ou verificar um produto específico pelo código.

        A ferramenta retorna os dados cadastrados do produto, incluindo:
        nome, preço, código e quantidade disponível em estoque.

        Esta é a ferramenta indicada para obter informações reais do produto,
        como nome, preço e saldo disponível, inclusive durante a preparação
        de uma compra.

        Para executar esta operação, é necessário somente o código do produto.

        Esta é uma operação somente de leitura e não altera o estoque
        nem qualquer outro dado do produto.

        Antes de executar a ferramenta, o código deve ser convertido para
        o formato definido na descrição do argumento.

        Não invente, complete ou deduza um código de produto ausente.
        """
    )
    public ProductDTO findProductByCode(

            @ToolArg(
                    description = """
                Código único do produto que será consultado.

                O código deve ser enviado preservando exatamente o valor
                utilizado para identificar o produto no sistema.

                Remova apenas espaços externos ou formatações provenientes
                da linguagem natural que não façam parte do código.

                Não remova, acrescente ou altere letras, números, zeros à
                esquerda, hífens ou outros caracteres que façam parte do
                código original.

                Se o código for informado por extenso e representar de forma
                inequívoca uma sequência numérica, converta os números para
                algarismos preservando sua ordem e zeros à esquerda.

                Exemplos:

                "00125"
                -> "00125"

                "zero zero um dois cinco"
                -> "00125"

                Não invente, complete ou deduza partes ausentes do código.

                A existência e a validade do código são verificadas pelo backend.
                """
            )
            String code
    ) {
        return productService.findProductByCode(code);
    }

    @Tool(description = "Cadastra um novo produto no estoque. Use quando pedirem para cadastrar, criar ou registrar um produto. Campos obrigatórios: nome, preço, código e quantidade inicial; a URL do produto é opcional. Operação de escrita.")
    public ProductOperationResult saveNewProduct(
            @ToolArg(description = "Nome do produto que será cadastrado.")
            String name,

            @ToolArg(description = "Preço unitário do produto em texto. Aceita formatos como 4, 4.50, 4,50, R$ 4,50 ou 4 reais.")
            String price,

            @ToolArg(description = "Código único do produto.")
            String code,

            @ToolArg(description = "Quantidade inicial do produto em estoque. Deve ser maior que zero.")
            Integer quantity,

            @ToolArg(description = "URL da imagem ou página do produto. Campo opcional.")
            String productUrl
    ) {
        try {
            NewProductDTO productDTO = new NewProductDTO(
                    name,
                    parsePrice(price),
                    code,
                    quantity,
                    productUrl
            );

            ProductDTO createdProduct = productService.saveNewProduct(productDTO);

            return new ProductCreatedSuccessDTO(createdProduct);

        } catch (IllegalArgumentException e) {
            return new ProductOperationErrorDTO(
                    "Não foi possível cadastrar o produto. Dados inválidos: " + e.getMessage()
            );

        } catch (Exception e) {
            return new ProductOperationErrorDTO(
                    "Não foi possível cadastrar o produto por um erro interno."
            );
        }
    }

    @Tool(
            description = """
        Remove permanentemente um produto cadastrado no estoque a partir do seu ID.

        Use esta ferramenta quando o usuário solicitar excluir, remover
        ou apagar um produto específico do cadastro de produtos.

        Para executar esta operação, é necessário informar o ID do produto
        que será removido.

        Esta é uma operação de escrita e altera permanentemente os dados do sistema.

        Execute esta ferramenta somente quando houver intenção explícita
        de remover o produto cadastrado.

        Não invente, complete ou deduza um ID ausente.
        """
    )
    public String deleteProductById(

            @ToolArg(
                    description = """
                ID único do produto que será removido.

                O valor deve ser enviado como um número inteiro.

                Se o ID for informado em linguagem natural ou por extenso,
                converta-o para algarismos quando a interpretação for inequívoca.

                Exemplos:

                "15"
                -> 15

                "quinze"
                -> 15

                Não invente, complete ou deduza um ID ausente.

                Não valide se o ID corresponde a um produto existente.
                A existência e a validade do produto são responsabilidade
                do backend.
                """
            )
            Long id
    ) {
        productService.deleProductById(id);

        return "Produto removido com sucesso.";
    }

    @Tool(description = "Atualiza os dados de um produto existente pelo ID. A quantidade informada aqui DEFINE (substitui) o valor absoluto do estoque — NÃO soma nem subtrai. Para somar use a reposição; para baixar use a baixa por compra. Campos nulos ou vazios são ignorados (mantêm o valor atual). Operação de escrita.")
    public String updateProduct(
            @ToolArg(description = "ID do produto que será atualizado.")
            Long id,

            @ToolArg(description = "Novo nome do produto. Envie null ou vazio para manter o nome atual.")
            String name,

            @ToolArg(description = "Novo preço do produto em texto. Aceita formatos como 4, 4.50, 4,50, R$ 4,50 ou 4 reais. Envie null ou vazio para manter o preço atual.")
            String price,

            @ToolArg(description = "Código do produto. Normalmente não precisa ser alterado; envie null se quiser manter o atual.")
            String code,

            @ToolArg(description = "Novo valor ABSOLUTO da quantidade em estoque (substitui o atual, não soma nem subtrai). Envie null para manter a quantidade atual.")
            Integer quantity,

            @ToolArg(description = "Nova URL da imagem ou página do produto. Envie null ou vazio para manter a URL atual.")
            String productUrl
    ) {
        ProductDTO productDTO = new ProductDTO(
                id,
                name,
                parseOptionalPrice(price),
                code,
                quantity,
                productUrl
        );

        productService.updateProduct(productDTO, id);

        return "Produto atualizado com sucesso.";
    }

    @Tool(
            description = """
        Atualiza o estoque após uma compra, subtraindo das quantidades disponíveis
        as quantidades efetivamente compradas de cada produto.

        Use esta ferramenta quando for necessário dar baixa no estoque após o
        registro ou efetivação, realização de uma compra.

        Os produtos são identificados pelos seus respectivos códigos.

        Os argumentos productCodes e quantities representam os mesmos produtos e
        devem possuir a mesma quantidade de elementos, mantendo correspondência
        por posição.

        Exemplo:

        productCodes[0] = "1001"
        quantities[0]   = 2

        Os dois valores acima representam o mesmo produto.

        Antes de executar a ferramenta, todos os argumentos devem ser convertidos
        para os formatos definidos em suas respectivas descrições.

        Não invente, complete ou deduza códigos ou quantidades ausentes.
        """
    )
    public List<UpdatedStockResponseDTO> updateQuantityProductsAfterPurchase(

            @ToolArg(
                    description = """
                Lista contendo os códigos dos produtos comprados que terão o
                estoque atualizado.

                Cada posição desta lista corresponde à mesma posição na lista
                quantities.

                Exemplo:

                productCodes = ["1001", "2002"]

                Nesse caso:
                índice 0 representa o primeiro produto;
                índice 1 representa o segundo produto.

                Preserve o código do produto no formato definido pelo sistema.

                Não invente, altere ou deduza códigos de produtos ausentes.
                Se o código necessário não estiver disponível, obtenha-o utilizando
                a ferramenta apropriada antes de executar esta operação.
                """
            )
            List<String> productCodes,

            @ToolArg(
                    description = """
                Lista contendo as quantidades compradas que devem ser subtraídas
                do estoque de cada produto.

                Cada posição desta lista corresponde à mesma posição na lista
                productCodes.

                A quantidade deve ser enviada como número inteiro.

                Exemplos:

                "um"
                -> 1

                "2"
                -> 2

                "três"
                -> 3

                Não valide disponibilidade de estoque antes da chamada.
                A validação da operação e das regras de estoque é responsabilidade
                do backend.

                Não invente ou deduza quantidades ausentes.
                """
            )
            List<Integer> quantities
    ) {
        List<PurchasedItemDTO> purchasedItems =
                buildPurchasedItems(productCodes, quantities);

        return productService.updateQuantityProductsAfterPurchase(purchasedItems);
    }

    @Tool(description = "Estorna/desfaz uma compra individual: SOMA de volta ao estoque a quantidade informada para o produto (identificado por código). Use para devolução ou cancelamento de item comprado. Operação de escrita.")
    public String undoIndividualPurchase(
            @ToolArg(description = "Código único do produto que terá a quantidade devolvida ao estoque.")
            String code,

            @ToolArg(description = "Quantidade que deve ser devolvida ao estoque.")
            Integer quantity
    ) {
        UndoPurchaseDTO undoPurchaseDTO = new UndoPurchaseDTO(quantity);

        productService.undoIndividualPurchase(undoPurchaseDTO, code);

        return "Compra individual desfeita e estoque atualizado com sucesso.";
    }

    @Tool(description = "Reposição massiva de estoque: SOMA (adiciona) as quantidades informadas ao estoque de vários produtos existentes de uma vez, identificados por código — NÃO substitui o valor atual. Use para repor/abastecer/entrada de mercadoria. Operação de escrita.")
    public List<MassiveReplenishmentResponseDTO> massiveReplenishment(
            @ToolArg(description = "Lista de nomes dos produtos que terão reposição de estoque.")
            List<String> names,

            @ToolArg(description = "Lista de preços dos produtos em texto. Aceita valores como 4, 4.50, 4,50, R$ 4,50 ou 4 reais. A posição deve corresponder à posição do produto nas outras listas.")
            List<String> prices,

            @ToolArg(description = "Lista de códigos dos produtos que terão reposição de estoque.")
            List<String> codes,

            @ToolArg(description = "Lista de quantidades que serão adicionadas ao estoque. A posição deve corresponder à posição do código na lista codes.")
            List<Integer> quantities
    ) {
        List<ProductMassiveReplenishmentDTO> products =
                buildMassiveReplenishmentProducts(names, prices, codes, quantities);

        return productService.massiveReplenishment(products);
    }

    private List<PurchasedItemDTO> buildPurchasedItems(
            List<String> productCodes,
            List<Integer> quantities
    ) {
        if (productCodes == null || quantities == null || productCodes.size() != quantities.size()) {
            throw new IllegalArgumentException("As listas productCodes e quantities devem ter o mesmo tamanho.");
        }

        List<PurchasedItemDTO> purchasedItems = new ArrayList<>();

        for (int i = 0; i < productCodes.size(); i++) {
            purchasedItems.add(new PurchasedItemDTO(
                    productCodes.get(i),
                    quantities.get(i)
            ));
        }

        return purchasedItems;
    }

    private List<ProductMassiveReplenishmentDTO> buildMassiveReplenishmentProducts(
            List<String> names,
            List<String> prices,
            List<String> codes,
            List<Integer> quantities
    ) {
        if (names == null || prices == null || codes == null || quantities == null) {
            throw new IllegalArgumentException("As listas names, prices, codes e quantities são obrigatórias.");
        }

        if (names.size() != prices.size()
                || names.size() != codes.size()
                || names.size() != quantities.size()) {
            throw new IllegalArgumentException("As listas names, prices, codes e quantities devem ter o mesmo tamanho.");
        }

        List<ProductMassiveReplenishmentDTO> products = new ArrayList<>();

        for (int i = 0; i < codes.size(); i++) {
            products.add(new ProductMassiveReplenishmentDTO(
                    names.get(i),
                    parsePrice(prices.get(i)),
                    codes.get(i),
                    quantities.get(i)
            ));
        }

        return products;
    }

    private BigDecimal parseOptionalPrice(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }

        return parsePrice(value);
    }

    private BigDecimal parsePrice(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("O preço é obrigatório.");
        }

        String normalized = value
                .trim()
                .toLowerCase()
                .replace("r$", "")
                .replace("reais", "")
                .replace("real", "")
                .replace(" ", "")
                .replace(",", ".");

        normalized = normalized.replaceAll("[^0-9.]", "");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Preço inválido: " + value);
        }

        int firstDot = normalized.indexOf(".");
        if (firstDot != -1) {
            String integerPart = normalized.substring(0, firstDot + 1);
            String decimalPart = normalized.substring(firstDot + 1).replace(".", "");
            normalized = integerPart + decimalPart;
        }

        BigDecimal price = new BigDecimal(normalized);

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }

        return price;
    }
}