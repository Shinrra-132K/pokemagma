import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';

/**
 * Pantalla de perfil del entrenador estilo Trainer Card de Game Boy Advance.
 * Muestra los datos del entrenador y permite regresar al mapa con `X`, `Escape` o clic.
 */
@Component({
  selector: 'app-profilecomponent',
  standalone: false,
  styleUrl: './profilecomponent.css',
  templateUrl: './profilecomponent.html',
})
export class Profilecomponent {
  /** Controla si la tarjeta de perfil se muestra encima del juego. */
  @Input() abierto = false;

  /** Emite la solicitud de cierre para regresar al mapa. */
  @Output() readonly cerrar = new EventEmitter<void>();

  /**
   * Cierra la tarjeta de perfil al presionar `X` o `Escape`.
   *
   * @param e Evento de teclado capturado a nivel de ventana.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(e: KeyboardEvent): void {
    if (!this.abierto) return;

    const tecla = e.key.toLowerCase();
    if ((tecla === 'x' || e.key === 'Escape') && !e.repeat) {
      e.preventDefault();
      e.stopPropagation();
      this.cerrar.emit();
    }
  }
}
