package root.exception;

import root.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    /**
     * -------------------------------------
     * Handler Spring Boot default exception
     * -------------------------------------
     */

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseDTO<String> allException(Exception e, HttpServletResponse response) {
        log.warn("WARN", e);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseDTO<String> httpMessageNotReadableException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    @ExceptionHandler({BindException.class})
    public ResponseDTO<String> bindException(BindException e, HttpServletResponse response) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();

        String msg = "";
        for (ObjectError error : errors) {
            FieldError fieldError = (FieldError) error;
            msg += fieldError.getField() + ":" + error.getDefaultMessage() + ";";
        }

        int status = HttpStatus.BAD_REQUEST.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(msg).build();
    }

    /**
     * -------------------------------------
     * Custom ExceptionHandler (Service)
     * -------------------------------------
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseDTO<String> resourceNotFoundException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.NOT_FOUND.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ResponseDTO<String> duplicateResourceException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.CONFLICT.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseDTO<String> requestValidationException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.BAD_REQUEST.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }
    /*==============================================*/
    /*@ExceptionHandler({NullPointerException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseDTO<String> nullPointerException(NullPointerException e) {
        String msg = "Null pointer";

        return ResponseDTO.<String>builder().status(404).msg(msg).build();
    }*/

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseDTO<String> accessDeniedException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.FORBIDDEN.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseDTO<String> insufficientAuthenticationException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseDTO<String> usernameNotFoundException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }

    // Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseDTO<String> forbiddenException(Exception e, HttpServletResponse response) {
        int status = HttpStatus.FORBIDDEN.value();
        response.setStatus(status);
        return ResponseDTO.<String>builder().status(status).msg(e.getMessage()).build();
    }
}
