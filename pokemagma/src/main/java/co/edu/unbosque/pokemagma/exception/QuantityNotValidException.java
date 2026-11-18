package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando la cantidad de un objeto no es válida.
 */
public class QuantityNotValidException extends Exception {

	/** Identificador de serialización de la excepción. */
	private static final long serialVersionUID = 1L;

	/** Crea la excepción con el mensaje estándar de cantidad no válida. */
	public QuantityNotValidException() {
		super("Cantidad no valida");
	}
}
