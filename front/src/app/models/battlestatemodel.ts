/** Estado de combate devuelto por el back. */
export interface BattleStateModel {
  id: number;
  playerPokemonId: number;
  wildSpeciesId: number;
  wildName: string;
  wildLevel: number;
  wildCurrentHp: number;
  wildMaxHp: number;
  playerCurrentHp: number;
  playerMaxHp: number;
  status: string;
  turnNumber: number;
}
