import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  Output,
  signal,
} from '@angular/core';
import { PauseMenuAction, Pausemenuoptionmodel } from '../../models/pausemenuoptionmodel';

/**
 * Menú lateral derecho semitransparente que se abre con `Enter` en el mapa.
 * Permite navegar entre Mochila, Pokédex, Mi perfil y Cerrar sesión.
 */
@Component({
  selector: 'app-menucomponent',
  standalone: false,
  styleUrl: './menucomponent.css',
  templateUrl: './menucomponent.html',
})
export class Menucomponent implements OnChanges {
  /** Controla si el menú lateral se encuentra visible sobre el mapa. */
  @Input() abierto = false;

  /** Emite la opción elegida por el jugador al confirmar. */
  @Output() readonly accionSeleccionada = new EventEmitter<PauseMenuAction>();

  /** Emite una solicitud para cerrar el menú lateral. */
  @Output() readonly cerrar = new EventEmitter<void>();

  /** Lista de opciones disponibles en el menú lateral. */
  readonly opciones: Pausemenuoptionmodel[] = [
    { accion: 'bag', etiqueta: 'Mochila' },
    { accion: 'pokedex', etiqueta: 'Pokédex' },
    { accion: 'profile', etiqueta: 'Mi perfil' },
    { accion: 'logout', etiqueta: 'Cerrar sesión' },
  ];

  /** Índice de la opción actualmente marcada con la flecha y resaltado. */
  indiceSeleccionado = signal(0);

  /**
   * Reinicia el cursor a la primera opción cada vez que se abre el menú.
   */
  ngOnChanges(): void {
    if (this.abierto) {
      this.indiceSeleccionado.set(0);
    }
  }

  /**
   * Permite moverse con `ArrowUp` / `ArrowDown` y cerrar con `Escape` o `Enter`.
   *
   * @param e Evento de teclado a nivel de ventana.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(e: KeyboardEvent): void {
    if (!this.abierto) return;

    if (e.key === 'ArrowUp') {
      e.preventDefault();
      e.stopPropagation();
      const total = this.opciones.length;
      this.indiceSeleccionado.set((this.indiceSeleccionado() - 1 + total) % total);
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      e.stopPropagation();
      const total = this.opciones.length;
      this.indiceSeleccionado.set((this.indiceSeleccionado() + 1) % total);
    } else if ((e.key === 'Escape' || e.key === 'Enter') && !e.repeat) {
      e.preventDefault();
      e.stopPropagation();
      this.cerrar.emit();
    }
  }

  /**
   * Confirma una opción concreta (por clic o ratón) y emite su acción.
   *
   * @param indice Índice de la opción elegida.
   */
  confirmarOpcion(indice: number): void {
    this.indiceSeleccionado.set(indice);
    const opcion = this.opciones[indice];
    if (opcion) {
      this.accionSeleccionada.emit(opcion.accion);
    }
  }
}
