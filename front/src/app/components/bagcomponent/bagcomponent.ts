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
  @Output() readonly objetoSeleccionado = new EventEmitter<BagItemModel>();

  /** Se emite al elegir `CLOSE BAG` o cerrar la mochila. */
  @Output() readonly cerrar = new EventEmitter<void>();

  /** Índice del bolsillo activo. */
  indiceBolsillo = signal(0);

  /** Índice de fila en el panel derecho (`0..objetos.length - 1` = objeto, `objetos.length` = `CLOSE BAG`). */
  indiceFila = signal(0);

  /**
   * Reinicia la selección de bolsillo y de fila cada vez que se abre la mochila.
   */
  ngOnChanges(): void {
    if (this.abierta) {
      this.indiceBolsillo.set(0);
      this.indiceFila.set(0);
    }
  }

  /**
   * Gestiona la navegación por teclado dentro de la mochila (`Flechas`, `X` y `Escape`).
   *
   * @param e Evento de teclado capturado a nivel de ventana.
   */
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

  /**
   * Devuelve el bolsillo actualmente seleccionado según `indiceBolsillo`.
   *
   * @returns El bolsillo activo o `undefined` si no hay bolsillos configurados.
   */
  obtenerBolsilloActual(): BagPocketModel | undefined {
    return this.bolsillos[this.indiceBolsillo()];
  }

  /**
   * Devuelve el bolsillo actualmente seleccionado según `indiceBolsillo`.
   *
   * @returns El bolsillo activo o `undefined` si no hay bolsillos configurados.
   */
  obtenerObjetosActuales(): BagItemModel[] {
    return this.obtenerBolsilloActual()?.objetos ?? [];
  }

  /**
   * Indica si la fila seleccionada actualmente corresponde a la opción `CLOSE BAG`.
   *
   * @returns `true` si el cursor está sobre `CLOSE BAG`, `false` en caso contrario.
   */
  esFilaCerrarBag(): boolean {
    return this.indiceFila() >= this.obtenerObjetosActuales().length;
  }

  /**
   * Obtiene la descripción del objeto seleccionado o el texto de salida si está en `CLOSE BAG`.
   *
   * @returns Texto descriptivo que se muestra en la caja inferior izquierda.
   */
  obtenerDescripcionActual(): string {
    if (this.esFilaCerrarBag()) {
      return 'Return to the field.';
    }
    return this.obtenerObjetosActuales()[this.indiceFila()]?.descripcion ?? '';
  }

  /**
   * Cambia al bolsillo anterior o siguiente de forma circular y reinicia la fila a `0`.
   *
   * @param paso Desplazamiento del índice (`-1` para anterior, `1` para siguiente).
   */
  cambiarBolsillo(paso: -1 | 1): void {
    const total = this.bolsillos.length;
    if (!total) return;
    this.indiceBolsillo.set((this.indiceBolsillo() + paso + total) % total);
    this.indiceFila.set(0);
  }

  /**
   * Desplaza la selección vertical entre los objetos del bolsillo y `CLOSE BAG`.
   *
   * @param paso Dirección del movimiento (`-1` hacia arriba, `1` hacia abajo).
   */
  moverFila(paso: -1 | 1): void {
    const totalFilas = this.obtenerObjetosActuales().length + 1;
    this.indiceFila.set((this.indiceFila() + paso + totalFilas) % totalFilas);
  }

  /**
   * Selecciona una fila concreta (por clic o ratón) y ejecuta su acción correspondiente.
   *
   * @param indice Índice de la fila dentro del panel derecho.
   */
  confirmarFila(indice: number): void {
    this.indiceFila.set(indice);
    this.confirmarFilaActual();
  }

  /**
   * Selecciona una fila concreta (por clic o ratón) y ejecuta su acción correspondiente.
   *
   * @param indice Índice de la fila dentro del panel derecho.
   */
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
