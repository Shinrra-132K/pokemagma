/** Respuesta del back al explorar una casilla de hierba. */
export interface ExploreResultModel {
  encounter: boolean;
  battleId: number | null;
  speciesId: number | null;
  pokemonName: string | null;
  level: number;
}
