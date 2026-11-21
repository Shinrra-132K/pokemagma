import { Service } from '@angular/core';

/**
 * Servicio encargado de gestionar la música de fondo de la aplicación.
 * Controla la reproducción en bucle, evita reiniciar la misma pista si ya está sonando,
 * maneja el bloqueo de *autoplay* del navegador y aplica un *fade out* al detener.
 */
@Service()
export class Musicservice {
  /** Instancia HTML5 `Audio` utilizada para reproducir las pistas musicales. */
  private audio = new Audio();

  /** Ruta de la pista cargada o reproduciéndose actualmente. */
  private pistaActual = '';

  /** Identificador del intervalo usado para el efecto gradual de reducción de volumen (*fade out*). */
  private fundido: ReturnType<typeof setInterval> | undefined;

  /** Nivel de volumen base configurado para la música (rango de `0` a `1`). */
  private volumen = 0.3; // de 0 a 1

  /**
   * Configura la reproducción en bucle continuo para la pista de audio.
   */
  constructor() {
    this.audio.loop = true;
  }

  /**
   * Reproduce la pista indicada en `ruta`.
   * Si la misma pista ya está sonando, evita reiniciarla; si es una pista distinta,
   * actualiza el origen (`src`), restablece el volumen base y solicita la reproducción.
   *
   * @param ruta Ruta del archivo de audio a reproducir.
   * @returns Promesa que resuelve a `true` si la reproducción inició o ya estaba activa,
   * o `false` si el navegador bloqueó o rechazó el `play()`.
   */
  reproducir(ruta: string): Promise<boolean> {
    clearInterval(this.fundido);
    if (this.pistaActual === ruta && !this.audio.paused) {
      return Promise.resolve(true);
    }
    if (this.pistaActual !== ruta) {
      this.audio.src = ruta;
      this.pistaActual = ruta;
    }
    this.audio.volume = this.volumen;
    return this.audio.play().then(() => true, () => false);
  }

  /**
   * Intenta reproducir una pista de inmediato; si el navegador bloquea el inicio automático,
   * registra oyentes de un solo uso en `click` y `keydown` para reintentar tras la primera
   * interacción del usuario.
   *
   * @param ruta Ruta del archivo de audio que se desea reproducir.
   */
  reproducirCuandoSePueda(ruta: string): void {
    this.reproducir(ruta).then(sono => {
      if (sono) return;
      const reintentar = () => {
        window.removeEventListener('click', reintentar);
        window.removeEventListener('keydown', reintentar);
        if (this.pistaActual === ruta) this.reproducir(ruta);
      };
      window.addEventListener('click', reintentar);
      window.addEventListener('keydown', reintentar);
    });
  }

  /**
   * Reduce progresivamente el volumen hasta silenciarlo (*fade out*), pausa el audio
   * y restaura el volumen base para futuras reproducciones.
   */
  detener(): void {
    clearInterval(this.fundido);
    this.fundido = setInterval(() => {
      if (this.audio.volume > 0.03) {
        this.audio.volume = Math.max(0, this.audio.volume - 0.03);
      } else {
        clearInterval(this.fundido);
        this.audio.pause();
        this.audio.volume = this.volumen;
      }
    }, 60);
  }
}
