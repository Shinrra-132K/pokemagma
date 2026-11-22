import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import {
  AdminGameEvent,
  AdminUser,
  AdminUserPresence,
} from '../models/adminauditmodels';

@Injectable({ providedIn: 'root' })
export class Adminauditservice {
  private readonly http = inject(HttpClient);
  private readonly url = 'http://localhost:8080/admin/audit';

  /**
   * Prepara el token que protege las rutas administrativas.
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
   * Convierte las respuestas vacías del backend (204) en listas vacías.
   */
  private obtenerLista<T>(ruta: string): Observable<T[]> {
    return this.http
      .get<T[] | null>(`${this.url}${ruta}`, this.opcionesAuth())
      .pipe(map(respuesta => respuesta ?? []));
  }

  /**
   * Obtiene los eventos de auditoría, del más reciente al más antiguo.
   */
  obtenerEventos(): Observable<AdminGameEvent[]> {
    return this.obtenerLista<AdminGameEvent>('/events/getall');
  }

  /**
   * Obtiene los eventos asociados a un jugador.
   */
  obtenerEventosJugador(playerId: number): Observable<AdminGameEvent[]> {
    return this.obtenerLista<AdminGameEvent>(
      `/events/getbyplayer/${playerId}`
    );
  }

  /**
   * Busca el detalle de un evento por su ID.
   */
  obtenerEvento(id: number): Observable<AdminGameEvent> {
    return this.http.get<AdminGameEvent>(
      `${this.url}/events/getbyid/${id}`,
      this.opcionesAuth()
    );
  }

  /**
   * Obtiene los jugadores y su estado de presencia.
   */
  obtenerPresencia(): Observable<AdminUserPresence[]> {
    return this.obtenerLista<AdminUserPresence>('/users/presence');
  }

  /**
   * Obtiene las cuentas deshabilitadas.
   * El backend debe responder sin contraseñas.
   */
  obtenerDeshabilitados(): Observable<AdminUser[]> {
    return this.obtenerLista<AdminUser>('/users/disabled');
  }

  /**
   * Obtiene la cantidad de cuentas deshabilitadas.
   */
  obtenerConteoDeshabilitados(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(
      `${this.url}/users/disabled/count`,
      this.opcionesAuth()
    );
  }
}
