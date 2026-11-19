package br.mackenzie.estudos.servico;

// Quebra de uma regra dos casos de uso. A mensagem é a que o usuário vê na tela (a API devolve com status 400)
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
