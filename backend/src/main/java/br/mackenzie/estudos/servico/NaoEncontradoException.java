package br.mackenzie.estudos.servico;

// Igual à NegocioException, mas a API devolve 404
public class NaoEncontradoException extends NegocioException {

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
