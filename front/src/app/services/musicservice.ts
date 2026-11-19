import { Service } from '@angular/core';

@Service()
export class Musicservice {
  private audio = new Audio();
  private pistaActual = '';
  private fundido: ReturnType<typeof setInterval> | undefined;
  private volumen = 0.3; // de 0 a 1

  constructor() {
    this.audio.loop = true;
  }

  /** Reproduce una pista. Si ya está sonando esa misma, no la reinicia. */
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

  /** Lo intenta ya; si el navegador lo bloquea, espera al primer clic o tecla. */
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

  /** Baja el volumen poco a poco y pausa. */
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
