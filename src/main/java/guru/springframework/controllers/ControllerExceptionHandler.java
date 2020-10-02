package guru.springframework.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public ModelAndView handleNumberFormatException(Exception exception) {
        log.error("Invalid number", exception);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.setViewName("error400");
        modelAndView.addObject("exception", exception);
        return modelAndView;
    }

}
