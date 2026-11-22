import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Authservice } from '../../services/authservice';
import {
  Adminauditservice,
  EventoAuditoria,
  PresenciaJugador,
  UsuarioDeshabilitado,
} from '../../services/adminauditservice';

@Component({
  selector: 'app-admincomponent',
  standalone: false,
  styleUrl: './admincomponent.css',
  templateUrl: './admincomponent.html',
})
export class Admincomponent implements OnInit, OnDestroy {
  private readonly auditoriaService = inject(Adminauditservice);
  private readonly authService = inject(Authservice);
  private readonly router = inject(Router);

  eventos: EventoAuditoria[] = [];
  jugadores: PresenciaJugador[] = [];
  usuariosDeshabilitados: UsuarioDeshabilitado[] = [];

  eventoSeleccionado: EventoAuditoria | null = null;
  nombreAdministrador = 'Administrador';

  buscar = '';
  buscarJugador = '';
  filtroTipo = '';
  filtroJugadorId: number | null = null;

  cargando = false;
  error = '';
  aviso = '';
  cantidadDeshabilitados = 0;

  private temporizador: ReturnType<typeof setInterval> | null = null;
  private suscripcionDetalle?: Subscription;

  ngOnInit(): void {
    this.cargarDatos();

    // Actualiza presencia y eventos cada 15 segundos mientras el panel está abierto.
    this.temporizador = setInterval(() => {
      this.cargarDatos(false);
    }, 15000);
  }

  ngOnDestroy(): void {
    if (this.temporizador) {
      clearInterval(this.temporizador);
    }

    this.suscripcionDetalle?.unsubscribe();
  }

  get cantidadConectados(): number {
    return this.jugadores.filter(
      jugador => jugador.status === 'ACTIVE'
    ).length;
  }

  get cantidadDesconectados(): number {
    return this.jugadores.filter(
      jugador => jugador.status !== 'ACTIVE'
    ).length;
  }

  get ultimosEventos(): EventoAuditoria[] {
    return [...this.eventos]
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
      )
      .slice(0, 5);
  }

  get jugadoresFiltrados(): PresenciaJugador[] {
    const texto = this.buscarJugador.trim().toLowerCase();

    if (!texto) {
      return this.jugadores;
    }

    return this.jugadores.filter(jugador =>
      `${jugador.username} ${jugador.userId}`
        .toLowerCase()
        .includes(texto)
    );
  }

  get eventosFiltrados(): EventoAuditoria[] {
    const texto = this.buscar.trim().toLowerCase();

    return [...this.eventos]
      .filter(evento => {
        const coincideTipo =
          !this.filtroTipo || evento.type === this.filtroTipo;

        const coincideJugador =
          this.filtroJugadorId === null ||
          this.filtroJugadorId === undefined ||
          evento.playerId === Number(this.filtroJugadorId);

        const nombre = this.nombreJugador(evento.playerId).toLowerCase();
        const descripcion = (evento.description ?? '').toLowerCase();
        const tipo = (evento.type ?? '').toLowerCase();

        const coincideTexto =
          !texto ||
          nombre.includes(texto) ||
          descripcion.includes(texto) ||
          tipo.includes(texto) ||
          String(evento.playerId ?? '').includes(texto);

        return coincideTipo && coincideJugador && coincideTexto;
      })
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
      );
  }

  cargarDatos(mostrarIndicador = true): void {
    if (mostrarIndicador) {
      this.cargando = true;
    }

    this.error = '';
    this.aviso = '';

    forkJoin({
      eventos: this.auditoriaService.obtenerEventos().pipe(
        catchError(error => {
          this.registrarError('eventos', error);
          return of([] as EventoAuditoria[]);
        })
      ),
      jugadores: this.auditoriaService.obtenerPresencia().pipe(
        catchError(error => {
          this.registrarError('presencia de jugadores', error);
          return of([] as PresenciaJugador[]);
        })
      ),
      deshabilitados:
        this.auditoriaService.obtenerUsuariosDeshabilitados().pipe(
          catchError(error => {
            this.registrarError('cuentas deshabilitadas', error);
            return of([] as UsuarioDeshabilitado[]);
          })
        ),
      cantidadDeshabilitados:
        this.auditoriaService.obtenerCantidadDeshabilitados().pipe(
          catchError(error => {
            this.registrarError('conteo de cuentas deshabilitadas', error);
            return of({ count: 0 });
          })
        ),
    }).subscribe({
      next: resultado => {
        this.eventos = resultado.eventos;
        this.jugadores = resultado.jugadores;
        this.usuariosDeshabilitados = resultado.deshabilitados;
        this.cantidadDeshabilitados =
          resultado.cantidadDeshabilitados.count ?? 0;
        this.cargando = false;

        if (!this.error) {
          this.aviso = 'Datos actualizados correctamente.';
        }
      },
      error: () => {
        this.error = 'No se pudieron cargar los datos de administración.';
        this.cargando = false;
      },
    });
  }

  verDetalle(id: number): void {
    this.suscripcionDetalle?.unsubscribe();
    this.error = '';
    this.eventoSeleccionado = null;

    this.suscripcionDetalle = this.auditoriaService
      .obtenerEventoPorId(id)
      .subscribe({
        next: evento => {
          this.eventoSeleccionado = evento;
        },
        error: error => {
          this.error = this.mensajeError(error);
        },
      });
  }

  verEventosDelJugador(playerId: number): void {
    this.filtroJugadorId = playerId;
    this.filtroTipo = '';
    this.buscar = '';

    this.auditoriaService.obtenerEventosPorJugador(playerId).subscribe({
      next: eventos => {
        this.eventos = eventos;
        this.eventoSeleccionado = null;
        this.aviso = `Se cargaron los eventos del jugador ${playerId}.`;
      },
      error: error => {
        this.error = this.mensajeError(error);
      },
    });
  }

  limpiarFiltros(): void {
    this.buscar = '';
    this.buscarJugador = '';
    this.filtroTipo = '';
    this.filtroJugadorId = null;
    this.eventoSeleccionado = null;
    this.cargarDatos();
  }

  cerrarDetalle(): void {
    this.eventoSeleccionado = null;
  }

  nombreJugador(playerId: number | null): string {
    if (playerId === null || playerId === undefined) {
      return 'Sistema';
    }

    const jugador = this.jugadores.find(
      item => item.userId === playerId
    );

    return jugador?.username ?? `Jugador ${playerId}`;
  }

  exportarCsv(): void {
    const filas = this.eventosFiltrados;

    if (filas.length === 0) {
      this.aviso = 'No hay registros para exportar.';
      return;
    }

    const encabezados = [
      'ID',
      'ID jugador',
      'Usuario',
      'Tipo',
      'Descripción',
      'Fecha',
    ];

    const escapar = (valor: unknown): string =>
      `"${String(valor ?? '').replace(/"/g, '""')}"`;

    const contenido = [
      encabezados.map(escapar).join(','),
      ...filas.map(evento =>
        [
          evento.id,
          evento.playerId ?? '',
          this.nombreJugador(evento.playerId),
          evento.type,
          evento.description,
          evento.createdAt,
        ]
          .map(escapar)
          .join(',')
      ),
    ].join('\r\n');

    const archivo = new Blob(['\uFEFF', contenido], {
      type: 'text/csv;charset=utf-8;',
    });

    const url = URL.createObjectURL(archivo);
    const enlace = document.createElement('a');
    enlace.href = url;
    enlace.download = `auditorias-${new Date()
      .toISOString()
      .slice(0, 10)}.csv`;
    enlace.click();
    URL.revokeObjectURL(url);
  }

  cerrarSesion(): void {
    this.authService.cerrarSesion();
    void this.router.navigate(['/login']);
  }

  private registrarError(seccion: string, error: unknown): void {
    const detalle = this.mensajeError(error);
    this.error = this.error
      ? `${this.error} ${seccion}: ${detalle}`
      : `No se pudieron cargar ${seccion}: ${detalle}`;
  }

  private mensajeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401 || error.status === 403) {
        return 'Tu sesión no tiene autorización. Inicia sesión con una cuenta administradora.';
      }

      if (error.status === 0) {
        return 'No se pudo conectar con el backend en http://localhost:8080.';
      }

      return `El servidor respondió con el error ${error.status}.`;
    }

    if (error instanceof Error) {
      return error.message;
    }

    return 'Ocurrió un error inesperado.';
  }
}
