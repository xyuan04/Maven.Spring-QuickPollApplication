package io.zipcoder.tc_spring_poll_application.exception;

import io.zipcoder.tc_spring_poll_application.error.ErrorDetail;
import io.zipcoder.tc_spring_poll_application.error.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException rnfe, HttpServletResponse request) {
        ErrorDetail errorDetail = new ErrorDetail("Resource Not Found", HttpStatus.NOT_FOUND.value(), rnfe.getMessage(), new Date().getTime(), rnfe.getClass().getName());

        return new ResponseEntity<>(errorDetail, null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException manve, HttpServletRequest request) {
        ErrorDetail errorDetail = new ErrorDetail("Resource Not Found", HttpStatus.NOT_FOUND.value(), manve.getMessage(), new Date().getTime(), manve.getClass().getName());

        List<FieldError> fieldErrors = manve.getBindingResult().getFieldErrors();
        for(FieldError fieldError : fieldErrors) {
            List<ValidationError> validationErrorList = errorDetail.getErrors().get(fieldError.getField());
            if(validationErrorList == null) {
                validationErrorList = new ArrayList<>();
                errorDetail.getErrors().put(fieldError.getField(), validationErrorList);
            }
            ValidationError validationError = new ValidationError();
            validationError.setCode(fieldError.getCode());
            validationError.setMessage(messageSource.getMessage(fieldError, null));
            validationErrorList.add(validationError);
        }

        return new ResponseEntity<>(errorDetail, null, HttpStatus.BAD_REQUEST);
    }
}
