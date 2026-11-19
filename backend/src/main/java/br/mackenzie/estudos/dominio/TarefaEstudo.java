package br.mackenzie.estudos.dominio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TarefaEstudo {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descricao;

    private LocalDate dataLimite;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Materia materia;

    // Construtor sem argumentos: o JPA precisa dele para montar o objeto
    protected TarefaEstudo() {
    }

    // Toda tarefa nasce Pendente
    public TarefaEstudo(Materia materia, String titulo, String descricao, LocalDate dataLimite) {
        this.materia = materia;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLimite = dataLimite;
        this.status = StatusTarefa.PENDENTE;
    }

    public boolean estaConcluida() {
        return status == StatusTarefa.CONCLUIDA;
    }

    public void concluir() {
        this.status = StatusTarefa.CONCLUIDA;
    }

    public String dataLimiteFormatada() {
        return dataLimite == null ? "" : dataLimite.format(FORMATO_DATA);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public Materia getMateria() {
        return materia;
    }
}
