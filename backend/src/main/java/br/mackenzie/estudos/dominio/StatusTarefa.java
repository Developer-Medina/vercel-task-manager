package br.mackenzie.estudos.dominio;

public enum StatusTarefa {
    PENDENTE("Pendente"),
    CONCLUIDA("Concluída");

    private final String rotulo;

    StatusTarefa(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
