import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Adminauditservice } from '../../services/adminauditservice';
import {
  AdminGameEvent,
  AdminUserPresence,
  AdminUser,
  AdminEventType,
} from '../../models/adminauditmodels';

type FiltroEstado = 'TODOS' | 'ACTIVE' | 'DISCONNECTED';
type FiltroEvento = 'TODOS' | AdminEventType;

@Component({
  selector: 'app-admincomponent',
  standalone: false,
  styleUrl: './admincomponent.css',
  templateUrl: './admincomponent.html',
})
export class Admincomponent implements OnInit, OnDestroy {
  private readonly adminAuditService = inject(Adminauditservice);

  jugadores: AdminUserPresence[] = [];
  eventos: AdminGameEvent[] = [];
  usuariosDeshabilitados: AdminUser[] = [];
  eventoSeleccionado: AdminGameEvent | null = null;

  totalDeshabilitados = 0;
  cargando = false;
  cargandoEventos = false;
  mensajeError = '';

  busquedaJugadores = '';
  busquedaEventos = '';
  filtroEstado: FiltroEstado = 'TODOS';
  filtroEvento: FiltroEvento = 'TODOS';
  jugadorSeleccionado = '';

  private temporizadorPresencia?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.cargarDatos();

    // Actualiza la presencia con frecuencia menor a la ventana de 30 s
    // que usa el backend para considerar activo a un jugador.
    this.temporizadorPresencia = setInterval(
      () => this.actualizarPresencia(),
      15_000
    );
  }

  ngOnDestroy(): void {
    if (this.temporizadorPresencia) {
      clearInterval(this.temporizadorPresencia);
    }
  }

  cargarDatos(): void {
    this.cargando = true;
    this.mensajeError = '';

    forkJoin({
      presencia: this.adminAuditService.obtenerPresencia(),
      eventos: this.adminAuditService.obtenerEventos(),
      deshabilitados: this.adminAuditService.obtenerDeshabilitados(),
      conteoDeshabilitados:
        this.adminAuditService.obtenerConteoDeshabilitados(),
    }).subscribe({
      next: datos => {
        this.jugadores = datos.presencia ?? [];
        this.eventos = datos.eventos ?? [];
        this.usuariosDeshabilitados = datos.deshabilitados ?? [];
        this.totalDeshabilitados = datos.conteoDeshabilitados.count ?? 0;
        this.cargando = false;
      },
      error: error => {
        this.cargando = false;
        this.mensajeError =
          error.status === 403
            ? 'Tu cuenta no tiene permisos de administrador.'
            : 'No se pudieron cargar los datos del panel. Revisa la conexión con el backend.';
      },
    });
  }

  private actualizarPresencia(): void {
    this.adminAuditService.obtenerPresencia().subscribe({
      next: presencia => {
        this.jugadores = presencia ?? [];
      },
      error: () => {
        // La siguiente actualización automática o manual puede recuperar la conexión.
      },
    });
  }

  get jugadoresFiltrados(): AdminUserPresence[] {
    const texto = this.busquedaJugadores.trim().toLocaleLowerCase();

    return this.jugadores.filter(jugador => {
      const coincideEstado =
        this.filtroEstado === 'TODOS' ||
        jugador.status === this.filtroEstado;

      const coincideTexto =
        !texto ||
        jugador.username.toLocaleLowerCase().includes(texto) ||
        String(jugador.userId).includes(texto);

      return coincideEstado && coincideTexto;
    });
  }

  get eventosFiltrados(): AdminGameEvent[] {
    const texto = this.busquedaEventos.trim().toLocaleLowerCase();

    return this.eventos.filter(evento => {
      const coincideTipo =
        this.filtroEvento === 'TODOS' ||
        evento.type === this.filtroEvento;

      const nombreJugador = this.nombreJugador(evento.playerId)
        .toLocaleLowerCase();

      const coincideTexto =
        !texto ||
        evento.description.toLocaleLowerCase().includes(texto) ||
        nombreJugador.includes(texto) ||
        String(evento.playerId ?? '').includes(texto) ||
        String(evento.id).includes(texto);

      return coincideTipo && coincideTexto;
    });
  }

  get jugadoresConectados(): number {
    return this.jugadores.filter(jugador => jugador.status === 'ACTIVE').length;
  }

  get jugadoresDesconectados(): number {
    return this.jugadores.filter(
      jugador => jugador.status === 'DISCONNECTED'
    ).length;
  }

  nombreJugador(playerId: number | null): string {
    if (playerId === null) {
      return 'Sistema';
    }

    const jugador = this.jugadores.find(item => item.userId === playerId);
    return jugador?.username ?? `Jugador #${playerId}`;
  }

  cambiarEstado(filtro: FiltroEstado): void {
    this.filtroEstado = filtro;
  }

  cambiarTipoEvento(valor: string): void {
    this.filtroEvento =
      valor === 'TODOS' ? 'TODOS' : (valor as AdminEventType);
  }

  cargarEventosJugador(valor: string): void {
    this.jugadorSeleccionado = valor;
    this.eventoSeleccionado = null;
    this.cargandoEventos = true;
    this.mensajeError = '';

    const solicitud = valor
      ? this.adminAuditService.obtenerEventosJugador(Number(valor))
      : this.adminAuditService.obtenerEventos();

    solicitud.subscribe({
      next: eventos => {
        this.eventos = eventos ?? [];
        this.cargandoEventos = false;
      },
      error: () => {
        this.cargandoEventos = false;
        this.mensajeError = 'No se pudieron cargar los eventos de auditoría.';
      },
    });
  }

  verDetalleEvento(id: number): void {
    this.adminAuditService.obtenerEvento(id).subscribe({
      next: evento => {
        this.eventoSeleccionado = evento;
      },
      error: () => {
        this.mensajeError = `No se pudo cargar el evento #${id}.`;
      },
    });
  }

  cerrarDetalleEvento(): void {
    this.eventoSeleccionado = null;
  }

  etiquetaEvento(tipo: AdminEventType): string {
    const etiquetas: Record<AdminEventType, string> = {
      LOGIN: 'Inicio de sesión',
      LOGOUT: 'Cierre de sesión',
      STARTER_SELECTED: 'Inicial elegido',
      WILD_ENCOUNTER: 'Encuentro salvaje',
      BATTLE: 'Combate',
      POKEMON_CAUGHT: 'Pokémon capturado',
      ITEM_ACQUIRED: 'Objeto obtenido',
      ITEM_USED: 'Objeto usado',
      ACCOUNT_DISABLED: 'Cuenta deshabilitada',
    };

    return etiquetas[tipo] ?? tipo;
  }

  etiquetaEstado(estado: string): string {
    return estado === 'ACTIVE' ? 'Conectado' : 'Desconectado';
  }
}
