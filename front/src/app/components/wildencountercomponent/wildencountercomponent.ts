import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  signal,
} from '@angular/core';
import {
  EncounterAction,
  Wildpokemonmodel,
} from '../../models/wildpokemonmodel';

/**
 * Muestra el encuentro salvaje, los sprites de ambos Pokémon,
 * sus barras de vida y las acciones disponibles.
 */
@Component({
  selector: 'app-wildencountercomponent',
  standalone: false,
  styleUrl: './wildencountercomponent.css',
  templateUrl: './wildencountercomponent.html',
})
export class Wildencountercomponent implements OnChanges, OnDestroy {
  /** Pokémon salvaje del combate. */
  @Input() pokemon: Wildpokemonmodel | null = null;

  /** Emite la acción seleccionada al componente del mapa. */
  @Output() readonly accionElegida =
    new EventEmitter<EncounterAction>();

  /** Texto visible en el diálogo del encuentro. */
  textoDialogo = signal('');

  /** Controla la visibilidad del menú de acciones. */
  mostrarOpciones = signal(true);

  /** Temporizador para escribir el diálogo progresivamente. */
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /** Nombre del jugador o del Pokémon que envía al combate. */
  @Input() nombreJugador = 'RED';

  /** Nivel del Pokémon enviado por el jugador (opcional). */
  @Input() nivelJugador: number | null = 5;

  /** Vida actual del jugador/Pokémon aliado en el encuentro. */
  @Input() hpActualJugador = 100;

  /** Vida máxima del jugador/Pokémon aliado en el encuentro. */
  @Input() hpMaximoJugador = 100;

  /** Acciones disponibles para el jugador. */
  readonly accionesMenu: {
    accion: EncounterAction;
    etiqueta: string;
  }[] = [
    { accion: 'battle', etiqueta: 'Combate' },
    { accion: 'bag', etiqueta: 'Mochila' },
    { accion: 'run', etiqueta: 'Huir' },
  ];

  /** Índice de la acción seleccionada en el menú. */
  indiceSeleccionado = signal(0);

  /**
   * Actualiza el mensaje cuando aparece un nuevo Pokémon salvaje.
   */
  ngOnChanges(): void {
    if (this.pokemon) {
      this.mostrarOpciones.set(true);
      this.indiceSeleccionado.set(0);
      this.escribirMensaje(
        `¡Un ${this.pokemon.nombre} salvaje apareció!`
      );
    }
  }

  /** Limpia el temporizador al destruir el componente. */
  ngOnDestroy(): void {
    clearInterval(this.temporizador);
  }

  /** Obtiene el sprite del Pokémon salvaje. */
  obtenerSpritePokemon(): string {
    if (!this.pokemon) {
      return '/assets/sprites/normal/001.png';
    }

    if (this.pokemon.sprite) {
      return this.pokemon.sprite;
    }

    return (
      'https://raw.githubusercontent.com/PokeAPI/sprites/master/' +
      `sprites/pokemon/${this.pokemon.id}.png`
    );
  }

  /** Calcula el porcentaje de vida del Pokémon salvaje. */
  obtenerPorcentajeHp(): number {
    if (!this.pokemon) return 100;
    const max = this.pokemon.hpMaximo ?? 100;
    const actual = this.pokemon.hpActual ?? max;
    if (max <= 0) return 0;
    return Math.max(0, Math.min(100, Math.round((actual / max) * 100)));
  }

  /** Determina el color de una barra según el porcentaje de HP. */
  private calcularClaseHp(
    porcentaje: number
  ): 'hp-alta' | 'hp-media' | 'hp-baja' {
    if (porcentaje > 50) {
      return 'hp-alta';
    }

    if (porcentaje > 20) {
      return 'hp-media';
    }

    return 'hp-baja';
  }

  /** Obtiene la clase de color de la barra del rival. */
  obtenerClaseHp(): 'hp-alta' | 'hp-media' | 'hp-baja' {
    return this.calcularClaseHp(this.obtenerPorcentajeHp());
  }

  /** Emite la acción seleccionada para que la gestione el mapa. */
  elegirAccion(accion: EncounterAction): void {
    this.accionElegida.emit(accion);
  }

  /**
   * Escribe un mensaje letra por letra.
   *
   * @param mensaje texto que se mostrará.
   */
  private escribirMensaje(mensaje: string): void {
    clearInterval(this.temporizador);
    this.textoDialogo.set('');

    let indice = 0;

    this.temporizador = setInterval(() => {
      indice++;
      this.textoDialogo.set(mensaje.slice(0, indice));

      if (indice >= mensaje.length) {
        clearInterval(this.temporizador);
      }
    }, 25);
  }

  /**
   * Permite navegar entre las acciones con las flechas arriba y abajo.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(event: KeyboardEvent): void {
    if (!this.pokemon || !this.mostrarOpciones()) {
      return;
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault();
      event.stopPropagation();

      const total = this.accionesMenu.length;
      this.indiceSeleccionado.set(
        (this.indiceSeleccionado() - 1 + total) % total
      );
    } else if (event.key === 'ArrowDown') {
      event.preventDefault();
      event.stopPropagation();

      const total = this.accionesMenu.length;
      this.indiceSeleccionado.set(
        (this.indiceSeleccionado() + 1) % total
      );
    }
  }

  /** Calcula el porcentaje de vida del jugador/aliado. */
  obtenerPorcentajeHpJugador(): number {
    if (this.hpMaximoJugador <= 0) return 0;
    return Math.max(
      0,
      Math.min(100, Math.round((this.hpActualJugador / this.hpMaximoJugador) * 100))
    );
  }

  /** Obtiene la clase de color para la barra de vida del jugador/aliado. */
  obtenerClaseHpJugador(): 'hp-alta' | 'hp-media' | 'hp-baja' {
    return this.calcularClaseHp(this.obtenerPorcentajeHpJugador());
  }
}
