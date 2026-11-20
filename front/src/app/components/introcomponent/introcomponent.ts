import { Component, HostListener, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

export interface InicialOpcion {
  id: number;
  nombre: string;
  sprite: string;
  pokeball: string;
}

@Component({
  selector: 'app-introcomponent',
  standalone: false,
  templateUrl: './introcomponent.html',
  styleUrl: './introcomponent.css'
})
export class Introcomponent implements OnInit, OnDestroy {
  private router = inject(Router);

  lineasAntesPokedex: string[] = [
    '¡Hola! ¡Bienvenido al mundo de los Pokémon!',
    'Me llamo Abedul, aunque todos me conocen como el Profesor Abedul.',
    'Este amigo de aquí es un pokemón, el mundo está lleno de estas criaturas.',
    'Algunas personas los tienen como compañeros. Otras los entrenan para combatir.',
    'Y hay quienes simplemente viven junto a ellos, como buenos amigos.',
    'Los Pokémon están en todas partes: en la hierba alta, en las cuevas, en el mar...',
    'Yo me dedico a investigar cómo viven y cómo se relacionan con las personas.'
  ];

  lineaPokedex = 'Toma, esta es una Pokédex. Registrará automáticamente los datos de los Pokémon que veas o captures.';
  lineaPedirInicial = 'Ahora, dentro de este maletín tengo tres Pokébolas. ¡Elige al Pokémon que te acompañará!';
  lineaDespedida = '¡Muchas aventuras pokemón te están esperando! ¡Mucha suerte!';

  iniciales: InicialOpcion[] = [
    {
      id: 252,
      nombre: 'Treecko',
      sprite: '/assets/sprites/normal/252.png',
      pokeball: '/assets/images/pokeicon.png'
    },
    {
      id: 255,
      nombre: 'Torchic',
      sprite: '/assets/sprites/normal/255.png',
      pokeball: '/assets/images/pokeicon.png'
    },
    {
      id: 258,
      nombre: 'Mudkip',
      sprite: '/assets/sprites/normal/258.png',
      pokeball: '/assets/images/pokeicon.png'
    }
  ];

  /** 'previo' -> 'pokedex' -> 'seleccion' -> 'confirmado' -> 'despedida' */
  fase = signal<'previo' | 'pokedex' | 'seleccion' | 'confirmado' | 'despedida'>('previo');
  indice = signal(0);
  textoMostrado = signal('');
  escribiendo = signal(false);
  mostrarImagen = signal(true);
  mostrarPokemon = signal(true);
  mostrarMaleta = signal(true);
  saliendo = signal(false);

  inicialSeleccionado = signal<InicialOpcion | null>(null);
  pokemonActualSprite = signal('/assets/sprites/normal/001.png');

  private temporizador: ReturnType<typeof setInterval> | undefined;
  private salida: ReturnType<typeof setTimeout> | undefined;

  ngOnInit(): void {
    this.escribirTexto(this.lineasAntesPokedex[0]);
  }

  ngOnDestroy(): void {
    clearInterval(this.temporizador);
    clearTimeout(this.salida);
  }

  @HostListener('window:keydown', ['$event'])
  alPresionar(e: KeyboardEvent): void {
    if ((e.key === 'x' || e.key === ' ') && !e.repeat) {
      e.preventDefault();
      // En la fase de selección de pokébolas, el usuario elige haciendo clic en una
      if (this.fase() !== 'seleccion') {
        this.avanzar();
      }
    }
  }

  seleccionarInicial(opcion: InicialOpcion, event: MouseEvent): void {
    event.stopPropagation();
    if (this.fase() !== 'seleccion') return;

    this.inicialSeleccionado.set(opcion);
    this.pokemonActualSprite.set(opcion.sprite);
    this.mostrarPokemon.set(true);
    this.fase.set('confirmado');
    this.escribirTexto(`¡Has elegido a ${opcion.nombre}! Es un gran compañero.`);
  }

  avanzar(): void {
    if (this.saliendo()) return;

    const textoActualObjetivo = this.obtenerTextoObjetivoActual();
    if (this.escribiendo() && textoActualObjetivo !== null) {
      clearInterval(this.temporizador);
      this.textoMostrado.set(textoActualObjetivo);
      this.escribiendo.set(false);
      return;
    }

    const faseActual = this.fase();

    if (faseActual === 'previo') {
      if (this.indice() < this.lineasAntesPokedex.length - 1) {
        this.indice.update(i => i + 1);
        this.escribirTexto(this.lineasAntesPokedex[this.indice()]);
      } else {
        this.fase.set('pokedex');
        this.escribirTexto(this.lineaPokedex);
      }
      return;
    }

    if (faseActual === 'pokedex') {
      this.fase.set('seleccion');
      this.mostrarPokemon.set(false);
      this.escribirTexto(this.lineaPedirInicial);
      return;
    }

    if (faseActual === 'seleccion') {
      // Espera a que el jugador seleccione una de las 3 pokébolas
      return;
    }

    if (faseActual === 'confirmado') {
      this.fase.set('despedida');
      this.escribirTexto(this.lineaDespedida);
      return;
    }

    if (faseActual === 'despedida') {
      this.saliendo.set(true);
      this.salida = setTimeout(() => this.router.navigate(['/game']), 600);
    }
  }

  private obtenerTextoObjetivoActual(): string | null {
    switch (this.fase()) {
      case 'previo':
        return this.lineasAntesPokedex[this.indice()] ?? '';
      case 'pokedex':
        return this.lineaPokedex;
      case 'seleccion':
        return this.lineaPedirInicial;
      case 'confirmado': {
        const sel = this.inicialSeleccionado();
        return sel ? `¡Has elegido a ${sel.nombre}! Es un gran compañero.` : '';
      }
      case 'despedida':
        return this.lineaDespedida;
    }
  }

  private escribirTexto(linea: string): void {
    clearInterval(this.temporizador);
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
