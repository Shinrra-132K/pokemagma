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
  BagItemModel,
  BagPocketModel,
} from '../../models/bagmodel';

@Component({
  selector: 'app-bagcomponent',
  standalone: false,
  styleUrl: './bagcomponent.css',
  templateUrl: './bagcomponent.html',
})
export class Bagcomponent implements OnChanges {
  /** Controla si la mochila se muestra encima del juego. */
  @Input() abierta = false;

  /** Tema de color de la mochila. */
  @Input() tema: 'ruby' | 'emerald' = 'ruby';

  /** Bolsillos y objetos recibidos desde el componente del mapa. */
  @Input() bolsillos: BagPocketModel[] = [];

  /** Emite el objeto seleccionado para que el mapa gestione su uso. */
  @Output() readonly objetoSeleccionado =
    new EventEmitter<BagItemModel>();

  /** Se emite al cerrar la mochila. */
  @Output() readonly cerrar = new EventEmitter<void>();

  /** Índice del bolsillo activo. */
  indiceBolsillo = signal(0);

  /** Índice de la fila activa del bolsillo. */
  indiceFila = signal(0);

  /**
   * Reinicia la selección al abrir la mochila.
   */
  ngOnChanges(): void {
    if (this.abierta) {
      this.indiceBolsillo.set(0);
      this.indiceFila.set(0);
    }
  }

  /**
   * Gestiona la navegación por teclado dentro de la mochila.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(evento: KeyboardEvent): void {
    if (!this.abierta) {
      return;
    }

    const tecla = evento.key.toLowerCase();

    if (evento.key === 'ArrowLeft') {
      evento.preventDefault();
      evento.stopPropagation();
      this.cambiarBolsillo(-1);
    } else if (evento.key === 'ArrowRight') {
      evento.preventDefault();
      evento.stopPropagation();
      this.cambiarBolsillo(1);
    } else if (evento.key === 'ArrowUp') {
      evento.preventDefault();
      evento.stopPropagation();
      this.moverFila(-1);
    } else if (evento.key === 'ArrowDown') {
      evento.preventDefault();
      evento.stopPropagation();
      this.moverFila(1);
    } else if (tecla === 'x' && !evento.repeat) {
      evento.preventDefault();
      evento.stopPropagation();
      this.confirmarFilaActual();
    } else if (evento.key === 'Escape' && !evento.repeat) {
      evento.preventDefault();
      evento.stopPropagation();
      this.cerrar.emit();
    }
  }

  /** Devuelve el bolsillo seleccionado. */
  obtenerBolsilloActual(): BagPocketModel | undefined {
    return this.bolsillos[this.indiceBolsillo()];
  }

  /** Devuelve los objetos del bolsillo seleccionado. */
  obtenerObjetosActuales(): BagItemModel[] {
    return this.obtenerBolsilloActual()?.objetos ?? [];
  }

  /** Indica si la fila seleccionada es la opción para cerrar. */
  esFilaCerrarBag(): boolean {
    return this.indiceFila() >= this.obtenerObjetosActuales().length;
  }

  /** Devuelve la descripción del objeto o el texto de cierre. */
  obtenerDescripcionActual(): string {
    if (this.esFilaCerrarBag()) {
      return 'Return to the field.';
    }

    return (
      this.obtenerObjetosActuales()[this.indiceFila()]?.descripcion ?? ''
    );
  }

  /** Cambia de bolsillo y selecciona su primera fila. */
  cambiarBolsillo(paso: -1 | 1): void {
    const total = this.bolsillos.length;

    if (total === 0) {
      return;
    }

    this.indiceBolsillo.set(
      (this.indiceBolsillo() + paso + total) % total
    );
    this.indiceFila.set(0);
  }

  /** Mueve la selección entre objetos y la opción de cierre. */
  moverFila(paso: -1 | 1): void {
    const totalFilas = this.obtenerObjetosActuales().length + 1;

    this.indiceFila.set(
      (this.indiceFila() + paso + totalFilas) % totalFilas
    );
  }

  /** Selecciona una fila indicada desde el HTML. */
  confirmarFila(indice: number): void {
    this.indiceFila.set(indice);
    this.confirmarFilaActual();
  }

  /** Emite el objeto seleccionado o cierra la mochila. */
  confirmarFilaActual(): void {
    if (this.esFilaCerrarBag()) {
      this.cerrar.emit();
      return;
    }

    const objeto = this.obtenerObjetosActuales()[this.indiceFila()];

    if (objeto && objeto.cantidad > 0) {
      this.objetoSeleccionado.emit(objeto);
    }
  }

  /** Devuelve la imagen correspondiente al bolsillo seleccionado. */
  obtenerImagenMochila(): string {
    const bolsillo = this.obtenerBolsilloActual();

    if (!bolsillo) {
      return '/assets/images/bag_two.png';
    }

    switch (bolsillo.tipo) {
      case 'items':
        return '/assets/images/bag_two.png';
      case 'pokeballs':
        return '/assets/images/bag_three.png';
      case 'key-items':
        return '/assets/images/bag_one.png';
      default:
        return '/assets/images/bag_two.png';
    }
  }
}
