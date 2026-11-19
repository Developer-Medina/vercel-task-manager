# Organizador de Estudos

Sistema web para cadastrar as matérias que o aluno cursa e acompanhar as tarefas de estudo de cada uma.

Projeto da disciplina Prática em Análise e Desenvolvimento de Sistemas (Mackenzie).

## Acesso

- Aplicação: https://vercel-task-manager-frontend.vercel.app
- Vídeo de demonstração (não listado): https://youtu.be/zCQXjAZ6B74
- Plano de testes: [PlanoTestesCenario](PlanoTestesCenario) (13 scripts) e [métricas](PlanoTestesCenario/MetricasGerais.md)
- Autenticação: desnecessária. O sistema não tem login.

## Estrutura

```
backend/             # API REST em Spring Boot (Java 21, Maven)
frontend/            # telas em HTML, CSS e JavaScript (hospedado na Vercel)
PlanoTestesCenario/  # 13 scripts de teste e as métricas
ROTEIRO_TESTE.md     # como acessar, ordem dos testes e roteiro do vídeo
```

## Como rodar

Requisitos: Java 21 e Maven.

```sh
cd backend
mvn spring-boot:run
```

Abre em http://localhost:8080 (o back-end também serve o `frontend/`). O banco é o H2 em memória e começa vazio a cada execução.

Mais detalhes (testes, PostgreSQL, front em outro endereço) em [ROTEIRO_TESTE.md](ROTEIRO_TESTE.md).

## Implantação

- **Front-end:** Vercel, usando a pasta `frontend/` como raiz do projeto.
- **Back-end:** servidor próprio, em Docker (`backend/Dockerfile`).
