/**
 * Categorías o bolsillos disponibles dentro de la mochila.
 */
export type BagPocketType = 'items' | 'pokeballs' | 'key-items';

/**
 * Representa un objeto almacenado dentro de un bolsillo de la mochila.
 */
export interface BagItemModel {
  /** Identificador único del objeto. */
  id: string;
  /** Nombre visible del objeto. */
  nombre: string;
  /** Cantidad disponible en la mochila. */
  cantidad: number;
  /** Breve descripción del efecto o uso del objeto. */
  descripcion: string;
}

/**
 * Representa una sección o bolsillo de la mochila junto con sus objetos.
 */
export interface BagPocketModel {
  /** Identificador interno del bolsillo. */
  tipo: BagPocketType;
  /** Título mostrado en la barra central (ej. "Items", "Poké Balls", "Key Items"). */
  titulo: string;
  /** Índice del cuadro dentro de la tira `bagchange.png` (0 a 4). */
  indiceSpriteMochila: number;
  /** Lista de objetos dentro de este espacio. */
  objetos: BagItemModel[];
}
