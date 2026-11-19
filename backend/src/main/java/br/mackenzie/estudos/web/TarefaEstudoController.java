package br.mackenzie.estudos.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.estudos.dominio.TarefaEstudo;
import br.mackenzie.estudos.servico.TarefaEstudoService;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaEstudoController {

    // dataLimite chega como texto (dd/mm/aaaa) e a validação fica no serviço
    public record TarefaEntrada(Long materiaId, String titulo, String descricao, String dataLimite) {
    }

    public record TarefaSaida(Long id, String titulo, String descricao, String dataLimite,
            String status, boolean concluida, Long materiaId, String materiaNome) {
        static TarefaSaida de(TarefaEstudo t) {
            return new TarefaSaida(t.getId(), t.getTitulo(), t.getDescricao(), t.dataLimiteFormatada(),
                    t.getStatus().getRotulo(), t.estaConcluida(),
                    t.getMateria().getId(), t.getMateria().getNome());
        }
    }

    private final TarefaEstudoService servico;

    public TarefaEstudoController(TarefaEstudoService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<TarefaSaida> listar() {
        return servico.listar().stream().map(TarefaSaida::de).toList();
    }

    @GetMapping("/{id}")
    public TarefaSaida buscar(@PathVariable Long id) {
        return TarefaSaida.de(servico.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarefaSaida cadastrar(@RequestBody TarefaEntrada e) {
        return TarefaSaida.de(servico.cadastrar(e.materiaId(), e.titulo(), e.descricao(), e.dataLimite()));
    }

    @PostMapping("/{id}/concluir")
    public TarefaSaida concluir(@PathVariable Long id) {
        return TarefaSaida.de(servico.concluir(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        servico.excluir(id);
    }
}
