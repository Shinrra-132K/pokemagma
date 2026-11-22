import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

export interface EventoAuditoria {
  id: number;
  playerId: number | null;
  type: string;
  description: string;
  createdAt: string;
}

export interface PresenciaJugador {
  userId: number;
  username: string;
  status: 'ACTIVE' | 'DISCONNECTED' | string;
  lastSeenAt: string | null;
}

export interface UsuarioDeshabilitado {
  id: number;
  username: string;
  email: string;
  role: string;
  enabled: boolean;
  emailVerified: boolean;
  registeredAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class Adminauditservice {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/admin/audit';

  private obtenerHeaders(): HttpHeaders {
    const token = localStorage.getItem('pokemagma_token');

    if (!token) {
      throw new Error('No se encontró el token. Inicia sesión nuevamente.');
    }

    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  private listaVaciaSiNoHayContenido<T>(
    observable: Observable<T[] | null>
  ): Observable<T[]> {
    return observable.pipe(map(respuesta => respuesta ?? []));
  }

  obtenerEventos(): Observable<EventoAuditoria[]> {
    return this.listaVaciaSiNoHayContenido(
      this.http.get<EventoAuditoria[] | null>(
        `${this.baseUrl}/events/getall`,
        { headers: this.obtenerHeaders() }
      )
    );
  }

  obtenerEventoPorId(id: number): Observable<EventoAuditoria> {
    return this.http.get<EventoAuditoria>(
      `${this.baseUrl}/events/getbyid/${id}`,
      { headers: this.obtenerHeaders() }
    );
  }

  obtenerEventosPorJugador(
    playerId: number
  ): Observable<EventoAuditoria[]> {
    return this.listaVaciaSiNoHayContenido(
      this.http.get<EventoAuditoria[] | null>(
        `${this.baseUrl}/events/getbyplayer/${playerId}`,
        { headers: this.obtenerHeaders() }
      )
    );
  }

  obtenerPresencia(): Observable<PresenciaJugador[]> {
    return this.listaVaciaSiNoHayContenido(
      this.http.get<PresenciaJugador[] | null>(
        `${this.baseUrl}/users/presence`,
        { headers: this.obtenerHeaders() }
      )
    );
  }

  obtenerUsuariosDeshabilitados(): Observable<UsuarioDeshabilitado[]> {
    return this.listaVaciaSiNoHayContenido(
      this.http.get<UsuarioDeshabilitado[] | null>(
        `${this.baseUrl}/users/disabled`,
        { headers: this.obtenerHeaders() }
      )
    );
  }

  obtenerCantidadDeshabilitados(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(
      `${this.baseUrl}/users/disabled/count`,
      { headers: this.obtenerHeaders() }
    );
  }
}
