import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StarterOptions {
  generationOneSpeciesId: number;
  generationTwoSpeciesId: number;
  generationThreeSpeciesId: number;
  selected: boolean;
}

export interface PlayerPokemon {
  id: number;
  speciesId: number;
  nickname: string | null;
  level: number;
}

@Injectable({ providedIn: 'root' })
export class Playergameservice {
  private http = inject(HttpClient);
  private readonly url = 'http://localhost:8080/player/game';

  /**
   * Incluye el JWT en las solicitudes a endpoints protegidos.
   * El login ya guarda el token en localStorage.
   */
  private opcionesAuth() {
    const token = localStorage.getItem('pokemagma_token');

    return {
      headers: new HttpHeaders(
        token ? { Authorization: `Bearer ${token}` } : {}
      ),
    };
  }

  /**
   * Consulta el equipo. El back responde 204 si está vacío.
   */
  obtenerEquipo(): Observable<PlayerPokemon[] | null> {
    return this.http.get<PlayerPokemon[]>(
      `${this.url}/team/getall`,
      this.opcionesAuth()
    );
  }

  /**
   * Obtiene la oferta de tres Pokémon que asignó el back al jugador.
   */
  obtenerOpcionesIniciales(): Observable<StarterOptions> {
    return this.http.get<StarterOptions>(
      `${this.url}/starters/getoptions`,
      this.opcionesAuth()
    );
  }

  /**
   * Guarda en el back el Pokémon inicial elegido.
   */
  elegirInicial(speciesId: number): Observable<PlayerPokemon> {
    return this.http.post<PlayerPokemon>(
      `${this.url}/starters/select`,
      { speciesId },
      this.opcionesAuth()
    );
  }
}
