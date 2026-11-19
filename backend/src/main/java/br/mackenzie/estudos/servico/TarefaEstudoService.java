package br.mackenzie.estudos.servico;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.mackenzie.estudos.dominio.Materia;
import br.mackenzie.estudos.dominio.TarefaEstudo;
import br.mackenzie.estudos.repositorio.TarefaEstudoRepository;

// CDU003 a CDU006 (cadastrar, consultar, concluir e excluir tarefa de estudo)
@Service
public class TarefaEstudoService {

    public static final String MSG_TITULO_OBRIGATORIO = "O título da tarefa deve ser informado";
    public static final String MSG_SEM_MATERIAS = "É preciso cadastrar uma matéria antes de criar uma tarefa de estudo";
    public static final String MSG_MATERIA_OBRIGATORIA = "A matéria da tarefa deve ser informada";
    public static final String MSG_DATA_INVALIDA = "A data limite deve estar no formato dd/mm/aaaa";
    public static final String MSG_JA_CONCLUIDA = "Esta tarefa já foi concluída";

    private final TarefaEstudoRepository repositorio;
    private final MateriaService materiaService;

    public TarefaEstudoService(TarefaEstudoRepository repositorio, MateriaService materiaService) {
        this.repositorio = repositorio;
        this.materiaService = materiaService;
    }

    public TarefaEstudo cadastrar(Long materiaId, String titulo, String descricao, String dataLimite) {
        if (materiaService.listar().isEmpty()) {
            throw new NegocioException(MSG_SEM_MATERIAS);
        }
        if (materiaId == null) {
            throw new NegocioException(MSG_MATERIA_OBRIGATORIA);
        }
        if (titulo == null || titulo.isBlank()) {
            throw new NegocioException(MSG_TITULO_OBRIGATORIO);
        }
        Materia materia = materiaService.buscar(materiaId);
        return repositorio.save(new TarefaEstudo(materia, titulo.trim(),
                descricao == null ? "" : descricao.trim(), converterData(dataLimite)));
    }

    public List<TarefaEstudo> listar() {
        return repositorio.findAllByOrderByIdAsc();
    }

    public TarefaEstudo buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new NaoEncontradoException("Tarefa não encontrada"));
    }

    // @Transactional: a alteração do status é gravada no fim do método, sem chamar o save
    @Transactional
    public TarefaEstudo concluir(Long id) {
        TarefaEstudo tarefa = buscar(id);
        if (tarefa.estaConcluida()) {
            throw new NegocioException(MSG_JA_CONCLUIDA);
        }
        tarefa.concluir();
        return tarefa;
    }

    public void excluir(Long id) {
        repositorio.delete(buscar(id));
    }

    // A data é opcional. Se vier preenchida, tem que estar em dd/mm/aaaa
    private LocalDate converterData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), TarefaEstudo.FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new NegocioException(MSG_DATA_INVALIDA);
        }
    }
}
