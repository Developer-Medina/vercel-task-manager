# Métricas gerais do plano de testes

**Projeto:** Sistema Organizador de Estudos

**Execução dos testes:** 18/11/2026, pela interface web (http://localhost:8080) da versão final, com back-end Spring Boot (`backend/`) e front-end em HTML, CSS e JavaScript (`frontend/`). Banco H2 em memória, aplicação iniciada vazia com `mvn spring-boot:run`. Os 13 scripts foram refeitos nessa versão, na ordem descrita em `ROTEIRO_TESTE.md`.

## 1. Contagem por caso de uso

Cada fluxo (principal, alternativo ou de exceção) do caso de uso gera um cenário, e cada cenário tem um script de teste.

| Caso de uso | Cenários (requisitos para teste) | Testes implementados | Testes executados | Testes bem-sucedidos |
| --- | :---: | :---: | :---: | :---: |
| CDU001 Cadastrar matéria | 2 | 2 | 2 | 2 |
| CDU002 Consultar matérias | 2 | 2 | 2 | 2 |
| CDU003 Cadastrar tarefa de estudo | 3 | 3 | 3 | 3 |
| CDU004 Consultar tarefas de estudo | 2 | 2 | 2 | 2 |
| CDU005 Concluir tarefa de estudo | 2 | 2 | 2 | 2 |
| CDU006 Excluir tarefa de estudo | 2 | 2 | 2 | 2 |
| **Total** | **13** | **13** | **13** | **13** |

## 2. Cenários e resultados

| Caso de uso | Cenário | Script | Resultado |
| --- | --- | --- | --- |
| CDU001 | Fluxo principal | TST004 | Sucesso |
| CDU001 | Exceção do passo 4 (nome não informado) | TST005 | Sucesso |
| CDU002 | Fluxo principal | TST007 | Sucesso |
| CDU002 | Alternativo do passo 3 (nenhuma matéria cadastrada) | TST006 | Sucesso |
| CDU003 | Fluxo principal | TST002 | Sucesso |
| CDU003 | Exceção do passo 2 (nenhuma matéria cadastrada) | TST001 | Sucesso |
| CDU003 | Exceção do passo 6 (título não informado) | TST003 | Sucesso |
| CDU004 | Fluxo principal | TST009 | Sucesso (1ª execução: Falha; corrigido e re-executado) |
| CDU004 | Alternativo do passo 3 (nenhuma tarefa cadastrada) | TST008 | Sucesso |
| CDU005 | Fluxo principal | TST010 | Sucesso |
| CDU005 | Alternativo do passo 3 (tarefa já concluída) | TST011 | Sucesso |
| CDU006 | Fluxo principal | TST013 | Sucesso |
| CDU006 | Alternativo do passo 5 (cancelamento da exclusão) | TST012 | Sucesso |

## 3. Cálculo das coberturas

Todas as coberturas são divididas pela quantidade de requisitos para teste (13 cenários).

| Cobertura | Fórmula | Resultado |
| --- | --- | --- |
| Testes implementados | 13 / 13 | **100%** |
| Testes executados | 13 / 13 | **100%** |
| Testes bem-sucedidos | 13 / 13 | **100%** |

## 4. Falha encontrada e corrigida

**TST009 (CDU004, fluxo principal):** na primeira execução, o resultado esperado era uma lista de tarefas com as colunas Matéria, Título, Descrição, Data limite e Status, mas a aplicação mostrava a lista sem a coluna **Descrição** (`templates/tarefas/lista.html`); a descrição só aparecia na tela de detalhe. Resultado da 1ª execução: **Falha** (12/13 = 92,31% de testes bem-sucedidos).

A coluna Descrição foi adicionada à lista e o TST009 foi executado de novo, com sucesso. Depois disso o sistema foi reescrito com back-end e front-end separados (mesmos casos de uso, telas e mensagens), e os 13 scripts foram executados outra vez do início, todos com sucesso. As métricas acima refletem essa execução final.

## 5. Observações

- A ordem de execução seguiu a conveniência de preparação (vários scripts partem do estado final de outro). A preparação intermediária (cadastrar a matéria "Banco de Dados" e a tarefa "Revisar comandos SQL") foi feita pela própria interface: a matéria antes de TST007 e a tarefa antes de TST009 (ela foi excluída em TST013 e cadastrada de novo antes de TST010).
- O teste automatizado `PlanoTestesCenarioTest` (em `backend/src/test`) roda os 13 scripts contra a API, na mesma ordem e com a numeração dos scripts (13 testes, 0 falhas). Ele não substitui a execução pela interface: o cancelamento da exclusão (TST012) acontece só na tela, então no teste automatizado ele apenas confere que a tarefa continua cadastrada.
