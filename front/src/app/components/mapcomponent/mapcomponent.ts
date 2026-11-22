import {
  Component,
  ElementRef,
  HostListener,
  NgZone,
  OnDestroy,
  OnInit,
  ViewChild,
  inject,
} from '@angular/core';
import { Router } from '@angular/router';

import { Mapaservice } from '../../services/mapaservice';
import { Authservice } from '../../services/authservice';
import { Playergameservice, BattleState } from '../../services/playergameservice';
import { Pokeapiservice } from '../../services/pokeapiservice';
import { Mapadatosmodel } from '../../models/mapadatosmodel';
import {
  Direccionmodel,
  Dialogomodel,
  Jugadormodel,
  Npcmodel,
} from '../../models/npcmodel';
import {
  EncounterAction,
  Wildpokemonmodel,
} from '../../models/wildpokemonmodel';
import { BagItemModel } from '../../models/bagmodel';
import { PauseMenuAction } from '../../models/pausemenuoptionmodel';

@Component({
  selector: 'app-mapcomponent',
  standalone: false,
  styleUrl: './mapcomponent.css',
  templateUrl: './mapcomponent.html',
})
export class Mapcomponent implements OnInit, OnDestroy {
  @ViewChild('lienzo', { static: true })
  lienzo!: ElementRef<HTMLCanvasElement>;

  private readonly mapaservice = inject(Mapaservice);
  private readonly authService = inject(Authservice);
  private readonly router = inject(Router);
  private readonly playergameservice = inject(Playergameservice);
  private readonly pokeapiservice = inject(Pokeapiservice);
  private readonly zone = inject(NgZone);

  private readonly ANCHO = 240;
  private readonly ALTO = 160;
  private readonly RUTA_SPRITE_JUGADOR = '/assets/sprites/normal/men.png';
  private readonly MAPA_ID = 'mapapokemon';

  private ctx!: CanvasRenderingContext2D;
  private datos!: Mapadatosmodel;
  private fondo = new Image();
  private spriteJugador = new Image();
  private spritesNpc = new Map<string, HTMLImageElement>();
  private idAnimacion = 0;
  private ultimoTiempo = 0;
  private teclas: Direccionmodel[] = [];
  private temporizador: ReturnType<typeof setInterval> | undefined;

  private readonly DURACION_PASO_NORMAL = 250;
  private readonly DURACION_PASO_CORRER = 120;

  private corriendo = false;
  private explorandoArbusto = false;
  private battleId: number | null = null;
  private capturando = false;

  pokemonSalvaje: Wildpokemonmodel | null = null;
  mochilaAbierta = false;
  menuLateralAbierto = false;
  perfilAbierto = false;
  pokedexAbierta = false;
  mensajeCombate = '';

  jugador: Jugadormodel = {
    col: 128,
    fila: 167,
    direccion: 'abajo',
    moviendo: false,
    progreso: 0,
    paso: 0,
  };

  npcs: Npcmodel[] = [];
  dialogoActivo: Dialogomodel | null = null;
  textoMostrado = '';
  private indiceLinea = 0;

  ngOnInit(): void {
    this.ctx = this.lienzo.nativeElement.getContext('2d')!;
    this.cargarSprites();

    this.mapaservice
      .cargarMapa('/assets/maps/mapapokemon.tmj')
      .subscribe(datos => {
        this.datos = datos;

        this.fondo.onload = () => {
          this.zone.runOutsideAngular(() => {
            this.idAnimacion = requestAnimationFrame(this.bucle);
          });
        };

        this.fondo.src = '/assets/maps/mapapokemonimage.png';
      });
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.idAnimacion);
    clearInterval(this.temporizador);
  }

  private cargarSprites(): void {
    this.spriteJugador.src = this.RUTA_SPRITE_JUGADOR;

    for (const npc of this.npcs) {
      const imagen = new Image();
      imagen.src = npc.sprite;
      this.spritesNpc.set(npc.id, imagen);
    }
  }

  @HostListener('window:keydown', ['$event'])
  alPresionar(evento: KeyboardEvent): void {
    const tecla = evento.key.toLowerCase();
    const direccion = this.teclaADireccion(evento.key);

    if (direccion) {
      evento.preventDefault();

      if (!this.teclas.includes(direccion)) {
        this.teclas.push(direccion);
      }

      return;
    }

    if (tecla === 'z') {
      evento.preventDefault();
      this.corriendo = true;
      return;
    }

    if (
      evento.key === 'Enter' &&
      !evento.repeat &&
      !this.perfilAbierto
    ) {
      evento.preventDefault();
      this.alternarMenuLateral();
      return;
    }

    if (tecla === 'x' && !evento.repeat) {
      evento.preventDefault();
      this.aceptarConTeclaX();
      return;
    }

    if (evento.key === ' ') {
      evento.preventDefault();
      this.interactuar();
      return;
    }

    if (
      tecla === 'e' &&
      !evento.repeat &&
      !this.pokemonSalvaje &&
      !this.dialogoActivo &&
      !this.mochilaAbierta &&
      !this.menuLateralAbierto &&
      !this.perfilAbierto
    ) {
      evento.preventDefault();
      this.abrirEncuentroPrueba();
    }
  }

  @HostListener('window:keyup', ['$event'])
  alSoltar(evento: KeyboardEvent): void {
    const tecla = evento.key.toLowerCase();

    if (tecla === 'z') {
      this.corriendo = false;
    }

    const direccion = this.teclaADireccion(evento.key);

    if (direccion) {
      this.teclas = this.teclas.filter(
        teclaActiva => teclaActiva !== direccion
      );
    }
  }

  private teclaADireccion(tecla: string): Direccionmodel | null {
    switch (tecla.toLowerCase()) {
      case 'arrowup':
        return 'arriba';
      case 'arrowdown':
        return 'abajo';
      case 'arrowleft':
        return 'izquierda';
      case 'arrowright':
        return 'derecha';
      default:
        return null;
    }
  }

  private bucle = (tiempo: number): void => {
    const delta = Math.min(tiempo - this.ultimoTiempo, 50);
    this.ultimoTiempo = tiempo;

    this.actualizar(delta);
    this.dibujar();

    this.idAnimacion = requestAnimationFrame(this.bucle);
  };

  private vector(direccion: Direccionmodel): { dc: number; df: number } {
    switch (direccion) {
      case 'arriba':
        return { dc: 0, df: -1 };
      case 'abajo':
        return { dc: 0, df: 1 };
      case 'izquierda':
        return { dc: -1, df: 0 };
      default:
        return { dc: 1, df: 0 };
    }
  }

  private npcEn(columna: number, fila: number): Npcmodel | undefined {
    return this.npcs.find(
      npc => npc.col === columna && npc.fila === fila
    );
  }

  private actualizar(delta: number): void {
    const jugador = this.jugador;

    if (
      this.dialogoActivo ||
      this.pokemonSalvaje ||
      this.mochilaAbierta ||
      this.menuLateralAbierto ||
      this.perfilAbierto ||
      this.pokedexAbierta ||
      this.explorandoArbusto
    ) {
      return;
    }

    if (jugador.moviendo) {
      const duracion = this.corriendo
        ? this.DURACION_PASO_CORRER
        : this.DURACION_PASO_NORMAL;

      jugador.progreso += delta / duracion;

      if (jugador.progreso >= 1) {
        const desplazamiento = this.vector(jugador.direccion);

        jugador.col += desplazamiento.dc;
        jugador.fila += desplazamiento.df;
        jugador.progreso = 0;
        jugador.moviendo = false;
        jugador.paso = (jugador.paso + 1) % 2;

        this.evaluarSucesosTile(jugador.col, jugador.fila);
      }

      return;
    }

    if (this.teclas.length > 0) {
      jugador.direccion = this.teclas[this.teclas.length - 1];

      const desplazamiento = this.vector(jugador.direccion);
      const columna = jugador.col + desplazamiento.dc;
      const fila = jugador.fila + desplazamiento.df;

      if (
        this.mapaservice.esCaminable(this.datos, columna, fila) &&
        !this.npcEn(columna, fila)
      ) {
        jugador.moviendo = true;
        jugador.progreso = 0;
      }
    }
  }

  abrirEncuentroPrueba(): void {
    this.teclas = [];
    this.pokemonSalvaje = {
      id: 263,
      nombre: 'Zigzagoon',
      nivel: 3,
    };
  }

  private evaluarSucesosTile(columna: number, fila: number): void {
    // Mapaservice asigna el valor 4 a las casillas de hierba.
    if (this.datos.grilla[fila]?.[columna] !== 4) {
      return;
    }

    if (this.explorandoArbusto || this.pokemonSalvaje || this.battleId) {
      return;
    }

    this.explorandoArbusto = true;
    this.teclas = [];

    const bushId = `${columna}-${fila}`;

    this.playergameservice
      .explorarArbusto(this.MAPA_ID, bushId)
      .subscribe({
        next: resultado => {
          if (!resultado.encounter || resultado.battleId == null) {
            this.explorandoArbusto = false;
            return;
          }

          this.battleId = resultado.battleId;

          this.playergameservice
            .obtenerCombate(resultado.battleId)
            .subscribe({
              next: combate => this.mostrarCombate(combate),
              error: error => {
                this.explorandoArbusto = false;
                this.battleId = null;
                this.mensajeCombate =
                  `Se creó el encuentro, pero no se pudo cargar el combate. ` +
                  `Error ${error.status || ''}`;
              },
            });
        },
        error: error => {
          this.explorandoArbusto = false;
          this.mensajeCombate =
            `No se pudo explorar el arbusto. Error ${error.status || ''}`;
        },
      });
  }

  private mostrarCombate(combate: BattleState): void {
    this.pokemonSalvaje = {
      id: combate.wildSpeciesId,
      nombre: combate.wildName,
      nivel: combate.wildLevel,
      hpActual: combate.wildCurrentHp,
      hpMaximo: combate.wildMaxHp,
    };

    this.explorandoArbusto = false;
    this.mensajeCombate = '';

    this.pokeapiservice.obtenerPokemon(combate.wildSpeciesId).subscribe({
      next: pokemonApi => {
        if (this.pokemonSalvaje?.id === combate.wildSpeciesId) {
          this.pokemonSalvaje = {
            ...this.pokemonSalvaje,
            sprite: pokemonApi.sprite,
          };
        }
      },
      error: () => {
        // Wildencountercomponent intentará usar el sprite local por ID.
      },
    });
  }

  private actualizarCombate(combate: BattleState): void {
    if (this.pokemonSalvaje) {
      this.pokemonSalvaje = {
        ...this.pokemonSalvaje,
        id: combate.wildSpeciesId,
        nombre: combate.wildName,
        nivel: combate.wildLevel,
        hpActual: combate.wildCurrentHp,
        hpMaximo: combate.wildMaxHp,
      };
    }

    if (combate.status !== 'ACTIVE') {
      const mensaje =
        combate.status === 'WON'
          ? '¡Ganaste el combate!'
          : combate.status === 'LOST'
            ? 'Tu Pokémon ya no puede continuar.'
            : combate.status === 'FLED'
              ? 'Escapaste del combate.'
              : combate.status === 'CAPTURED'
                ? '¡Pokémon capturado!'
                : 'El combate terminó.';

      this.pokemonSalvaje = null;
      this.battleId = null;
      this.mensajeCombate = mensaje;
    }
  }

  alElegirAccionEncuentro(accion: EncounterAction): void {
    if (accion === 'bag') {
      this.mochilaAbierta = true;
      return;
    }

    if (this.battleId == null) {
      this.pokemonSalvaje = null;
      return;
    }

    const idCombate = this.battleId;

    if (accion === 'run') {
      this.playergameservice.huir(idCombate).subscribe({
        next: combate => this.actualizarCombate(combate),
        error: error => {
          this.mensajeCombate =
            `No se pudo huir del combate. Error ${error.status || ''}`;
        },
      });

      return;
    }

    // La opción “Combate” realiza un ataque básico usando el backend.
    this.playergameservice.atacar(idCombate).subscribe({
      next: combate => this.actualizarCombate(combate),
      error: error => {
        this.mensajeCombate =
          `No se pudo ejecutar el ataque. Error ${error.status || ''}`;
      },
    });
  }

  alCerrarMochila(): void {
    this.mochilaAbierta = false;
  }

  alSeleccionarObjetoMochila(item: BagItemModel): void {
    const codigoObjeto = item.id.toLowerCase();

    const esPokebola =
      codigoObjeto === 'pokeball' ||
      codigoObjeto === 'pokébola' ||
      codigoObjeto === 'pokebola';

    if (!esPokebola) {
      // Los demás objetos todavía no están conectados a una acción de combate.
      this.mensajeCombate = 'Este objeto aún no tiene una acción configurada.';
      return;
    }

    if (this.battleId === null || !this.pokemonSalvaje) {
      this.mensajeCombate = 'Solo puedes usar una Pokébola durante un combate.';
      return;
    }

    if (item.cantidad <= 0) {
      this.mensajeCombate = 'No te quedan Pokébolas.';
      return;
    }

    if (this.capturando) {
      return;
    }

    this.capturando = true;
    const idCombate = this.battleId;

    this.playergameservice.capturar(idCombate).subscribe({
      next: combate => {
        this.capturando = false;

        // El backend consume una Pokébola en cada intento aceptado.
        item.cantidad = Math.max(0, item.cantidad - 1);
        this.mochilaAbierta = false;

        this.actualizarCombate(combate);

        if (combate.status === 'CAPTURED') {
          this.mensajeCombate = `¡Capturaste a ${combate.wildName}!`;
        } else if (combate.status === 'ACTIVE') {
          this.mensajeCombate =
            `¡${combate.wildName} se liberó! El Pokémon salvaje contraataca.`;
        }
      },
      error: error => {
        this.capturando = false;

        // Si el backend rechazó el intento, no se descuenta la Pokébola.
        if (error.status === 0) {
          this.mensajeCombate = 'No se pudo conectar con el servidor.';
        } else if (error.status === 401 || error.status === 403) {
          this.mensajeCombate = 'Tu sesión no está autorizada.';
        } else {
          this.mensajeCombate =
            error.error?.message ||
            `No se pudo usar la Pokébola (error ${error.status}).`;
        }
      },
    });
  }

  alCerrarMenuLateral(): void {
    this.menuLateralAbierto = false;
  }

  alSeleccionarOpcionMenu(accion: PauseMenuAction): void {
    this.menuLateralAbierto = false;

    if (accion === 'bag') {
      this.mochilaAbierta = true;
      return;
    }

    if (accion === 'pokedex') {
      this.pokedexAbierta = true;
      return;
    }

    if (accion === 'profile') {
      this.perfilAbierto = true;
      return;
    }

    if (accion === 'logout') {
      this.authService.cerrarSesion();
      void this.router.navigateByUrl('/home');
    }
  }

  private dibujar(): void {
    const contexto = this.ctx;
    const tamañoTile = this.datos.tamanoTile;
    const jugador = this.jugador;
    const desplazamiento = this.vector(jugador.direccion);

    const px =
      (jugador.col +
        (jugador.moviendo ? desplazamiento.dc * jugador.progreso : 0)) *
      tamañoTile;

    const py =
      (jugador.fila +
        (jugador.moviendo ? desplazamiento.df * jugador.progreso : 0)) *
      tamañoTile;

    const camX = Math.round(
      Math.max(
        0,
        Math.min(
          px + tamañoTile / 2 - this.ANCHO / 2,
          this.fondo.width - this.ANCHO
        )
      )
    );

    const camY = Math.round(
      Math.max(
        0,
        Math.min(
          py + tamañoTile / 2 - this.ALTO / 2,
          this.fondo.height - this.ALTO
        )
      )
    );

    contexto.imageSmoothingEnabled = false;
    contexto.clearRect(0, 0, this.ANCHO, this.ALTO);
    contexto.drawImage(
      this.fondo,
      camX,
      camY,
      this.ANCHO,
      this.ALTO,
      0,
      0,
      this.ANCHO,
      this.ALTO
    );

    const entidades = [
      ...this.npcs.map(npc => ({
        y: npc.fila * tamañoTile,
        dibujar: () => this.dibujarNpc(npc, camX, camY),
      })),
      {
        y: py,
        dibujar: () => this.dibujarJugador(px, py, camX, camY),
      },
    ].sort((a, b) => a.y - b.y);

    entidades.forEach(entidad => entidad.dibujar());
  }

  private dibujarJugador(
    px: number,
    py: number,
    camX: number,
    camY: number
  ): void {
    const imagen = this.spriteJugador;

    if (!imagen.complete || imagen.naturalWidth === 0) {
      return;
    }

    const columnas = 4;
    const filas = 4;
    const anchoSprite = imagen.naturalWidth / columnas;
    const altoSprite = imagen.naturalHeight / filas;

    const filaPorDireccion: Record<Direccionmodel, number> = {
      abajo: 0,
      izquierda: 1,
      derecha: 2,
      arriba: 3,
    };

    const fila = filaPorDireccion[this.jugador.direccion];
    const columna = this.jugador.moviendo
      ? this.jugador.paso === 0
        ? 1
        : 3
      : 0;

    const xDestino = Math.round(px - camX);
    const yDestino = Math.round(py - camY - (24 - 16));

    this.ctx.drawImage(
      imagen,
      columna * anchoSprite,
      fila * altoSprite,
      anchoSprite,
      altoSprite,
      xDestino,
      yDestino,
      28,
      24
    );
  }

  private dibujarNpc(
    npc: Npcmodel,
    camX: number,
    camY: number
  ): void {
    const imagen = this.spritesNpc.get(npc.id);

    if (imagen) {
      this.dibujarSprite(
        imagen,
        npc.direccion,
        false,
        0,
        npc.col * 16 - camX,
        npc.fila * 16 - camY
      );
    }
  }

  private dibujarSprite(
    imagen: HTMLImageElement,
    direccion: Direccionmodel,
    moviendo: boolean,
    paso: number,
    x: number,
    y: number
  ): void {
    if (!imagen.complete || imagen.naturalWidth === 0) {
      return;
    }

    let cuadro = 0;

    if (direccion === 'abajo') {
      cuadro = moviendo ? 3 + paso : 0;
    }

    if (direccion === 'arriba') {
      cuadro = moviendo ? 5 + paso : 1;
    }

    if (direccion === 'izquierda' || direccion === 'derecha') {
      cuadro = moviendo ? 7 + paso : 2;
    }

    const destinoY = y - 16;

    if (direccion === 'derecha') {
      this.ctx.save();
      this.ctx.translate(x + 16, destinoY);
      this.ctx.scale(-1, 1);
      this.ctx.drawImage(
        imagen,
        cuadro * 16,
        0,
        16,
        16,
        0,
        0,
        16,
        16
      );
      this.ctx.restore();
    } else {
      this.ctx.drawImage(
        imagen,
        cuadro * 16,
        0,
        16,
        16,
        x,
        destinoY,
        16,
        16
      );
    }
  }

  private interactuar(): void {
    if (this.dialogoActivo) {
      this.avanzarDialogo();
      return;
    }

    const jugador = this.jugador;

    if (jugador.moviendo) {
      return;
    }

    const desplazamiento = this.vector(jugador.direccion);
    const npc = this.npcEn(
      jugador.col + desplazamiento.dc,
      jugador.fila + desplazamiento.df
    );

    if (npc) {
      npc.direccion = this.opuesta(jugador.direccion);
      this.dialogoActivo = {
        nombre: npc.nombre,
        lineas: npc.dialogo,
      };
      this.indiceLinea = 0;
      this.escribir();
    }
  }

  private opuesta(direccion: Direccionmodel): Direccionmodel {
    return {
      arriba: 'abajo',
      abajo: 'arriba',
      izquierda: 'derecha',
      derecha: 'izquierda',
    }[direccion] as Direccionmodel;
  }

  private escribir(): void {
    clearInterval(this.temporizador);

    const linea = this.dialogoActivo!.lineas[this.indiceLinea];
    this.textoMostrado = '';

    let indice = 0;

    this.temporizador = setInterval(() => {
      this.textoMostrado = linea.slice(0, ++indice);

      if (indice >= linea.length) {
        clearInterval(this.temporizador);
      }
    }, 30);
  }

  private avanzarDialogo(): void {
    const dialogo = this.dialogoActivo!;
    const linea = dialogo.lineas[this.indiceLinea];

    if (this.textoMostrado.length < linea.length) {
      clearInterval(this.temporizador);
      this.textoMostrado = linea;
      return;
    }

    if (this.indiceLinea < dialogo.lineas.length - 1) {
      this.indiceLinea++;
      this.escribir();
    } else {
      this.dialogoActivo = null;
    }
  }

  private alternarMenuLateral(): void {
    if (this.mochilaAbierta) {
      return;
    }

    if (this.menuLateralAbierto) {
      this.menuLateralAbierto = false;
      return;
    }

    if (!this.pokemonSalvaje && !this.dialogoActivo) {
      this.teclas = [];
      this.menuLateralAbierto = true;
    }
  }

  private aceptarConTeclaX(): void {
    if (this.dialogoActivo) {
      this.avanzarDialogo();
      return;
    }

    if (this.perfilAbierto) {
      this.perfilAbierto = false;
      return;
    }

    if (this.mochilaAbierta) {
      const filaMochila = document.querySelector<HTMLButtonElement>(
        '.mochila-overlay .fila-mochila.fila-activa:not([disabled])'
      );
      filaMochila?.click();
      return;
    }

    if (this.pokemonSalvaje && !this.mochilaAbierta) {
      const opcionActiva = document.querySelector<HTMLButtonElement>(
        '.encuentro-overlay .opcion-btn.seleccionada:not([disabled])'
      );
      opcionActiva?.click();
      return;
    }

    if (this.pokedexAbierta) {
      const filaPokedex = document.querySelector<HTMLButtonElement>(
        '.pokedex-overlay .pokedex-fila.seleccionada:not([disabled])'
      );
      filaPokedex?.click();
      return;
    }

    if (this.menuLateralAbierto) {
      const opcionMenu = document.querySelector<HTMLButtonElement>(
        '.menu-lateral-overlay .menu-lateral-opcion.seleccionada:not([disabled])'
      );
      opcionMenu?.click();
      return;
    }

    const elementoActivo = document.activeElement as HTMLElement | null;

    if (
      elementoActivo?.tagName === 'BUTTON' &&
      !(elementoActivo as HTMLButtonElement).disabled
    ) {
      elementoActivo.click();
      return;
    }

    const botonVisible = document.querySelector<HTMLButtonElement>(
      '.mochila-overlay button:not([disabled]), .encuentro-overlay button:not([disabled])'
    );

    botonVisible?.click();
  }
}
