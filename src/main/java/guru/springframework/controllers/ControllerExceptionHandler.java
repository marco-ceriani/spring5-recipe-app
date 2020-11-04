package guru.springframework.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public String handleNumberFormatException(Exception exception, Model model) {
        log.error("Binding error: Invalid number", exception.getMessage());
        model.addAttribute("exception", exception);
        return "error400";
    }

}
