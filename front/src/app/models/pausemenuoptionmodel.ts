/**
 * Identificadores de las opciones disponibles en el menú lateral de pausa/inicio.
 */
export type PauseMenuAction = 'bag' | 'pokedex' | 'profile' | 'logout';

/**
 * Representa una opción visible dentro del menú lateral derecho.
 */
export interface Pausemenuoptionmodel {
  /** Identificador interno de la acción al elegir esta opción. */
  accion: PauseMenuAction;
  /** Texto mostrado en el menú. */
  etiqueta: string;
}
