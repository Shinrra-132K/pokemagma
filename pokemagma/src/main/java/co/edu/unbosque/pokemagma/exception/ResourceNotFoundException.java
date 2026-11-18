package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando no se encuentra el recurso solicitado.
 */
public class ResourceNotFoundException extends Exception {

    /** Identificador de serialización de la excepción. */
    private static final long serialVersionUID = 1L;

    /** Crea la excepción con el mensaje estándar de recurso no encontrado. */
    public ResourceNotFoundException() {
        super("Recurso no encontrado");
    }
}
