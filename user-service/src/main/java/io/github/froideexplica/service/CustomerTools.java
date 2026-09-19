package io.github.froideexplica.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.github.froideexplica.dto.input.PurchaseHistoryInDTO;
import io.github.froideexplica.dto.input.PurchasedProductsDTO;
import io.github.froideexplica.dto.input.RegisterCustomerDTO;
import io.github.froideexplica.dto.output.AddressDTO;
import io.github.froideexplica.dto.output.CustomerDTO;
import io.github.froideexplica.dto.output.PurchaseHistoryOutDTO;
import io.github.froideexplica.model.Address;
import io.github.froideexplica.model.Customer;
import io.github.froideexplica.model.PurchaseRecord;
import io.github.froideexplica.model.Status;

import io.github.froideexplica.validations.exceptions.CustomerRegisterValidationException;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CustomerTools {

    private final CustomerService customerService;

    @Inject
    public CustomerTools(CustomerService customerService) {
        this.customerService = customerService;
    }

    /** Mantém apenas dígitos (remove pontos, traços, espaços, parênteses etc.). */
    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    @Tool(
            description = """
        Localiza um cliente cadastrado a partir do seu documento (CPF ou CNPJ).

        Use esta ferramenta quando o usuário solicitar localizar, buscar, consultar
        ou visualizar os dados de um cliente específico por meio do documento.

        A ferramenta retorna os dados cadastrais encontrados para o cliente,
        podendo incluir informações pessoais, endereço e histórico de compras.

        Esta operação requer somente o documento do cliente.

        Não utilize esta ferramenta para cadastrar, atualizar ou excluir clientes.
        """
    )
    public CustomerDTO findCustomerByDocument(

            @ToolArg(
                    description = """
                Documento CPF ou CNPJ do cliente que será consultado.

                O valor deve ser enviado contendo somente algarismos, sem pontos,
                traços, barras, espaços ou outros caracteres de formatação.

                Se o documento for informado em linguagem natural, parcialmente
                por extenso ou com formatação, normalize o valor antes de chamar
                esta ferramenta.

                Exemplos:

                "007.692.032-13"
                -> "00769203213"

                "zero zero sete seis nove dois zero três dois um três"
                -> "00769203213"

                "007 seis nove dois 032 13"
                -> "00769203213"

                Não valide se o CPF ou CNPJ é matematicamente válido.
                A validação do documento é responsabilidade do backend.
                """
            )
            String document
    ) {
        return customerService.findCustomerByDocument(onlyDigits(document));
    }

    @Tool(
            description = """
        Gera a nota/fatura em PDF referente às compras em aberto de um cliente
        e envia o documento para o e-mail cadastrado.

        Use esta ferramenta quando o usuário solicitar gerar, emitir ou enviar
        a nota, fatura ou recibo referente às compras de um cliente.

        Para executar esta operação, é necessário somente o documento
        CPF ou CNPJ do cliente.

        Antes de executar a ferramenta, o documento deve ser convertido
        para o formato definido na descrição do argumento.

        Não invente, complete ou deduza um documento ausente.
        """
    )
    public String generatePurchaseInvoice(

            @ToolArg(
                    description = """
                Documento CPF ou CNPJ do cliente para o qual a nota/fatura
                será gerada.

                O valor deve ser enviado contendo somente algarismos, sem
                pontos, traços, barras, espaços ou outros caracteres de
                formatação.

                Se o documento for informado em linguagem natural,
                parcialmente por extenso ou com formatação, normalize o valor
                antes de chamar esta ferramenta.

                Exemplos:

                "007.692.032-13"
                -> "00769203213"

                "zero zero sete seis nove dois zero três dois um três"
                -> "00769203213"

                "007 seis nove dois 032 13"
                -> "00769203213"

                Não valide se o CPF ou RG é matematicamente válido.
                A validação do documento é responsabilidade do backend.
                """
            )
            String document
    ) {
        customerService.generatePurchaseInvoice(onlyDigits(document));

        return "Nota detalhada gerada e enviada para o e-mail cadastrado do cliente.";
    }

    @Tool(description = """
    Cadastra um novo cliente.

    Use esta ferramenta quando o usuário solicitar o cadastro, criação
    ou registro de um novo cliente.

    Para executar o cadastro, são obrigatórios:
    nome, documento, e-mail, telefone, nome da rua,
    número da residência/estabelecimento e complemento.

    Antes de executar a ferramenta, todos os argumentos devem ser
    convertidos para os formatos definidos em suas respectivas descrições.

    Não invente, complete ou deduza informações ausentes.
    """)
    public CustomerDTO saveNewCustomer(

            @ToolArg(description = """
        Nome completo do cliente.
        Preserve o nome informado pelo usuário, realizando apenas
        normalizações triviais de espaços.
        Não abrevie, complete ou invente partes do nome.
        """)
            String name,

            @ToolArg(description = """
        Documento brasileiro do cliente: CPF ou RG.

        Para CPF:
        - converta números escritos ou falados por extenso para algarismos
          quando a interpretação for inequívoca;
        - remova pontos, hífens e espaços;
        - envie exatamente 11 dígitos.

        Exemplo:
        "007.692.032-13" -> "00769203213"
        "zero zero sete seis nove dois zero três dois um três"
        -> "00769203213"
        
        "zero zero sete seis nove dois zero três dois um três"
        -> "00769203213"
        
        "007 seis nove dois 032 13"
        -> "00769203213"
        
        "007 seis nove dois 032 um três"
        -> "00769203213"

        Para RG:
        - preserve letras e números;
        - remova apenas caracteres de formatação, como pontos,
          hífens e espaços;
        - não converta RG para CPF nem complete caracteres ausentes.

        Nunca invente caracteres do documento.
        """)
            String document,

            @ToolArg(description = """
        E-mail do cliente em formato eletrônico normalizado.

        Quando vier de linguagem natural ou transcrição de áudio,
        converta expressões inequívocas de endereço eletrônico.

        Exemplos:
        "jeff ponto trabalho arroba outlook ponto com"
        -> "jeff.trabalho@outlook.com"

        "joao arroba gmail ponto com"
        -> "joao@gmail.com"

        Remova espaços indevidos e utilize letras minúsculas
        quando apropriado.

        O endereço deve conter uma parte local, o caractere @
        e um domínio válido.

        Nunca invente usuário, domínio ou caracteres ausentes.
        Se o endereço estiver ambíguo, solicite confirmação.
        """)
            String email,

        @ToolArg(description = """
        Número de telefone celular brasileiro do cliente.

        Envie somente números, sem espaços, parênteses ou hífens.

        Converta números escritos ou falados por extenso para algarismos
        quando a interpretação for inequívoca.

        Exemplo:
        "9 8914-4511" -> "989144511"

        Nunca invente ou complete dígitos ausentes.
        """) String phone,

            @ToolArg(description = """
        Nome da rua, avenida, travessa informado pelo cliente.
        Preserve o conteúdo informado e não invente endereço.
        """)
            String streetName,

            @ToolArg(description = """
        Número da residência ou estabelecimento.
        Preserve exatamente a informação fornecida pelo usuário.
        """)
            String houseNumber,

            @ToolArg(description = """
        Complemento do endereço.

        Exemplos: apartamento, bloco, casa, referência
        ou "sem complemento".

        Se o usuário informar explicitamente que não possui complemento,
        utilize "sem complemento".
        """)
            String complement
    ) {
        RegisterCustomerDTO dto = new RegisterCustomerDTO(
                name,
                onlyDigits(document),
                email,
                onlyDigits(phone),
                new AddressDTO(
                        streetName,
                        houseNumber,
                        complement
                )
        );

        try {
            return customerService.saveNewCustomer(dto);
        } catch (CustomerRegisterValidationException e) {
            throw new ToolCallException(buildDuplicateMessage(e));
        }
    }

    /** Mensagem amigável quando o cliente já existe (documento, telefone ou e-mail duplicado). */
    private String buildDuplicateMessage(CustomerRegisterValidationException e) {
        List<String> campos = new ArrayList<>();

        for (String m : e.getErrorMessages()) {
            String lower = m.toLowerCase();
            if (lower.contains("document") && !campos.contains("documento")) {
                campos.add("documento");
            } else if (lower.contains("phone") && !campos.contains("telefone")) {
                campos.add("telefone");
            } else if (lower.contains("email") && !campos.contains("e-mail")) {
                campos.add("e-mail");
            }
        }

        if (campos.isEmpty()) {
            return "Cliente já cadastrado.";
        }

        return "Cliente já cadastrado: já existe um cliente com este " + String.join(" e este ", campos) + ".";
    }

//    @Tool(description = """
//            Realiza a exclusão lógica de um cliente pelo documento.
//
//            Use esta ferramenta quando o usuário solicitar remoção, exclusão, inativação ou desativação de um cliente.
//
//            A exclusão é lógica: o cliente não é removido fisicamente do banco, apenas marcado como inativo.
//            """)
//    public String logicalCustomerDeletion(
//            @ToolArg(description = "Documento único do cliente que será inativado.")
//            String document
//    ) {
//        customerService.logicalCustomerDeletion(document);
//
//        return "Cliente inativado com sucesso.";
//    }

    @Tool(
            description = """
        Registra uma nova compra no histórico de compras de um cliente existente.

        Use esta ferramenta quando o usuário solicitar registrar, efetivar
        ou salvar uma nova compra realizada por um cliente.

        Esta ferramenta persiste a compra na tabela tb_purchase.

        Registrar a compra nesta ferramenta NÃO altera a quantidade dos produtos
        em estoque. A atualização do estoque é uma operação separada.

        Para executar o registro da compra, são obrigatórios:
        documento do cliente, nome, preço, código e quantidade de cada produto comprado.

        Os argumentos productNames, productPrices, productCodes e quantities
        representam os mesmos produtos e devem estar alinhados pela posição.

        Exemplo:

        índice 0:
        productNames[0]  = "Arroz"
        productPrices[0] = "5.50"
        productCodes[0]  = "1001"
        quantities[0]    = 2

        Todos esses valores representam o mesmo produto.

        As quatro listas devem possuir a mesma quantidade de elementos.

        Antes de executar a ferramenta, todos os argumentos devem ser
        convertidos para os formatos definidos em suas respectivas descrições.

        Não invente, complete ou deduza informações ausentes.
        """
    )
    public PurchaseHistoryOutDTO registerPurchase(

            @ToolArg(
                    description = """
                Documento CPF ou CNPJ do cliente que realizou a compra.

                O valor deve ser enviado contendo somente algarismos, sem
                pontos, traços, barras, espaços ou outros caracteres de formatação.

                Se o documento for informado em linguagem natural, parcialmente
                por extenso ou com formatação, normalize o valor antes de chamar
                esta ferramenta.

                Exemplos:

                "007.692.032-13"
                -> "00769203213"

                "zero zero sete seis nove dois zero três dois um três"
                -> "00769203213"

                "007 seis nove dois 032 13"
                -> "00769203213"

                "12.345.678/0001-90"
                -> "12345678000190"

                Não valide se o CPF ou CNPJ é matematicamente válido.
                A validação do documento é responsabilidade do backend.
                """
            )
            String document,

            @ToolArg(
                    description = """
                Lista contendo os nomes dos produtos comprados.

                Cada posição desta lista corresponde à mesma posição nas listas
                productPrices, productCodes e quantities.

                Exemplo:

                productNames = ["Arroz", "Feijão"]

                Nesse caso:
                índice 0 representa o primeiro produto;
                índice 1 representa o segundo produto.

                Não invente ou deduza o nome de um produto caso ele não tenha
                sido informado ou obtido por meio de uma ferramenta apropriada.
                """
            )
            List<String> productNames,

            @ToolArg(
                    description = """
                Lista contendo os preços unitários dos produtos comprados.

                Cada posição desta lista corresponde à mesma posição nas listas
                productNames, productCodes e quantities.

                O preço deve ser enviado no formato numérico decimal utilizando
                ponto como separador decimal e sem símbolo de moeda.

                Normalize automaticamente valores informados em linguagem natural
                ou com formatação monetária.

                Exemplos:

                "4"
                -> "4"

                "4.50"
                -> "4.50"

                "4,50"
                -> "4.50"

                "R$ 4,50"
                -> "4.50"

                "4 reais e 50 centavos"
                -> "4.50"

                Não valide se o preço corresponde ao preço atual do produto.
                Essa validação, quando necessária, é responsabilidade do backend
                ou da ferramenta responsável pelos produtos.
                """
            )
            List<String> productPrices,

            @ToolArg(
                    description = """
                Lista contendo os códigos dos produtos comprados.

                Cada posição desta lista corresponde à mesma posição nas listas
                productNames, productPrices e quantities.

                Preserve o código do produto no formato definido pelo sistema.

                Não invente, altere ou deduza códigos de produtos ausentes.
                Se o código necessário não estiver disponível, obtenha-o utilizando
                a ferramenta apropriada antes de executar esta operação.
                """
            )
            List<String> productCodes,

            @ToolArg(
                    description = """
                Lista contendo a quantidade comprada de cada produto.

                Cada posição desta lista corresponde à mesma posição nas listas
                productNames, productPrices e productCodes.

                A quantidade deve ser enviada como número inteiro.

                Exemplos:

                "um"
                -> 1

                "2"
                -> 2

                "três"
                -> 3

                Não valide regras de estoque ou disponibilidade do produto.
                Essa validação é responsabilidade do serviço responsável pelo estoque.
                """
            )
            List<Integer> quantities
    ){
        validatePurchaseLists(productNames, productPrices, productCodes, quantities);

        List<PurchasedProductsDTO> products = new ArrayList<>();

        for (int i = 0; i < productNames.size(); i++) {
            products.add(new PurchasedProductsDTO(
                    productNames.get(i),
                    parsePrice(productPrices.get(i)),
                    productCodes.get(i),
                    quantities.get(i)
            ));
        }

        PurchaseHistoryInDTO dto = new PurchaseHistoryInDTO(
                onlyDigits(document),
                products
        );

        return customerService.registerPurchase(dto);
    }

    private void validatePurchaseLists(
            List<String> productNames,
            List<String> productPrices,
            List<String> productCodes,
            List<Integer> quantities
    ) {
        if (productNames == null || productPrices == null || productCodes == null || quantities == null) {
            throw new IllegalArgumentException("As listas productNames, productPrices, productCodes e quantities são obrigatórias.");
        }

        if (productNames.size() != productPrices.size()
                || productNames.size() != productCodes.size()
                || productNames.size() != quantities.size()) {
            throw new IllegalArgumentException("As listas productNames, productPrices, productCodes e quantities devem ter o mesmo tamanho.");
        }

        if (productNames.isEmpty()) {
            throw new IllegalArgumentException("A compra deve conter pelo menos um produto.");
        }
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