import { Component, HostListener, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-introcomponent',
  standalone: false,
  templateUrl: './introcomponent.html',
  styleUrl: './introcomponent.css'
})
export class Introcomponent implements OnInit, OnDestroy {
  private router = inject(Router);

  lineas: string[] = [
    '¡Hola! ¡Bienvenido al mundo de los Pokémon!',
    'Me llamo Abedul, aunque todos me conocen como el Profesor Abedul.',
    'Este amigo de aquí es un pokemón, el mundo está lleno de estas criaturas.',
    'Algunas personas los tienen como compañeros. Otras los entrenan para combatir.',
    'Y hay quienes simplemente viven junto a ellos, como buenos amigos.',
    'Los Pokémon están en todas partes: en la hierba alta, en las cuevas, en el mar...',
    'Yo me dedico a investigar cómo viven y cómo se relacionan con las personas.',
    '¡Muchas aventuras pokemón te están esperando! ¡Mucha suerte!'
  ];

  indice = signal(0);
  textoMostrado = signal('');
  escribiendo = signal(false);
  mostrarImagen = signal(true);
  private temporizador: ReturnType<typeof setInterval> | undefined;
  saliendo = signal(false);
  private salida: ReturnType<typeof setTimeout> | undefined;
  mostrarPokemon = signal(true);

  ngOnInit(): void {
    this.escribirLinea();
  }

  ngOnDestroy(): void {
    clearInterval(this.temporizador);
    clearTimeout(this.salida);
  }

  @HostListener('window:keydown', ['$event'])
  alPresionar(e: KeyboardEvent): void {
    if ((e.key === 'x' || e.key === ' ') && !e.repeat) {
      e.preventDefault();
      this.avanzar();
    }
  }

  avanzar(): void {
    if (this.saliendo()) return;

    if (this.escribiendo()) {
      clearInterval(this.temporizador);
      this.textoMostrado.set(this.lineas[this.indice()]);
      this.escribiendo.set(false);
      return;
    }
    if (this.indice() < this.lineas.length - 1) {
      this.indice.update(i => i + 1);
      this.escribirLinea();
    } else {
      this.saliendo.set(true);
      this.salida = setTimeout(() => this.router.navigate(['/game']), 600);
    }
  }

  private escribirLinea(): void {
    clearInterval(this.temporizador);
    const linea = this.lineas[this.indice()];
    this.textoMostrado.set('');
    this.escribiendo.set(true);
    let i = 0;
    this.temporizador = setInterval(() => {
      i++;
      this.textoMostrado.set(linea.slice(0, i));
      if (i >= linea.length) {
        clearInterval(this.temporizador);
        this.escribiendo.set(false);
      }
    }, 25);
  }
}
