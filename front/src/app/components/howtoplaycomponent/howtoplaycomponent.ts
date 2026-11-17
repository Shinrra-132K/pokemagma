import { Component, HostListener, computed, signal } from '@angular/core';

/**
 * Pantalla "¿Cómo jugar?" de PokeMagma.
 *
 * Es un tutorial de {@link Howtoplaycomponent.total total} pasos que el usuario recorre
 * de uno en uno: registrarse, iniciar sesión, controles, exploración,
 * captura, Pokédex, batallas e inventario/menú de opciones.
 *
 * Solo se muestra un paso a la vez. El paso actual se guarda en una señal
 * ('signal') y la plantilla lo usa para activar la sección correspondiente.
 *
 * @remarks
 * El tutorial se maneja con las mismas teclas que el juego, de modo que el
 * usuario las practica mientras lee:
 * '→' o 'X': siguiente paso.
 * '←' o 'Z': paso anterior.
 */
@Component({
  selector: 'app-howtoplaycomponent',
  standalone: false,
  styleUrl: './howtoplaycomponent.css',
  templateUrl: './howtoplaycomponent.html',
})
export class Howtoplaycomponent {
  /** Número total de pasos del tutorial. */
  readonly total = 8;

  /**
   * Lista de índices de los pasos, de '0' a 'total - 1'.
   * Se usa en la plantilla para dibujar los puntos de navegación.
   */
  readonly pasos = Array.from({ length: this.total }, (_, i) => i);

  /**
   * Índice del paso que se está mostrando (empieza en '0').
   * Es una señal: al cambiar, la plantilla se actualiza sola.
   */
  paso = signal(0);

  /** 'true' cuando el usuario está en el primer paso (no puede retroceder más). */
  esPrimero = computed(() => this.paso() === 0);

  /** 'true' cuando el usuario está en el último paso (no puede avanzar más). */
  esUltimo = computed(() => this.paso() === this.total - 1);

  /**
   * Avanza al paso siguiente.
   * Si ya está en el último, se queda donde está.
   */
  siguiente() {
    this.paso.update(p => Math.min(p + 1, this.total - 1));
  }

  /**
   * Retrocede al paso anterior.
   * Si ya está en el primero, se queda donde está.
   */
  anterior() {
    this.paso.update(p => Math.max(p - 1, 0));
  }

  /**
   * Salta directamente a un paso concreto, por ejemplo al pulsar uno de los
   * puntos de navegación.
   *
   * @param i Índice del paso al que ir (de '0' a 'total - 1').
   */
  ir(i: number) {
    this.paso.set(i);
  }

  /**
   * Maneja las teclas pulsadas en cualquier parte de la ventana.
   *
   * '→' o 'X' avanzan al siguiente paso.
   * '←' o 'Z' vuelven al paso anterior.
   * Cualquier otra tecla se ignora y conserva su comportamiento normal.
   *
   * En las teclas que sí se usan se llama a 'preventDefault()' para que el
   * navegador no haga su acción habitual (por ejemplo, desplazar la página
   * con las flechas).
   *
   * @param e Evento de teclado recibido de 'window:keydown'.
   */
  @HostListener('window:keydown', ['$event'])
  teclas(e: KeyboardEvent) {
    switch (e.key.toLowerCase()) {
      case 'arrowright':
      case 'x':
        this.siguiente();
        break;
      case 'arrowleft':
      case 'z':
        this.anterior();
        break;
      default:
        return;
    }
    e.preventDefault();
  }
}
