package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando una acción no es válida para el estado actual del juego.
 */
public class GameRuleException extends Exception {

	/**
	 * Identificador de serialización de la excepción
	 */
    private static final long serialVersionUID = 1L;

    /** Crea la excepción con un mensaje general de regla de juego. */
    public GameRuleException() {
        super("La acción no es válida para el estado actual del juego.");
    }
}

