package com.agendasena.agendasena.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> handleReglaNegocio(ReglaNegocioException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConflictoReservaException.class)
    public ResponseEntity<ErrorResponse> handleConflicto(ConflictoReservaException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Red de seguridad para cualquier otro error no anticipado (bugs reales)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenerico(Exception ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado.");
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(HttpStatus status, String mensaje) {
        ErrorResponse error = new ErrorResponse(status.value(), mensaje);
        return new ResponseEntity<>(error, status);
    }
}