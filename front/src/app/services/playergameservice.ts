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
  currentHp?: number;
  maxHp?: number;
}

export interface EncounterResult {
  encounter: boolean;
  battleId: number | null;
  speciesId: number | null;
  pokemonName: string | null;
  level: number;
}

export interface BattleState {
  id: number;
  playerPokemonId: number;
  wildSpeciesId: number;
  wildName: string;
  wildLevel: number;
  wildCurrentHp: number;
  wildMaxHp: number;
  playerCurrentHp: number;
  playerMaxHp: number;
  status: 'ACTIVE' | 'WON' | 'LOST' | 'FLED' | 'CAPTURED' | string;
  turnNumber: number;
}

@Injectable({ providedIn: 'root' })
export class Playergameservice {
  private readonly http = inject(HttpClient);
  private readonly url = 'http://localhost:8080/player/game';

  private opcionesAuth() {
    const token = localStorage.getItem('pokemagma_token');

    return {
      headers: new HttpHeaders(
        token ? { Authorization: `Bearer ${token}` } : {}
      ),
    };
  }

  obtenerEquipo(): Observable<PlayerPokemon[] | null> {
    return this.http.get<PlayerPokemon[]>(
      `${this.url}/team/getall`,
      this.opcionesAuth()
    );
  }

  obtenerOpcionesIniciales(): Observable<StarterOptions> {
    return this.http.get<StarterOptions>(
      `${this.url}/starters/getoptions`,
      this.opcionesAuth()
    );
  }

  elegirInicial(speciesId: number): Observable<PlayerPokemon> {
    return this.http.post<PlayerPokemon>(
      `${this.url}/starters/select`,
      { speciesId },
      this.opcionesAuth()
    );
  }

  explorarArbusto(
    mapId: string,
    bushId: string
  ): Observable<EncounterResult> {
    return this.http.post<EncounterResult>(
      `${this.url}/explore`,
      { mapId, bushId },
      this.opcionesAuth()
    );
  }

  obtenerCombate(battleId: number): Observable<BattleState> {
    return this.http.get<BattleState>(
      `${this.url}/battles/getbyid/${battleId}`,
      this.opcionesAuth()
    );
  }

  atacar(battleId: number): Observable<BattleState> {
    return this.http.post<BattleState>(
      `${this.url}/battles/${battleId}/attack`,
      {},
      this.opcionesAuth()
    );
  }

  capturar(battleId: number): Observable<BattleState> {
    return this.http.post<BattleState>(
      `${this.url}/battles/${battleId}/capture`,
      {},
      this.opcionesAuth()
    );
  }

  huir(battleId: number): Observable<BattleState> {
    return this.http.post<BattleState>(
      `${this.url}/battles/${battleId}/run`,
      {},
      this.opcionesAuth()
    );
  }
}
