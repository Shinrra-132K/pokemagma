import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  Output,
  signal,
} from '@angular/core';
import { BagItemModel, BagPocketModel } from '../../models/bagmodel';

@Component({
  selector: 'app-bagcomponent',
  standalone: false,
  styleUrl: './bagcomponent.css',
  templateUrl: './bagcomponent.html',
})
export class Bagcomponent implements OnChanges {
  /** Controla si la mochila se muestra encima del juego. */
  @Input() abierta = false;

  /** Tema de color estilo GBA (`'ruby'` azul/rojo o `'emerald'` morado/rosa/verde). */
  @Input() tema: 'ruby' | 'emerald' = 'ruby';

  /** Bolsillos disponibles de la mochila. */
  @Input() bolsillos: BagPocketModel[] = [
    {
      tipo: 'items',
      titulo: 'Items',
      indiceSpriteMochila: 0,
      objetos: [
        {
          id: 'potion',
          nombre: 'Potion',
          cantidad: 3,
          descripcion: 'Restores 20 HP of a Pokémon.',
        },
        {
          id: 'antidote',
          nombre: 'Antidote',
          cantidad: 2,
          descripcion: 'Heals a poisoned Pokémon.',
        },
      ],
    },
    {
      tipo: 'pokeballs',
      titulo: 'Poké Balls',
      indiceSpriteMochila: 1,
      objetos: [
        {
          id: 'pokeball',
          nombre: 'Poké Ball',
          cantidad: 5,
          descripcion: 'A device for catching wild Pokémon.',
        },
      ],
    },
    {
      tipo: 'key-items',
      titulo: 'Key Items',
      indiceSpriteMochila: 4,
      objetos: [
        {
          id: 'running-shoes',
          nombre: 'Running Shoes',
          cantidad: 1,
          descripcion: 'Shoes that allow you to run quickly.',
        },
      ],
    },
  ];

  /** Se emite al seleccionar un objeto de la lista. */
  @Output() objetoSeleccionado = new EventEmitter<BagItemModel>();

  /** Se emite al elegir `CLOSE BAG` o cerrar la mochila. */
  @Output() cerrar = new EventEmitter<void>();

  /** Índice del bolsillo activo. */
  indiceBolsillo = signal(0);

  /** Índice de fila en el panel derecho (`0..objetos.length - 1` = objeto, `objetos.length` = `CLOSE BAG`). */
  indiceFila = signal(0);

  ngOnChanges(): void {
    if (this.abierta) {
      this.indiceBolsillo.set(0);
      this.indiceFila.set(0);
    }
  }

  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(e: KeyboardEvent): void {
    if (!this.abierta) return;

    const tecla = e.key.toLowerCase();

    if (e.key === 'ArrowLeft') {
      e.preventDefault();
      e.stopPropagation();
      this.cambiarBolsillo(-1);
    } else if (e.key === 'ArrowRight') {
      e.preventDefault();
      e.stopPropagation();
      this.cambiarBolsillo(1);
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      e.stopPropagation();
      this.moverFila(-1);
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      e.stopPropagation();
      this.moverFila(1);
    } else if (tecla === 'x' && !e.repeat) {
      e.preventDefault();
      e.stopPropagation();
      this.confirmarFilaActual();
    } else if (e.key === 'Escape' && !e.repeat) {
      e.preventDefault();
      e.stopPropagation();
      this.cerrar.emit();
    }
  }

  obtenerBolsilloActual(): BagPocketModel | undefined {
    return this.bolsillos[this.indiceBolsillo()];
  }

  obtenerObjetosActuales(): BagItemModel[] {
    return this.obtenerBolsilloActual()?.objetos ?? [];
  }

  esFilaCerrarBag(): boolean {
    return this.indiceFila() >= this.obtenerObjetosActuales().length;
  }

  obtenerDescripcionActual(): string {
    if (this.esFilaCerrarBag()) {
      return 'Return to the field.';
    }
    return this.obtenerObjetosActuales()[this.indiceFila()]?.descripcion ?? '';
  }

  cambiarBolsillo(paso: -1 | 1): void {
    const total = this.bolsillos.length;
    if (!total) return;
    this.indiceBolsillo.set((this.indiceBolsillo() + paso + total) % total);
    this.indiceFila.set(0);
  }

  moverFila(paso: -1 | 1): void {
    const totalFilas = this.obtenerObjetosActuales().length + 1;
    this.indiceFila.set((this.indiceFila() + paso + totalFilas) % totalFilas);
  }

  confirmarFila(indice: number): void {
    this.indiceFila.set(indice);
    this.confirmarFilaActual();
  }

  confirmarFilaActual(): void {
    if (this.esFilaCerrarBag()) {
      this.cerrar.emit();
      return;
    }
    const item = this.obtenerObjetosActuales()[this.indiceFila()];
    if (item) {
      this.objetoSeleccionado.emit(item);
    }
  }

  /**
   * Devuelve la ruta de la imagen de la mochila según el bolsillo activo.
   */
  obtenerImagenMochila(): string {
    const bolsillo = this.obtenerBolsilloActual();
    if (!bolsillo) return '/assets/bag/bag_one.png';

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
