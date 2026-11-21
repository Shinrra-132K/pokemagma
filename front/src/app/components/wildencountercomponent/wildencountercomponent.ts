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
import { EncounterAction, Wildpokemonmodel } from '../../models/wildpokemonmodel';

/**
 * Componente visual de encuentro con un Pokémon salvaje en la hierba alta.
 * Muestra la escena retro, el mensaje inicial de aparición y el panel de opciones
 * (`Combate`, `Mochila`, `Huir`) sin lógica de batalla aún.
 */
@Component({
  selector: 'app-wildencountercomponent',
  standalone: false,
  styleUrl: './wildencountercomponent.css',
  templateUrl: './wildencountercomponent.html',
})
export class Wildencountercomponent {
  /** Pokémon salvaje encontrado en la hierba alta. */
  @Input() pokemon: Wildpokemonmodel | null = null;

  /** Emite la acción elegida por el jugador en el menú del encuentro. */
  @Output() accionElegida = new EventEmitter<EncounterAction>();

  /** Texto del cuadro de diálogo inferior. */
  textoDialogo = signal('');

  /** Controla si el menú de opciones (`Combate`, `Mochila`, `Huir`) está visible. */
  mostrarOpciones = signal(true);

  /** Temporizador para el efecto de escritura del mensaje de aparición. */
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /**
   * Actualiza el mensaje de encuentro cada vez que cambia el Pokémon salvaje recibido.
   */
  ngOnChanges(): void {
    if (this.pokemon) {
      this.mostrarOpciones.set(true);
      this.indiceSeleccionado.set(0);
      this.escribirMensaje(`¡Un ${this.pokemon.nombre} salvaje apareció!`);
    }
  }

  /**
   * Limpia el temporizador de escritura al destruir el componente.
   */
  ngOnDestroy(): void {
    clearInterval(this.temporizador);
  }

  /**
   * Obtiene la ruta del sprite del Pokémon salvaje.
   *
   * @returns Ruta del archivo de imagen del sprite.
   */
  obtenerSpritePokemon(): string {
    if (!this.pokemon) return '/assets/sprites/normal/001.png';
    if (this.pokemon.sprite) return this.pokemon.sprite;
    const numero = String(this.pokemon.id).padStart(3, '0');
    return `/assets/sprites/normal/${numero}.png`;
  }

  /**
   * Calcula el porcentaje de vida actual (`0` a `100`) del Pokémon salvaje.
   *
   * @returns Porcentaje de HP restante.
   */
  obtenerPorcentajeHp(): number {
    if (!this.pokemon) return 100;
    const max = this.pokemon.hpMaximo ?? 100;
    const actual = this.pokemon.hpActual ?? max;
    if (max <= 0) return 0;
    return Math.max(0, Math.min(100, Math.round((actual / max) * 100)));
  }

  /**
   * Devuelve la clase de color de la barra de HP según el porcentaje restante.
   *
   * @returns Clase CSS correspondiente al nivel de vida.
   */
  obtenerClaseHp(): 'hp-alta' | 'hp-media' | 'hp-baja' {
    const pct = this.obtenerPorcentajeHp();
    if (pct > 50) return 'hp-alta';
    if (pct > 20) return 'hp-media';
    return 'hp-baja';
  }

  /**
   * Notifica al componente padre la opción seleccionada (`battle`, `bag` o `run`).
   *
   * @param accion Opción elegida por el jugador.
   */
  elegirAccion(accion: EncounterAction): void {
    this.accionElegida.emit(accion);
  }

  /**
   * Escribe progresivamente el texto de aparición en el cuadro inferior.
   *
   * @param mensaje Frase a mostrar.
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
  /** Lista ordenada de acciones visibles en el menú inferior derecho. */
  readonly accionesMenu: { accion: EncounterAction; etiqueta: string }[] = [
    { accion: 'battle', etiqueta: 'Combate' },
    { accion: 'bag', etiqueta: 'Mochila' },
    { accion: 'run', etiqueta: 'Huir' },
  ];

  /** Índice de la opción actualmente marcada con la flecha roja y resaltado. */
  indiceSeleccionado = signal(0);

  /**
   * Permite navegar entre Combate, Mochila y Huir usando las flechas Arriba y Abajo.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(e: KeyboardEvent): void {
    if (!this.pokemon || !this.mostrarOpciones()) return;

    if (e.key === 'ArrowUp') {
      e.preventDefault();
      e.stopPropagation();
      const total = this.accionesMenu.length;
      this.indiceSeleccionado.set((this.indiceSeleccionado() - 1 + total) % total);
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      e.stopPropagation();
      const total = this.accionesMenu.length;
      this.indiceSeleccionado.set((this.indiceSeleccionado() + 1) % total);
    }
  }

}

