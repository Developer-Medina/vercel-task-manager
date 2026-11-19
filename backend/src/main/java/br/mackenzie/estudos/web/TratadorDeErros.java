package br.mackenzie.estudos.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.mackenzie.estudos.servico.NaoEncontradoException;
import br.mackenzie.estudos.servico.NegocioException;

// Transforma as exceções do serviço em {"erro": "mensagem"}, que o front mostra na tela
@RestControllerAdvice
public class TratadorDeErros {

    // A NaoEncontradoException é filha da NegocioException, então o Spring usa este handler primeiro
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(NaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, String>> negocio(NegocioException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}
