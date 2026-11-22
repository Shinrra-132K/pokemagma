import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

/** Oferta de Pokémon iniciales asignada por el back al jugador. */
export interface StarterOptions {
  generationOneSpeciesId: number;
  generationTwoSpeciesId: number;
  generationThreeSpeciesId: number;
  selected: boolean;
}

/** Pokémon perteneciente al equipo del jugador. */
export interface PlayerPokemon {
  id: number;
  speciesId: number;
  nickname: string | null;
  level: number;
}

/** Respuesta del back al explorar una casilla de hierba. */
export interface ExploreResult {
  encounter: boolean;
  battleId: number | null;
  speciesId: number | null;
  pokemonName: string | null;
  level: number;
}

/** Estado de un combate devuelto por el back. */
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
  status: string;
  turnNumber: number;
}

@Injectable({ providedIn: 'root' })
export class Playergameservice {
  private http = inject(HttpClient);

  /** URL base de los endpoints de juego del back. */
  private readonly url = 'http://localhost:8080/player/game';

  /**
   * Prepara el encabezado de autorización para los endpoints protegidos.
   * El token se guarda en localStorage al iniciar sesión.
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
   * Consulta el equipo del jugador autenticado.
   * El back responde 204 si el equipo está vacío.
   */
  obtenerEquipo(): Observable<PlayerPokemon[] | null> {
    return this.http.get<PlayerPokemon[]>(
      `${this.url}/team/getall`,
      this.opcionesAuth()
    );
  }

  /**
   * Obtiene las tres opciones iniciales asignadas por el back.
   */
  obtenerOpcionesIniciales(): Observable<StarterOptions> {
    return this.http.get<StarterOptions>(
      `${this.url}/starters/getoptions`,
      this.opcionesAuth()
    );
  }

  /**
   * Envía al back la especie elegida como Pokémon inicial.
   *
   * @param speciesId identificador de especie del Pokémon.
   */
  elegirInicial(speciesId: number): Observable<PlayerPokemon> {
    return this.http.post<PlayerPokemon>(
      `${this.url}/starters/select`,
      { speciesId },
      this.opcionesAuth()
    );
  }

  /**
   * Pide al back que procese el paso del jugador por una casilla de hierba.
   *
   * @param mapId identificador del mapa recorrido.
   * @param bushId identificador de la casilla de hierba.
   */
  explorarArbusto(
    mapId: string,
    bushId: string
  ): Observable<ExploreResult> {
    return this.http.post<ExploreResult>(
      `${this.url}/explore`,
      { mapId, bushId },
      this.opcionesAuth()
    );
  }

  /**
   * Solicita al back que ejecute un turno de ataque.
   *
   * @param battleId identificador del combate activo.
   */
  atacar(battleId: number): Observable<BattleState> {
    return this.http.post<BattleState>(
      `${this.url}/battles/${battleId}/attack`,
      {},
      this.opcionesAuth()
    );
  }

  /**
   * Solicita al back que finalice el combate por huida.
   *
   * @param battleId identificador del combate activo.
   */
  huir(battleId: number): Observable<BattleState> {
    return this.http.post<BattleState>(
      `${this.url}/battles/${battleId}/run`,
      {},
      this.opcionesAuth()
    );
  }

  /**
   * Solicita al back que intente capturar al Pokémon salvaje.
   *
   * @param battleId identificador del combate activo.
   */
  capturar(battleId: number): Observable<BattleState> {
    return this.http.post<BattleState>(
      `${this.url}/battles/${battleId}/capture`,
      {},
      this.opcionesAuth()
    );
  }
}
