package br.mackenzie.estudos.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.mackenzie.estudos.dominio.Materia;
import br.mackenzie.estudos.repositorio.MateriaRepository;

// CDU001 (Cadastrar matéria) e CDU002 (Consultar matérias)
@Service
public class MateriaService {

    public static final String MSG_NOME_OBRIGATORIO = "O nome da matéria deve ser informado";

    private final MateriaRepository repositorio;

    public MateriaService(MateriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Materia cadastrar(String nome, String descricao) {
        if (nome == null || nome.isBlank()) {
            throw new NegocioException(MSG_NOME_OBRIGATORIO);
        }
        return repositorio.save(new Materia(nome.trim(), descricao == null ? "" : descricao.trim()));
    }

    public List<Materia> listar() {
        return repositorio.findAllByOrderByIdAsc();
    }

    public Materia buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new NaoEncontradoException("Matéria não encontrada"));
    }
}
