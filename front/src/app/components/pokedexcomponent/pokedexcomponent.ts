import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  Output,
  signal,
} from '@angular/core';
import { Pokedexmodel } from '../../models/pokedexmodel';

/**
 * Pantalla de Pokédex estilo Game Boy Advance.
 * Permite recorrer la lista de Pokémon, ver detalles y regresar al mapa.
 */
@Component({
  selector: 'app-pokedexcomponent',
  standalone: false,
  styleUrl: './pokedexcomponent.css',
  templateUrl: './pokedexcomponent.html',
})
export class Pokedexcomponent {
  /** Controla si la Pokédex se encuentra abierta sobre el juego. */
  @Input() abierto = false;

  /** Emite la solicitud de cierre para regresar al mapa. */
  @Output() readonly cerrar = new EventEmitter<void>();

  /** Lista de entradas de prueba para la Pokédex. */
  readonly entradas: Pokedexmodel[] = [
    {
      numero: 252,
      nombre: 'Treecko',
      visto: true,
      capturado: true,
      tipo: 'Planta',
      descripcion: 'Trepa paredes con las garras de sus plantas y mide el aire con la cola.',
    },
    {
      numero: 255,
      nombre: 'Torchic',
      visto: true,
      capturado: true,
      tipo: 'Fuego',
      descripcion: 'Guarda una bolsa de llama en su interior y desprende calor al abrazarlo.',
    },
    {
      numero: 258,
      nombre: 'Mudkip',
      visto: true,
      capturado: false,
      tipo: 'Agua',
      descripcion: 'Usa su aleta caudal para sentir vibraciones en el agua y el aire.',
    },
    {
      numero: 263,
      nombre: 'Zigzagoon',
      visto: true,
      capturado: true,
      tipo: 'Normal',
      descripcion: 'Camina en zigzag con mucha curiosidad; siempre busca algo que olfatear.',
    },
    {
      numero: 265,
      nombre: 'Wurmple',
      visto: false,
      capturado: false,
      tipo: 'Bicho',
      descripcion: 'Datos aún no registrados en la Pokédex.',
    },
  ];

  /** Índice de la fila seleccionada en la lista (`0..entradas.length - 1`, o `entradas.length` para salir). */
  indiceSeleccionado = signal(0);

  /**
   * Reinicia la selección al abrir la Pokédex.
   */
  ngOnChanges(): void {
    if (this.abierto) {
      this.indiceSeleccionado.set(0);
    }
  }

  /**
   * Navega por la lista con `ArrowUp` / `ArrowDown` y cierra con `Escape`.
   *
   * @param e Evento de teclado a nivel de ventana.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(e: KeyboardEvent): void {
    if (!this.abierto) return;

    const totalFilas = this.entradas.length + 1;

    if (e.key === 'ArrowUp') {
      e.preventDefault();
      e.stopPropagation();
      this.indiceSeleccionado.set((this.indiceSeleccionado() - 1 + totalFilas) % totalFilas);
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      e.stopPropagation();
      this.indiceSeleccionado.set((this.indiceSeleccionado() + 1) % totalFilas);
    } else if (e.key === 'Escape' && !e.repeat) {
      e.preventDefault();
      e.stopPropagation();
      this.cerrar.emit();
    }
  }

  /**
   * Indica si el cursor está sobre la opción final para salir de la Pokédex.
   *
   * @returns `true` si está seleccionada la fila de salida.
   */
  esFilaSalir(): boolean {
    return this.indiceSeleccionado() >= this.entradas.length;
  }

  /**
   * Devuelve la entrada actualmente resaltada, o `undefined` si está en la fila de salir.
   *
   * @returns Entrada activa de la Pokédex.
   */
  obtenerEntradaActual(): Pokedexmodel | undefined {
    if (this.esFilaSalir()) {
      return undefined;
    }
    return this.entradas[this.indiceSeleccionado()];
  }

  /**
   * Cuenta cuántos Pokémon están marcados como capturados.
   *
   * @returns Total de capturados.
   */
  totalCapturados(): number {
    return this.entradas.filter((item) => item.capturado).length;
  }

  /**
   * Cuenta cuántos Pokémon están marcados como vistos.
   *
   * @returns Total de vistos.
   */
  totalVistos(): number {
    return this.entradas.filter((item) => item.visto || item.capturado).length;
  }

  /**
   * Activa una fila específica (por clic o ratón) y cierra si es la opción de salida.
   *
   * @param indice Índice de fila dentro de la lista.
   */
  confirmarFila(indice: number): void {
    this.indiceSeleccionado.set(indice);
    if (indice >= this.entradas.length) {
      this.cerrar.emit();
    }
  }
}
