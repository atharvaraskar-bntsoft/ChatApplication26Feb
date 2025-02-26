package com.alibou.websocket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(UserNotFoundException.class)
      public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
       }  

      @ExceptionHandler(UserAlreadyExistsException.class)
      @ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict: Resource already exists
      public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
       }			   

}
