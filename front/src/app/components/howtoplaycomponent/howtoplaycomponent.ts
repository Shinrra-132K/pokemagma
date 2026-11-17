import { Component, HostListener, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';


const TECLAS_JUEGO = ['arrowup', 'arrowdown', 'arrowleft', 'arrowright', 'x', 'z', 'c', 'enter'];
@Component({
  selector: 'app-howtoplaycomponent',
  standalone: false,
  styleUrl: './howtoplaycomponent.css',
  templateUrl: './howtoplaycomponent.html',
})
export class Howtoplaycomponent {
  readonly total = 8;
  readonly pasos = Array.from({ length: this.total }, (_, i) => i);

  paso = signal(0);
  esPrimero = computed(() => this.paso() === 0);
  esUltimo = computed(() => this.paso() === this.total - 1);

  siguiente() {
    this.paso.update(p => Math.min(p + 1, this.total - 1));
  }

  anterior() {
    this.paso.update(p => Math.max(p - 1, 0));
  }

  ir(i: number) {
    this.paso.set(i);
  }

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
