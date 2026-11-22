/**
 * Representa una entrada dentro de la Pokédex del jugador.
 */
export interface Pokedexmodel {
  /** Número nacional o regional del Pokémon. */
  numero: number;
  /** Nombre del Pokémon. */
  nombre: string;
  /** Indica si el jugador ya lo ha visto o registrado. */
  visto: boolean;
  /** Indica si el jugador ya lo ha capturado. */
  capturado: boolean;
  /** Tipo o tipos principales para mostrar en la ficha. */
  tipo: string;
  /** Breve descripción de entrada en la Pokédex. */
  descripcion: string;
  /** Ruta opcional al sprite del Pokémon. */
  sprite?: string;
}
