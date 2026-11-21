import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Tmjmodel } from '../models/tmjmodel';
import { Mapadatosmodel } from '../models/mapadatosmodel';

@Injectable({ providedIn: 'root' })
export class Mapaservice {
  private http = inject(HttpClient);

  private codigos: Record<string, number> = { muro: 1, agua: 2, vacio: 3, hierba: 4 };

  cargarMapa(url: string): Observable<Mapadatosmodel> {
    return this.http.get<Tmjmodel>(url).pipe(map(tmj => this.construirGrilla(tmj)));
  }

  private construirGrilla(tmj: Tmjmodel): Mapadatosmodel {
    const t = tmj.tilewidth;
    const grilla: number[][] = Array.from({ length: tmj.height }, () => new Array<number>(tmj.width).fill(0));

    for (const capa of tmj.layers) {
      if (capa.type !== 'objectgroup' || !capa.objects) continue;
      for (const o of capa.objects) {
        const codigo = this.codigos[o.type];
        if (!codigo) continue;
        const c0 = Math.round(o.x / t), f0 = Math.round(o.y / t);
        const c1 = Math.round((o.x + o.width) / t), f1 = Math.round((o.y + o.height) / t);
        for (let f = f0; f < f1; f++) {
          for (let c = c0; c < c1; c++) {
            grilla[f][c] = codigo;
          }
        }
      }
    }
    return { columnas: tmj.width, filas: tmj.height, tamanoTile: t, grilla };
  }

  esCaminable(datos: Mapadatosmodel, col: number, fila: number): boolean {
    const valor = datos.grilla[fila]?.[col];
    return valor === 0 || valor === 4; // libre o hierba
  }
}
