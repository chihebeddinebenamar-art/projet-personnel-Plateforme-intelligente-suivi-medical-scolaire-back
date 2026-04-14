package tn.educanet.pfe.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tn.educanet.pfe.exception.BusinessException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, String>> business(BusinessException ex) {
		Map<String, String> body = new HashMap<>();
		body.put("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
		StringBuilder sb = new StringBuilder();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			sb.append(fe.getField()).append(": ").append(fe.getDefaultMessage()).append("; ");
		}
		Map<String, String> body = new HashMap<>();
		body.put("message", sb.toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	/**
	 * Doublon SQL (ex. matricule élève unique) si la validation métier n’a pas intercepté.
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> dataIntegrity(DataIntegrityViolationException ex) {
		Map<String, String> body = new HashMap<>();
		String cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
		if (cause != null && cause.contains("Duplicate entry")) {
			body.put("message",
					"Valeur déjà existante (ex. matricule élève déjà utilisé). Choisissez un autre matricule.");
		} else {
			body.put("message", "Contrainte base de données : " + (cause != null ? cause : ex.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Map<String, String>> dataAccess(DataAccessException ex) {
		Map<String, String> body = new HashMap<>();
		body.put("message",
				"Erreur base de données (connexion MySQL, schéma pfedbback ou table niveau). Détail : "
						+ ex.getMostSpecificCause().getMessage());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
	}
}
