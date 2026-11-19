package br.mackenzie.estudos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

// Roda os 13 scripts do plano de testes contra a API, na mesma ordem em que são executados na tela.
// O banco é o mesmo do começo ao fim, então a ordem importa (cada teste parte do estado deixado pelo anterior)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlanoTestesCenarioTest {

    @Autowired
    private MockMvc mvc;

    private static Object idEngenharia;
    private static Object idBancoDeDados;
    private static Object idDiagramas;
    private static Object idSql;

    /* ------------ CDU003 / CDU002: sem nada cadastrado ------------ */

    @Test
    @Order(1)
    void tst001_tarefaSemMateriasCadastradas() throws Exception {
        enviarTarefa(1, "Estudar diagramas de sequência", "", "30/11/2026")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("É preciso cadastrar uma matéria antes de criar uma tarefa de estudo"));
        mvc.perform(get("/api/tarefas")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(2)
    void tst006_consultaMateriasVazia() throws Exception {
        mvc.perform(get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /* ------------ CDU001: cadastrar matéria ------------ */

    @Test
    @Order(3)
    void tst004_cadastroDeMateria() throws Exception {
        String resposta = enviarMateria("Engenharia de Software", "Processos, requisitos e UML")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Engenharia de Software"))
                .andReturn().getResponse().getContentAsString();
        idEngenharia = JsonPath.read(resposta, "$.id");
    }

    @Test
    @Order(4)
    void tst005_materiaSemNome() throws Exception {
        enviarMateria("", "Descrição sem nome")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("O nome da matéria deve ser informado"));
        mvc.perform(get("/api/materias")).andExpect(jsonPath("$.length()").value(1));
    }

    /* ------------ CDU002 / CDU004: consultas ------------ */

    @Test
    @Order(5)
    void tst007_consultaMateriasCadastradas() throws Exception {
        // Preparação do script: segunda matéria
        String resposta = enviarMateria("Banco de Dados", "Modelagem relacional e SQL")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        idBancoDeDados = JsonPath.read(resposta, "$.id");

        mvc.perform(get("/api/materias"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Engenharia de Software"))
                .andExpect(jsonPath("$[0].descricao").value("Processos, requisitos e UML"))
                .andExpect(jsonPath("$[1].nome").value("Banco de Dados"))
                .andExpect(jsonPath("$[1].descricao").value("Modelagem relacional e SQL"));
    }

    @Test
    @Order(6)
    void tst008_consultaTarefasVazia() throws Exception {
        mvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /* ------------ CDU003: cadastrar tarefa ------------ */

    @Test
    @Order(7)
    void tst002_cadastroDeTarefa() throws Exception {
        String resposta = enviarTarefa(idEngenharia, "Estudar diagramas de sequência",
                "Ler a Aula 2 e refazer os diagramas", "30/11/2026")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Pendente"))
                .andExpect(jsonPath("$.materiaNome").value("Engenharia de Software"))
                .andExpect(jsonPath("$.dataLimite").value("30/11/2026"))
                .andReturn().getResponse().getContentAsString();
        idDiagramas = JsonPath.read(resposta, "$.id");
    }

    @Test
    @Order(8)
    void tst003_tarefaSemTitulo() throws Exception {
        enviarTarefa(idBancoDeDados, "", "Resolver a lista de exercícios", "05/12/2026")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("O título da tarefa deve ser informado"));
        mvc.perform(get("/api/tarefas")).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Order(9)
    void tst009_consultaTarefasCadastradas() throws Exception {
        // Preparação do script: segunda tarefa
        String resposta = enviarTarefa(idBancoDeDados, "Revisar comandos SQL",
                "Resolver a lista de exercícios", "05/12/2026")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        idSql = JsonPath.read(resposta, "$.id");

        mvc.perform(get("/api/tarefas"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].materiaNome").value("Engenharia de Software"))
                .andExpect(jsonPath("$[0].titulo").value("Estudar diagramas de sequência"))
                .andExpect(jsonPath("$[0].descricao").value("Ler a Aula 2 e refazer os diagramas"))
                .andExpect(jsonPath("$[0].dataLimite").value("30/11/2026"))
                .andExpect(jsonPath("$[0].status").value("Pendente"))
                .andExpect(jsonPath("$[1].materiaNome").value("Banco de Dados"))
                .andExpect(jsonPath("$[1].titulo").value("Revisar comandos SQL"))
                .andExpect(jsonPath("$[1].descricao").value("Resolver a lista de exercícios"))
                .andExpect(jsonPath("$[1].dataLimite").value("05/12/2026"))
                .andExpect(jsonPath("$[1].status").value("Pendente"));
    }

    /* ------------ CDU006: excluir tarefa ------------ */

    @Test
    @Order(10)
    void tst012_cancelamentoDaExclusao() throws Exception {
        // Cancelar é só na tela: o front não chama a API, então a tarefa continua lá
        mvc.perform(get("/api/tarefas/" + idSql))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Revisar comandos SQL"));
        mvc.perform(get("/api/tarefas")).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(11)
    void tst013_exclusaoDeTarefa() throws Exception {
        mvc.perform(delete("/api/tarefas/" + idSql)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tarefas"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Estudar diagramas de sequência"));

        // Preparação do script seguinte: recria a tarefa excluída
        String resposta = enviarTarefa(idBancoDeDados, "Revisar comandos SQL",
                "Resolver a lista de exercícios", "05/12/2026")
                .andReturn().getResponse().getContentAsString();
        idSql = JsonPath.read(resposta, "$.id");
    }

    /* ------------ CDU005: concluir tarefa ------------ */

    @Test
    @Order(12)
    void tst010_conclusaoDeTarefa() throws Exception {
        mvc.perform(post("/api/tarefas/" + idDiagramas + "/concluir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Concluída"));
        mvc.perform(get("/api/tarefas/" + idSql)).andExpect(jsonPath("$.status").value("Pendente"));
    }

    @Test
    @Order(13)
    void tst011_tarefaJaConcluida() throws Exception {
        mvc.perform(post("/api/tarefas/" + idDiagramas + "/concluir"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Esta tarefa já foi concluída"));
        mvc.perform(get("/api/tarefas/" + idDiagramas)).andExpect(jsonPath("$.status").value("Concluída"));
    }

    /* ------------ Auxiliares ------------ */

    private ResultActions enviarMateria(String nome, String descricao) throws Exception {
        return mvc.perform(post("/api/materias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"nome\":\"%s\",\"descricao\":\"%s\"}", nome, descricao)));
    }

    private ResultActions enviarTarefa(Object materiaId, String titulo,
            String descricao, String data) throws Exception {
        return mvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("{\"materiaId\":%s,\"titulo\":\"%s\",\"descricao\":\"%s\",\"dataLimite\":\"%s\"}",
                        materiaId, titulo, descricao, data)));
    }

    private static String json(String modelo, Object... valores) {
        return modelo.formatted(valores);
    }
}
