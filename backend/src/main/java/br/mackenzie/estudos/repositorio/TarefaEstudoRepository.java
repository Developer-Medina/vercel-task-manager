package br.mackenzie.estudos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.mackenzie.estudos.dominio.TarefaEstudo;

public interface TarefaEstudoRepository extends JpaRepository<TarefaEstudo, Long> {

    List<TarefaEstudo> findAllByOrderByIdAsc();
}
