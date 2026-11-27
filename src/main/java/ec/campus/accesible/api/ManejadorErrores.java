package ec.campus.accesible.api;

import ec.campus.accesible.dominio.LugarNoEncontradoException;
import ec.campus.accesible.dominio.MotivoSinRecorrido;
import ec.campus.accesible.dominio.SinRecorridoCompatibleException;
import ec.campus.accesible.dominio.TramoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * RNF-03: toda falla conocida se responde con un mensaje legible y un código HTTP adecuado.
 * Nunca se devuelve la traza interna al cliente.
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    public record Error(String mensaje, List<MotivoSinRecorrido> motivos) {
        Error(String mensaje) {
            this(mensaje, List.of());
        }
    }

    @ExceptionHandler({LugarNoEncontradoException.class, TramoNoEncontradoException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Error noEncontrado(RuntimeException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Error cuerpoInvalido(Exception e) {
        return new Error("Los datos enviados no tienen el formato esperado");
    }

    @ExceptionHandler(SinRecorridoCompatibleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    Error sinRecorrido(SinRecorridoCompatibleException e) {
        return new Error(e.getMessage(), e.motivos());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Error faltaParametro(MissingServletRequestParameterException e) {
        return new Error("Falta el dato «" + e.getParameterName() + "»");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Error datoInvalido(IllegalArgumentException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Error inesperado(Exception e) {
        log.error("Error no controlado", e);
        return new Error("Ocurrió un error inesperado. Intenta de nuevo en unos minutos.");
    }
}
