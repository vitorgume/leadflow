# Plano de Testes de API Abrangente - LeadFlow

Como **QA Lead Senior e Engenheiro de Backend**, elaborei este plano de testes para validar de ponta a ponta os fluxos de negócio e a robustez do sistema **LeadFlow**. O plano cobre cenários felizes (Happy Path) e de erro (Sad Path), considerando a arquitetura de microsserviços e os requisitos de negócio.

---

### 🟢 Cenário 1: Configuração Inicial (Setup do Usuário)
**Objetivo:** Garantir que um usuário consegue se cadastrar, fazer login e configurar as integrações essenciais.

#### 1.1. Registro de Novo Usuário (Happy Path)
*   **Endpoint e Método:** `POST /api/v1/usuarios`
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "nome": "Admin LeadFlow",
      "email": "admin@empresa.com",
      "senha": "Senha@Forte123",
      "nome_empresa": "Empresa Exemplo SA"
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `201 Created`
    *   **Lógica:** O usuário deve ser criado com sucesso. O corpo da resposta deve conter o UUID do usuário recém-criado. A senha não deve ser exposta.
*   **Dica de Validação no Banco:** `SELECT * FROM usuarios WHERE email = 'admin@empresa.com';`. Verificar se a coluna `senha` está criptografada (ex: BCrypt).

#### 1.2. Login de Usuário (Happy Path)
*   **Endpoint e Método:** `POST /api/v1/login`
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "email": "admin@empresa.com",
      "senha": "Senha@Forte123"
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** Autenticação bem-sucedida. O corpo da resposta deve conter um `accessToken` (JWT) válido.
*   **Dica de Validação no Banco:** N/A (Validação de lógica).

#### 1.3. Login com Credenciais Inválidas (Sad Path)
*   **Endpoint e Método:** `POST /api/v1/login`
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "email": "admin@empresa.com",
      "senha": "senha-incorreta"
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `401 Unauthorized`
    *   **Lógica:** A API deve negar o acesso devido a credenciais inválidas.
*   **Dica de Validação no Banco:** N/A.

#### 1.4. Configurar Credenciais do Kommo CRM (Happy Path)
*   **Endpoint e Método:** `PUT /api/v1/usuarios/{id_usuario}/configuracao-crm` (Requer Token JWT)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "crm": "KOMMO",
      "url_base": "https://empresaexemplo.kommo.com",
      "api_key": "chave-secreta-da-api-kommo-gerada-pelo-usuario",
      "mapeamento_campos": [
        { "campo_leadflow": "nome_cliente", "campo_crm": "contact.name" },
        { "campo_leadflow": "telefone_cliente", "campo_crm": "contact.phone" },
        { "campo_leadflow": "segmento_interesse", "campo_crm": "lead.custom_fields.segment" }
      ]
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** As configurações do CRM devem ser salvas e associadas ao `id_usuario`.
*   **Dica de Validação no Banco:** `SELECT * FROM configuracao_crm WHERE id_usuario = BINARY_TO_UUID('{id_usuario}');`

#### 1.5. Salvar Tokens de Integração (WhatsApp/IA) (Happy Path)
*   **Endpoint e Método:** `PUT /api/v1/usuarios/{id_usuario}/tokens` (Requer Token JWT)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "whatsapp_token": "token-permanente-whatsapp-business",
      "agente_api_key": "chave-api-da-api-agente"
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** Os tokens devem ser atualizados no registro do usuário.
*   **Dica de Validação no Banco:** Verificar as colunas `whatsapp_token` e `agente_api_key` na tabela `usuarios`.

---

### 🔵 Cenário 2: Gestão de Vendas (Regras de Negócio)
**Objetivo:** Validar a criação da equipe de vendas e a correta aplicação das regras de distribuição de leads.

#### 2.1. Criar um Vendedor (Happy Path)
*   **Endpoint e Método:** `POST /api/v1/vendedores` (Requer Token JWT)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "nome": "Ana Lima - Vendas Saúde",
      "email": "ana.lima@empresa.com",
      "id_crm": "54321",
      "status": "ATIVO"
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `201 Created`
    *   **Lógica:** Um novo registro de vendedor é criado. A resposta deve conter o UUID do vendedor.
*   **Dica de Validação no Banco:** `SELECT * FROM vendedores WHERE email = 'ana.lima@empresa.com';`

#### 2.2. Criar Regra de Distribuição (Happy Path)
*   **Endpoint e Método:** `POST /api/v1/regras-distribuicao` (Requer Token JWT)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "descricao": "Regra para leads de Saúde com orçamento alto",
      "id_vendedor_padrao": "UUID_VENDEDOR_PADRAO_CATCH_ALL",
      "condicoes": [
        {
          "campo": "segmento",
          "operador": "IGUAL",
          "valor": "Saúde",
          "conector_logico": "E",
          "id_vendedor": "UUID_VENDEDORA_ANA_LIMA"
        },
        {
          "campo": "orcamento_estimado",
          "operador": "MAIOR_QUE",
          "valor": "1000",
          "conector_logico": null,
          "id_vendedor": "UUID_VENDEDORA_ANA_LIMA"
        }
      ]
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `201 Created`
    *   **Lógica:** A `ConfiguracaoEscolhaVendedor` e suas `Condicoes` associadas devem ser salvas.
*   **Dica de Validação no Banco:** Verificar a tabela `configuracao_escolha_vendedor` e a tabela `condicoes` para confirmar o relacionamento.

#### 2.3. Criar Regra com Operador Inválido (Sad Path)
*   **Endpoint e Método:** `POST /api/v1/regras-distribuicao` (Requer Token JWT)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "descricao": "Regra com erro",
      "id_vendedor_padrao": "UUID_VENDEDOR_PADRAO_CATCH_ALL",
      "condicoes": [
        { "campo": "segmento", "operador": "CONTÉM", "valor": "Saúde", "id_vendedor": "UUID_VENDEDORA_ANA_LIMA" }
      ]
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `400 Bad Request`
    *   **Lógica:** A API deve retornar um erro de validação, informando que o `operador` "CONTÉM" é inválido (assumindo que o enum correto é "CONTEM").
*   **Dica de Validação no Banco:** Garantir que nenhum registro foi inserido nas tabelas de configuração.

---

### 🟣 Cenário 3: Fluxo de Vida do Lead (O Coração do Sistema)
**Objetivo:** Simular a chegada de um lead via webhook, sua qualificação pela IA e a persistência dos dados.

#### 3.1. Receber Webhook do WhatsApp (Happy Path)
*   **Endpoint e Método:** `POST /api/v1/webhook/whatsapp` (API Intermediária)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "entry": [{
        "changes": [{
          "value": {
            "messages": [{
              "from": "5511912345678",
              "text": { "body": "Olá, gostaria de um orçamento para minha empresa." },
              "type": "text"
            }]
          }
        }]
      }]
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `202 Accepted`
    *   **Lógica:** O sistema deve acusar o recebimento e processar a mensagem de forma assíncrona. Um cliente deve ser criado ou encontrado com base no telefone.
*   **Dica de Validação no Banco:** `SELECT * FROM clientes WHERE telefone = '5511912345678';`. Um novo `Contexto` também deve ser criado.

#### 3.2. Qualificar Conversa com IA (Happy Path - API Agente)
*   **Endpoint e Método:** `POST /api/v1/agente/qualificar` (API Agente)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "id_usuario": "UUID_DO_USUARIO_CONFIGURADO",
      "atributos_qualificacao": ["nome", "segmento", "orcamento_estimado"],
      "historico_conversa": "Agente: Olá, como posso ajudar? Cliente: Oi, meu nome é Beatriz. Estou buscando um plano de saúde e meu orçamento é até 1500 reais."
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** A API deve retornar um JSON com os dados extraídos da conversa.
    *   **Corpo da Resposta (Exemplo):** `{"nome": "Beatriz", "segmento": "Saúde", "orcamento_estimado": "1500"}`
*   **Dica de Validação no Banco:** N/A (Este passo valida a lógica da IA).

#### 3.3. Verificar Persistência dos Dados Qualificados (Happy Path)
*   **Contexto:** Este teste verifica a consequência do passo anterior, onde a `API Principal` consome a resposta da `API Agente` e atualiza o cliente.
*   **Endpoint e Método:** `GET /api/v1/clientes/{id_cliente}` (Requer Token JWT)
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** O corpo da resposta deve conter o objeto do cliente com os dados qualificados pela IA devidamente preenchidos (ex: em um campo JSON `dados_qualificados`).
*   **Dica de Validação no Banco:** `SELECT dados_qualificados FROM clientes WHERE id = BINARY_TO_UUID('{id_cliente}');`

---

### 🟠 Cenário 4: Distribuição e Integração (O Gran Finale)
**Objetivo:** Garantir que o lead qualificado é atribuído ao vendedor correto e os dados são enviados para o CRM.

#### 4.1. Disparar Finalização e Roteamento (Happy Path)
*   **Endpoint e Método:** `POST /api/v1/clientes/{id_cliente}/finalizar-qualificacao` (Requer Token JWT)
*   **Exemplo de Payload (JSON):**
    ```json
    {
      "qualificado": true
    }
    ```
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** A API deve: 1. Aplicar as regras de distribuição (do Cenário 2). 2. Atribuir o vendedor correto ao cliente. 3. Disparar a integração com o Kommo CRM.
*   **Dica de Validação no Banco:** `SELECT id_vendedor_atribuido FROM clientes WHERE id = BINARY_TO_UUID('{id_cliente}');`. O valor deve corresponder ao UUID da vendedora "Ana Lima", com base na regra.

#### 4.2. Roteamento para Vendedor Padrão (Sad Path)
*   **Setup:** Qualificar um cliente com dados que não correspondam a nenhuma regra (ex: `segmento: "Tecnologia"`, `orcamento: 500`).
*   **Endpoint e Método:** `POST /api/v1/clientes/{id_cliente_sem_regra}/finalizar-qualificacao`
*   **Resultado Esperado:**
    *   **Status Code:** `200 OK`
    *   **Lógica:** Como nenhuma regra corresponde, o `id_vendedor_atribuido` deve ser o `id_vendedor_padrao` definido na configuração.
*   **Dica de Validação no Banco:** `SELECT id_vendedor_atribuido FROM clientes WHERE id = ...;`. O valor deve ser igual ao `id_vendedor_padrao`.

#### 4.3. Falha na Integração com CRM (Sad Path)
*   **Contexto:** Simular que a API do Kommo está offline ou retorna um erro 500 durante o passo 4.1.
*   **Validação (Não é um teste de API direto):**
    *   **Lógica Esperada:** O sistema não deve travar. A atribuição do vendedor deve ocorrer normalmente, mas a integração com o CRM deve ser marcada como falha, idealmente para uma nova tentativa (retry).
    *   **Dica de Validação no Banco:** `SELECT status_integracao_crm FROM clientes WHERE id = ...;`. O valor deve ser `FALHA` ou `PENDENTE_RETRY`. Verificar também os logs da `API Principal` por mensagens de erro relacionadas à falha de comunicação com o Kommo.
