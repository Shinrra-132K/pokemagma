/** Pokémon perteneciente al equipo del jugador. */
export interface PlayerPokemonModel {
  /** Identificador del Pokémon del jugador, distinto al de su especie. */
  id: number;
  /** Identificador de especie de PokéAPI. */
  speciesId: number;
  /** Apodo elegido para el Pokémon, si tiene uno. */
  nickname: string | null;
  /** Nivel actual. */
  level: number;
  /** Puntos de salud actuales. */
  currentHp: number;
  /** Puntos de salud máximos. */
  maxHp: number;
}
