export type Direccionmodel = 'abajo' | 'arriba' | 'izquierda' | 'derecha';

export interface Npcmodel {
  id: string;
  nombre: string;
  col: number;
  fila: number;
  direccion: Direccionmodel;
  sprite: string;
  dialogo: string[];
}

export interface Jugadormodel {
  col: number;
  fila: number;
  direccion: Direccionmodel;
  moviendo: boolean;
  progreso: number; // 0 a 1 dentro del paso actual
  paso: number;     // alterna el pie izquierdo/derecho
}

export interface Dialogomodel {
  nombre: string;
  lineas: string[];
}
