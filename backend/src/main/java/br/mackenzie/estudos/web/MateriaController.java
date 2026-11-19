package br.mackenzie.estudos.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.mackenzie.estudos.dominio.Materia;
import br.mackenzie.estudos.servico.MateriaService;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    // Dados que o front envia / recebe
    public record MateriaEntrada(String nome, String descricao) {
    }

    public record MateriaSaida(Long id, String nome, String descricao) {
        static MateriaSaida de(Materia m) {
            return new MateriaSaida(m.getId(), m.getNome(), m.getDescricao());
        }
    }

    private final MateriaService servico;

    public MateriaController(MateriaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<MateriaSaida> listar() {
        return servico.listar().stream().map(MateriaSaida::de).toList();
    }

    @GetMapping("/{id}")
    public MateriaSaida buscar(@PathVariable Long id) {
        return MateriaSaida.de(servico.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MateriaSaida cadastrar(@RequestBody MateriaEntrada entrada) {
        return MateriaSaida.de(servico.cadastrar(entrada.nome(), entrada.descricao()));
    }
}
