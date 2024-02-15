package madstodolist.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED,reason = "No tienes permisos para acceder a esta página")
public class UnauthorizedException extends RuntimeException{

}

