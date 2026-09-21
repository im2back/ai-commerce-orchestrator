# Commerce AI Orchestrator

Projeto de orquestração de agentes de IA para operações de comércio, utilizando
Quarkus, LangChain4j, MCP e uma stack de observabilidade baseada em LGTM.

<h2>🎥 Assista ao Overview do Projeto</h2>

<p>
Veja uma demonstração completa da arquitetura, funcionamento dos agentes,
integração via MCP e observabilidade do projeto.
</p>

<a href="https://youtu.be/GR-93V80HPg">
  <img
    width="1672"
    alt="Assista ao Overview do Commerce AI Orchestrator"
    src="https://github.com/user-attachments/assets/134ba86f-fcb4-497b-a053-83198f1dbbbe"
  />
</a>

<p style="font-size: 40px;">
  ▶️ <strong>Clique na imagem acima para assistir ao vídeo no YouTube.</strong>
</p>

---

---
## Pré-requisitos

Para executar o projeto é necessário ter instalado:

- Java 17
- Docker
- Maven / Maven Wrapper
- Git
- Uma chave da OpenAI

---

## 1. Configurar a chave da OpenAI

A chave da OpenAI **não está versionada no projeto**.

O Orchestrator utiliza a propriedade:

```properties
quarkus.langchain4j.openai.api-key
```

Para executar através de Docker, configure inicialmente sua chave em uma variável de ambiente no PowerShell:

```powershell
$env:OPENAI_API_KEY="SUA_CHAVE_OPENAI"
```

Ao iniciar o container do Orchestrator, essa variável será mapeada para a propriedade esperada pelo Quarkus:

```powershell
-e "QUARKUS_LANGCHAIN4J_OPENAI_API_KEY=$env:OPENAI_API_KEY"
```

Dessa forma, a chave não precisa ser armazenada no repositório.

---

## 2. Criar a rede Docker

Os containers da aplicação precisam compartilhar uma mesma rede para que possam se comunicar utilizando seus nomes como hostnames.

Crie a rede:

```powershell
docker network create ai-network
```

Os principais serviços conectados nessa rede serão:

```text
orchestrator-ai
user-service
inventory-service
mysql
lgtm
```

O Orchestrator utiliza diretamente os seguintes endereços internos:

```text
user-service:8081
inventory-service:8082
mysql:3306
lgtm:4318
```

---

## 3. Configurar o MySQL

O projeto utiliza MySQL para persistência.

Existem dois databases utilizados atualmente:

```text
mercearia2
estoque
```

### Orchestrator

O Orchestrator utiliza:

```text
Host: mysql
Porta: 3306
Database: mercearia2
```

Esse banco é utilizado para persistir a memória das conversas.

A tabela utilizada é:

```text
chat_memory
```

Com os principais campos:

```text
memory_id
messages
updated_at
```

O Hibernate está configurado com:

```properties
schema-management.strategy=update
```

Portanto, a tabela pode ser criada/atualizada automaticamente pelo Hibernate, mas o database `mercearia2` precisa existir.

### User Service

O User Service também utiliza:

```text
Database: mercearia2
Porta: 3306
```

Na configuração atual do projeto, o acesso é realizado através de:

```text
host.docker.internal:3306
```

### Inventory Service

O Inventory Service utiliza:

```text
Database: estoque
Porta: 3306
```

Na configuração atual:

```text
host.docker.internal:3306
```

Portanto, antes de iniciar todos os serviços, certifique-se de que os databases abaixo existem:

```sql
CREATE DATABASE mercearia2;
CREATE DATABASE estoque;
```

---

## 4. Subir o User Service

Entre no diretório:

```powershell
cd user-service
```

Compile a aplicação:

```powershell
.\mvnw.cmd package
```

Construa a imagem Docker:

```powershell
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/user-service-jvm .
```

Suba o container:

```powershell
docker run -d `
  --name user-service `
  --network ai-network `
  -p 8081:8081 `
  --env "QUARKUS_HTTP_HOST_VALIDATION_ALLOWED_HOSTS=localhost,127.0.0.1,host.docker.internal,user-service" `
  quarkus/user-service-jvm
```

O serviço ficará disponível em:

```text
http://localhost:8081
```

O Orchestrator acessa o servidor MCP através de:

```text
http://user-service:8081/mcp
```

O User Service disponibiliza as ferramentas relacionadas ao domínio de clientes.

---

## 5. Subir o Inventory Service

Entre no diretório:

```powershell
cd inventory-service
```

Compile:

```powershell
.\mvnw.cmd package
```

Construa a imagem:

```powershell
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/inventory-service-jvm .
```

Suba o container:

```powershell
docker run -d `
  --name inventory-service `
  --network ai-network `
  -p 8082:8082 `
  quarkus/inventory-service-jvm
```

O serviço ficará disponível em:

```text
http://localhost:8082
```

O Orchestrator acessa o servidor MCP através de:

```text
http://inventory-service:8082/mcp
```

O Inventory Service disponibiliza as ferramentas relacionadas ao domínio de estoque.

---

## 6. Subir a stack de observabilidade

A aplicação utiliza a stack LGTM para centralizar a observabilidade.

Crie um arquivo:

```text
docker-compose.yml
```

Com o seguinte conteúdo:

```yaml
services:
  lgtm:
    image: grafana/otel-lgtm:0.24.0
    container_name: lgtm
    environment:
      GF_SECURITY_ADMIN_USER: admin
      GF_SECURITY_ADMIN_PASSWORD: admin
    command: /otel-lgtm/run-all.sh
    ports:
      - "3000:3000"
      - "3200:3200"
      - "4317:4317"
      - "4318:4318"
      - "4040:4040"
    networks:
      - ai-network

networks:
  ai-network:
    external: true
```

Execute:

```powershell
docker compose up -d
```

O Grafana ficará disponível em:

```text
http://localhost:3000
```

Credenciais:

```text
Usuário: admin
Senha: admin
```

O Orchestrator envia traces e logs utilizando OTLP HTTP/Protobuf para:

```text
http://lgtm:4318
```

As métricas OTLP são direcionadas para:

```text
http://lgtm:4318/v1/metrics
```

A aplicação também possui:

```text
OpenTelemetry
Micrometer
Prometheus
Logs estruturados em JSON
Pyroscope
```

> Observação: atualmente o Pyroscope está configurado no código para `http://localhost:4040`. Quando o Orchestrator é executado em container separado do LGTM, `localhost` aponta para o próprio container do Orchestrator. Essa configuração precisa ser ajustada para utilizar o LGTM corretamente entre containers.

---

## 7. Subir o Commerce AI Orchestrator

Entre no diretório:

```powershell
cd commerce-ai-orchestrator
```

Compile:

```powershell
.\mvnw.cmd package
```

Construa a imagem:

```powershell
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/commerce-ai-orchestrator-jvm .
```

Suba o container:

```powershell
docker run -d `
  --name orchestrator-ai `
  --network ai-network `
  -p 8080:8080 `
  --env "QUARKUS_LANGCHAIN4J_OPENAI_API_KEY=$env:OPENAI_API_KEY" `
  quarkus/commerce-ai-orchestrator-jvm
```

O Orchestrator ficará disponível em:

```text
http://localhost:8080
```

O Orchestrator utiliza os seguintes servidores MCP:

```text
CustomerAgent
    ↓
http://user-service:8081/mcp

InventoryAgent
    ↓
http://inventory-service:8082/mcp

PurchaseAgent
    ↓
user-service + inventory-service
```

O fluxo principal da aplicação é:

```text
Usuário
   ↓
OrchestratorAgent
   ↓
Tool de Orquestração
   ↓
Agente Especialista
   ↓
MCP
   ↓
Serviço responsável
```

---

## 8. Containers esperados

Depois de iniciar o ambiente, execute:

```powershell
docker ps
```

Os principais containers esperados são:

```text
orchestrator-ai      → 8080
user-service         → 8081
inventory-service    → 8082
mysql                → 3306
lgtm                 → Observabilidade
```

A rede pode ser verificada através de:

```powershell
docker network inspect ai-network
```

O Orchestrator depende da resolução dos seguintes nomes dentro da rede:

```text
user-service
inventory-service
mysql
lgtm
```

---

## 9. Testando sem WhatsApp

A arquitetura completa permite integração com WhatsApp utilizando:

```text
WhatsApp
   ↓
Evolution API
   ↓
n8n
   ↓
Commerce AI Orchestrator
```

O **Evolution API e o n8n não fazem parte deste repositório**.

Eles são utilizados como camada externa de integração para receber eventos do WhatsApp, tratar mensagens e permitir fluxos adicionais, como processamento e transcrição de áudio.

Para testar o projeto, essa integração **não é necessária**.

As mensagens de texto podem ser enviadas diretamente para o Orchestrator utilizando Postman.

### Endpoint

```text
POST http://localhost:8080/assistant
```

### Headers

```text
Content-Type: text/plain
Accept: text/plain
X-User-Name: usuario-teste
debug: teste-001
```

O header `X-User-Name` identifica o usuário utilizado na conversa e na memória.

O header `debug` pode ser utilizado como chave de deduplicação da requisição.

### Body

Selecione:

```text
Body → raw → Text
```

E envie uma mensagem em linguagem natural:

```text
Liste todos os produtos disponíveis no estoque.
```

O fluxo será:

```text
Postman
   ↓
OrchestratorAgent
   ↓
Tool de Orquestração
   ↓
Agente Especialista
   ↓
MCP
   ↓
User Service / Inventory Service
   ↓
Resposta
```

Também é possível realizar o teste através do PowerShell:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/assistant" `
  -ContentType "text/plain" `
  -Headers @{
    "Accept" = "text/plain"
    "X-User-Name" = "usuario-teste"
    "debug" = "teste-001"
  } `
  -Body "Liste todos os produtos disponíveis no estoque."
```

Para reproduzir a experiência completa demonstrada no projeto através de WhatsApp e mensagens de áudio, é necessário realizar separadamente a integração com **Evolution API + n8n**.
