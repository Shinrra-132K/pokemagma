import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BattleStateModel } from '../models/battlestatemodel';
import { ExploreResultModel } from '../models/exploreresultmodel';
import { PlayerPokemonModel } from '../models/playerpokemonmodel';
import { StarterOptionsModel } from '../models/starteroptionsmodel';
import { InventoryItemModel } from '../models/inventoryitemmodel';

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
  obtenerEquipo(): Observable<PlayerPokemonModel[] | null> {
    return this.http.get<PlayerPokemonModel[]>(
      `${this.url}/team/getall`,
      this.opcionesAuth()
    );
  }

  /**
   * Obtiene las tres opciones iniciales asignadas por el back.
   */
  obtenerOpcionesIniciales(): Observable<StarterOptionsModel> {
    return this.http.get<StarterOptionsModel>(
      `${this.url}/starters/getoptions`,
      this.opcionesAuth()
    );
  }

  /**
   * Envía al back la especie elegida como Pokémon inicial.
   *
   * @param speciesId identificador de especie del Pokémon.
   */
  elegirInicial(speciesId: number): Observable<PlayerPokemonModel> {
    return this.http.post<PlayerPokemonModel>(
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
  ): Observable<ExploreResultModel> {
    return this.http.post<ExploreResultModel>(
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
  atacar(battleId: number): Observable<BattleStateModel> {
    return this.http.post<BattleStateModel>(
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
  huir(battleId: number): Observable<BattleStateModel> {
    return this.http.post<BattleStateModel>(
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
  capturar(battleId: number): Observable<BattleStateModel> {
    return this.http.post<BattleStateModel>(
      `${this.url}/battles/${battleId}/capture`,
      {},
      this.opcionesAuth()
    );
  }

  obtenerCombate(battleId: number): Observable<BattleStateModel> {
    return this.http.get<BattleStateModel>(
      `${this.url}/battles/getbyid/${battleId}`,
      this.opcionesAuth()
    );
  }

  obtenerInventario(): Observable<InventoryItemModel[] | null> {
    return this.http.get<InventoryItemModel[]>(
      'http://localhost:8080/player/inventory/getall',
      this.opcionesAuth()
    );
  }

  usarObjeto(
    itemCode: string,
    playerPokemonId: number
  ): Observable<PlayerPokemonModel> {
    return this.http.post<PlayerPokemonModel>(
      `${this.url}/items/use`,
      { itemCode, playerPokemonId },
      this.opcionesAuth()
    );
  }
}
