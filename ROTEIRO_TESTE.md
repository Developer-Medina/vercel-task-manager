# Organizador de Estudos

Sistema web para o aluno cadastrar as **matérias** que cursa e acompanhar as **tarefas de estudo** de cada uma.

Projeto da disciplina Prática em Análise e Desenvolvimento de Sistemas (Mackenzie).

Este arquivo diz como acessar a aplicação, em que ordem executar os scripts do plano de testes e o que falar no vídeo de demonstração.

## Sobre o sistema

O aluno cadastra as matérias (nome e descrição). Para cada matéria, cria tarefas de estudo com título, descrição e data limite. Toda tarefa nasce como **Pendente** e passa a **Concluída** quando o aluno a conclui. Também dá para excluir uma tarefa, sempre com pedido de confirmação.

Casos de uso implementados:

| Código | Caso de uso | O que faz |
| --- | --- | --- |
| CDU001 | Cadastrar matéria | O nome é obrigatório; a descrição é opcional |
| CDU002 | Consultar matérias | Lista as matérias cadastradas |
| CDU003 | Cadastrar tarefa de estudo | Exige ao menos uma matéria cadastrada e um título; a data limite é opcional, no formato dd/mm/aaaa |
| CDU004 | Consultar tarefas de estudo | Lista as tarefas; ao clicar no título, abre os detalhes |
| CDU005 | Concluir tarefa de estudo | Muda o status para Concluída; uma tarefa já concluída não pode ser concluída de novo |
| CDU006 | Excluir tarefa de estudo | Mostra os dados da tarefa e pede confirmação antes de excluir |

## Acesso

- URL: https://vercel-task-manager-frontend.vercel.app
- Vídeo de demonstração (não listado): https://youtu.be/zCQXjAZ6B74
- Autenticação: desnecessária. O sistema não tem login, então não há usuário nem senha. Todos os dados de teste são cadastrados pelo próprio sistema.

O front-end fica na Vercel e o back-end roda em servidor próprio. A aplicação fica no ar por pelo menos duas semanas a partir da entrega (18/11/2026).

## Estrutura

```
MeuProjeto/
├── backend/             # API REST em Spring Boot (Java 21, Maven)
├── frontend/            # telas em HTML, CSS e JavaScript puro
├── PlanoTestesCenario/  # os 13 scripts de teste e as métricas
└── ROTEIRO_TESTE.md
```

O back-end guarda os dados (matérias e tarefas) e aplica as regras dos casos de uso. O front-end só desenha as telas e chama a API em `/api/materias` e `/api/tarefas`.

Para ficar simples de rodar e de implantar, o próprio back-end também serve os arquivos do `frontend/`: a aplicação inteira abre em um endereço só.

## Como rodar localmente

Requisitos: Java 21 e Maven.

```sh
cd backend
mvn spring-boot:run
```

A aplicação abre em http://localhost:8080. A porta pode ser trocada com a variável de ambiente `PORT`.

O banco é o H2 em memória: ele **começa vazio** e **zera sempre que a aplicação reinicia**. Todos os scripts partem do sistema vazio, então para recomeçar os testes basta reiniciar.

Para usar PostgreSQL no lugar do H2:

```sh
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Front-end em outro endereço

Se o front for hospedado separado do back-end (por exemplo, na Vercel):

1. Em `frontend/js/config.js`, trocar `API` pela URL do back-end (sem barra no final).
2. No back-end, liberar o endereço do front na variável `app.cors.origens` (ou `APP_CORS_ORIGENS`).

### Teste automatizado

```sh
cd backend
mvn test
```

Roda os 13 scripts contra a API, na mesma ordem do plano de testes.

## Ordem de execução dos scripts

Vários scripts usam como preparação o estado em que o anterior terminou, então a ordem abaixo evita repetir cadastros. As linhas "Preparar" não são testes: é só deixar o sistema no estado que o próximo script pede.

| # | Script | Caso de uso | O que fazer | O que deve acontecer |
| --- | --- | --- | --- | --- |
| 1 | TST001 | CDU003 | Tarefas, depois Nova, sem nenhuma matéria cadastrada | Aparece "É preciso cadastrar uma matéria antes de criar uma tarefa de estudo" e não há formulário |
| 2 | TST006 | CDU002 | Abrir Matérias | "Nenhuma matéria encontrada" |
| 3 | TST004 | CDU001 | Matérias, Nova, cadastrar Engenharia de Software | "Matéria cadastrada com sucesso" |
| 4 | TST005 | CDU001 | Matérias, Nova, salvar sem nome | "O nome da matéria deve ser informado", nada é cadastrado |
| | Preparar | | Cadastrar a matéria Banco de Dados | |
| 5 | TST007 | CDU002 | Abrir Matérias | Lista com as duas matérias |
| 6 | TST008 | CDU004 | Abrir Tarefas | "Nenhuma tarefa de estudo encontrada" |
| 7 | TST002 | CDU003 | Tarefas, Nova, cadastrar a tarefa de Engenharia de Software | "Tarefa cadastrada com sucesso", status Pendente |
| 8 | TST003 | CDU003 | Tarefas, Nova, salvar sem título | "O título da tarefa deve ser informado", nada é cadastrado |
| | Preparar | | Cadastrar a tarefa de Banco de Dados (Revisar comandos SQL) | |
| 9 | TST009 | CDU004 | Abrir Tarefas | Lista com as duas tarefas e a coluna Descrição |
| 10 | TST012 | CDU006 | Abrir Revisar comandos SQL, Excluir, Cancelar | Volta ao detalhe e a tarefa continua |
| 11 | TST013 | CDU006 | Abrir Revisar comandos SQL, Excluir, Confirmar | "Tarefa excluída com sucesso" |
| | Preparar | | Cadastrar de novo a tarefa Revisar comandos SQL | |
| 12 | TST010 | CDU005 | Abrir Estudar diagramas de sequência, Concluir | "Tarefa concluída com sucesso", status Concluída |
| 13 | TST011 | CDU005 | Na mesma tarefa, Concluir de novo | "Esta tarefa já foi concluída" |

Os dados de cada cadastro (títulos, descrições e datas) estão nos próprios scripts, em `PlanoTestesCenario/`.

## Roteiro do vídeo (8 minutos)

Gravar com o sistema recém-iniciado e seguir a ordem da tabela acima. Tempos aproximados:

| Tempo | O que mostrar |
| --- | --- |
| 0:00 | Introdução: o que é o sistema e quais casos de uso existem (texto abaixo) |
| 0:45 | Sistema vazio: Tarefas sem matéria (TST001) e Matérias vazia (TST006) |
| 1:30 | Cadastrar matéria (TST004) e tentar sem nome (TST005) |
| 2:30 | Segunda matéria e lista de matérias (TST007) |
| 3:15 | Tarefas vazia (TST008), cadastrar tarefa (TST002) e tentar sem título (TST003) |
| 4:45 | Lista de tarefas com a segunda tarefa (TST009) |
| 5:30 | Excluir tarefa: cancelar (TST012) e confirmar (TST013) |
| 6:30 | Concluir tarefa (TST010) e tentar concluir de novo (TST011) |
| 7:30 | Fechamento: resultado dos testes (13 de 13) e URL da aplicação |

### Introdução para falar (uns 40 segundos)

> Olá, eu sou o Julio Medina e este é o Sistema Organizador de Estudos, da disciplina de Prática em Análise e Desenvolvimento de Sistemas.
>
> A ideia é ajudar o aluno a organizar o que tem para estudar. Ele cadastra as matérias que cursa e, dentro de cada uma, cria as tarefas de estudo, com título, descrição e data limite. Cada tarefa começa como pendente e, quando o aluno termina, ele marca como concluída. Também é possível excluir uma tarefa, e o sistema sempre pede confirmação antes.
>
> São seis casos de uso: cadastrar e consultar matérias, e cadastrar, consultar, concluir e excluir tarefas. O back-end é em Spring Boot e o front-end é em HTML, CSS e JavaScript. Como o sistema não tem login, não há usuário nem senha.
>
> Vou executar os 13 scripts do plano de testes, começando com o sistema vazio.

### Fechamento (uns 20 segundos)

> Os 13 scripts foram executados e todos tiveram sucesso: 100% de testes implementados, executados e bem-sucedidos. O sistema está no ar neste endereço, que fica disponível por duas semanas. Obrigado.

## Resultado da execução

Os 13 scripts foram executados em 18/11/2026 e todos tiveram sucesso. O TST009 tinha falhado na primeira execução (a lista não mostrava a descrição), foi corrigido e passou na segunda. Os detalhes e as coberturas estão em `PlanoTestesCenario/MetricasGerais.md`.

## Tecnologias

- Back-end: Java 21, Spring Boot 3, Spring Data JPA
- Banco: H2 em memória (PostgreSQL opcional, pelo perfil `postgres`)
- Front-end: HTML, CSS e JavaScript, sem framework
- Testes: JUnit e MockMvc
