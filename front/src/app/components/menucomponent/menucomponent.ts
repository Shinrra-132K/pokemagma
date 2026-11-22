import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  Output,
  signal,
} from '@angular/core';
import {
  PauseMenuAction,
  Pausemenuoptionmodel,
} from '../../models/pausemenuoptionmodel';

@Component({
  selector: 'app-menucomponent',
  standalone: false,
  styleUrl: './menucomponent.css',
  templateUrl: './menucomponent.html',
})
export class Menucomponent implements OnChanges {
  @Input() abierto = false;

  @Output() readonly accionSeleccionada =
    new EventEmitter<PauseMenuAction>();

  @Output() readonly cerrar = new EventEmitter<void>();

  readonly opciones: Pausemenuoptionmodel[] = [
    { accion: 'bag', etiqueta: 'Mochila' },
    { accion: 'pokedex', etiqueta: 'Pokédex' },
    { accion: 'profile', etiqueta: 'Mi perfil' },
    { accion: 'logout', etiqueta: 'Cerrar sesión' },
  ];

  indiceSeleccionado = signal(0);

  ngOnChanges(): void {
    if (this.abierto) {
      this.indiceSeleccionado.set(0);
    }
  }

  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(evento: KeyboardEvent): void {
    if (!this.abierto) {
      return;
    }

    if (evento.key === 'ArrowUp') {
      evento.preventDefault();
      evento.stopPropagation();

      const total = this.opciones.length;
      this.indiceSeleccionado.set(
        (this.indiceSeleccionado() - 1 + total) % total
      );
    } else if (evento.key === 'ArrowDown') {
      evento.preventDefault();
      evento.stopPropagation();

      const total = this.opciones.length;
      this.indiceSeleccionado.set(
        (this.indiceSeleccionado() + 1) % total
      );
    } else if (evento.key === 'Escape' && !evento.repeat) {
      evento.preventDefault();
      evento.stopPropagation();
      this.cerrar.emit();
    } else if (evento.key === 'Enter' && !evento.repeat) {
      evento.preventDefault();
      evento.stopPropagation();
      this.confirmarOpcion(this.indiceSeleccionado());
    }
  }

  confirmarOpcion(indice: number): void {
    this.indiceSeleccionado.set(indice);

    const opcion = this.opciones[indice];

    if (opcion) {
      this.accionSeleccionada.emit(opcion.accion);
    }
  }
}
