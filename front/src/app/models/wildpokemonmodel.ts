/**
 * Datos básicos de un Pokémon salvaje para mostrar en la escena de encuentro.
 */
export interface Wildpokemonmodel {
  /** Número de Pokédex para cargar su sprite. */
  id: number;
  /** Nombre del Pokémon salvaje. */
  nombre: string;
  /** Nivel del Pokémon salvaje. */
  nivel: number;
  /** Ruta opcional del sprite; si no se indica, se construye desde `id`. */
  sprite?: string;
  /** Puntos de salud actuales del Pokémon salvaje. */
  hpActual?: number;
  /** Puntos de salud máximos del Pokémon salvaje. */
  hpMaximo?: number;
}

/**
 * Acciones seleccionables desde el menú del encuentro salvaje.
 */
export type EncounterAction = 'battle' | 'bag' | 'run';
