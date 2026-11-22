/** Objeto y cantidad pertenecientes al inventario del jugador. */
export interface InventoryItemModel {
  id: number;
  playerId: number;
  itemCode: string;
  quantity: number;
}
