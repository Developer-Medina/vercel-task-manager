package br.mackenzie.estudos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.mackenzie.estudos.dominio.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    List<Materia> findAllByOrderByIdAsc();
}
