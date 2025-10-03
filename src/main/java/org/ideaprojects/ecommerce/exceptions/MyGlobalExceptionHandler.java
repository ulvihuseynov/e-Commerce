package org.ideaprojects.ecommerce.exceptions;

import org.ideaprojects.ecommerce.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@ControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> methodArgumentNotValidException(MethodArgumentNotValidException ex){

        Map<String,String> errors=new HashMap<>();
        ex.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return  new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> resourceNotFoundException(ResourceNotFoundException ex){

        String errorMessage=ex.getMessage();

        APIResponse apiResponse=new APIResponse();
        apiResponse.setMessage(errorMessage);
        apiResponse.setStatus(false);
        
        return  new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> apiException(APIException ex){

        String errorMessage=ex.getMessage();

        APIResponse apiResponse=new APIResponse();
        apiResponse.setMessage(errorMessage);
        apiResponse.setStatus(false);

        return  new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
