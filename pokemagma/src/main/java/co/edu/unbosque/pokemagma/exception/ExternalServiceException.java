package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando no es posible consultar el proveedor externo de datos Pokémon.
 */
public class ExternalServiceException extends Exception {

	/**
	 * Identificador de serialización de la excepción
	 */
    private static final long serialVersionUID = 1L;

    /** Crea la excepción con un mensaje general del servicio externo. */
    public ExternalServiceException() {
        super("No fue posible consultar el servicio de datos Pokémon.");
    }
}

