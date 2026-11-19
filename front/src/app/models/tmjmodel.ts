export interface Objetotmjmodel {
  x: number;
  y: number;
  width: number;
  height: number;
  type: string;
}

export interface Capatmjmodel {
  name: string;
  type: string;
  objects?: Objetotmjmodel[];
}

export interface Tmjmodel {
  width: number;
  height: number;
  tilewidth: number;
  layers: Capatmjmodel[];
}
