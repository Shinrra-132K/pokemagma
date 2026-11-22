import { Component, ElementRef, HostListener, NgZone, OnDestroy, OnInit, ViewChild, inject, } from '@angular/core';
import { Mapaservice } from '../../services/mapaservice';
import { Mapadatosmodel } from '../../models/mapadatosmodel';
import { Direccionmodel, Dialogomodel, Jugadormodel, Npcmodel, } from '../../models/npcmodel';
import { EncounterAction, Wildpokemonmodel, } from '../../models/wildpokemonmodel';
import { BagItemModel } from '../../models/bagmodel';
import { PauseMenuAction } from '../../models/pausemenuoptionmodel';
import { BattleState, Playergameservice, } from '../../services/playergameservice';

@Component({
  selector: 'app-mapcomponent',
  standalone: false,
  styleUrl: './mapcomponent.css',
  templateUrl: './mapcomponent.html',
})
export class Mapcomponent implements OnInit, OnDestroy {
  @ViewChild('lienzo', { static: true })
  lienzo!: ElementRef<HTMLCanvasElement>;

  private mapaservice = inject(Mapaservice);
  private playergameservice = inject(Playergameservice);
  private zone = inject(NgZone);
  /** Evita enviar varias solicitudes de captura al mismo tiempo. */
  private capturaEnCurso = false;

  private readonly ANCHO = 240;
  private readonly ALTO = 160;
  private readonly RUTA_SPRITE_JUGADOR =
    '/assets/sprites/normal/men.png';
  private readonly MAP_ID = 'mapapokemon';

  private ctx!: CanvasRenderingContext2D;
  private datos!: Mapadatosmodel;
  private fondo = new Image();
  private spriteJugador = new Image();
  private spritesNpc = new Map<string, HTMLImageElement>();
  private idAnimacion = 0;
  private ultimoTiempo = 0;
  private teclas: Direccionmodel[] = [];
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /** Evita iniciar otra exploración mientras responde el servidor. */
  private explorando = false;

  /** ID del combate creado por el back. */
  private battleId: number | null = null;

  /** Último estado de combate recibido desde el back. */
  private estadoCombate: BattleState | null = null;

  /** Pokémon salvaje que se muestra en Wildencountercomponent. */
  pokemonSalvaje: Wildpokemonmodel | null = null;

  private readonly DURACION_PASO_NORMAL = 250;
  private readonly DURACION_PASO_CORRER = 120;

  private corriendo = false;

  mochilaAbierta = false;
  menuLateralAbierto = false;
  perfilAbierto = false;

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
      const img = new Image();
      img.src = npc.sprite;
      this.spritesNpc.set(npc.id, img);
    }
  }

  @HostListener('window:keydown', ['$event'])
  alPresionar(e: KeyboardEvent): void {
    const tecla = e.key.toLowerCase();
    const direccion = this.teclaADireccion(e.key);

    if (direccion) {
      e.preventDefault();

      if (
        !this.explorando &&
        !this.pokemonSalvaje &&
        !this.dialogoActivo &&
        !this.mochilaAbierta &&
        !this.menuLateralAbierto &&
        !this.perfilAbierto &&
        !this.teclas.includes(direccion)
      ) {
        this.teclas.push(direccion);
      }

      return;
    }

    if (tecla === 'z') {
      e.preventDefault();
      this.corriendo = true;
      return;
    }

    if (e.key === 'Enter' && !e.repeat && !this.perfilAbierto) {
      e.preventDefault();
      this.alternarMenuLateral();
      return;
    }

    if (tecla === 'x' && !e.repeat) {
      e.preventDefault();
      this.aceptarConTeclaX();
      return;
    }

    if (e.key === ' ') {
      e.preventDefault();
      this.interactuar();
    }
  }

  @HostListener('window:keyup', ['$event'])
  alSoltar(e: KeyboardEvent): void {
    const tecla = e.key.toLowerCase();

    if (tecla === 'z') {
      this.corriendo = false;
    }

    const direccion = this.teclaADireccion(e.key);
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
    const dt = Math.min(tiempo - this.ultimoTiempo, 50);
    this.ultimoTiempo = tiempo;

    this.actualizar(dt);
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

  private npcEn(col: number, fila: number): Npcmodel | undefined {
    return this.npcs.find(npc => npc.col === col && npc.fila === fila);
  }

  private actualizar(dt: number): void {
    const jugador = this.jugador;

    if (
      this.explorando ||
      this.dialogoActivo ||
      this.pokemonSalvaje ||
      this.mochilaAbierta ||
      this.menuLateralAbierto ||
      this.perfilAbierto
    ) {
      return;
    }

    if (jugador.moviendo) {
      const duracion = this.corriendo
        ? this.DURACION_PASO_CORRER
        : this.DURACION_PASO_NORMAL;

      jugador.progreso += dt / duracion;

      if (jugador.progreso >= 1) {
        const movimiento = this.vector(jugador.direccion);

        jugador.col += movimiento.dc;
        jugador.fila += movimiento.df;
        jugador.progreso = 0;
        jugador.moviendo = false;
        jugador.paso = (jugador.paso + 1) % 2;

        this.evaluarSucesosTile(jugador.col, jugador.fila);
      }

      return;
    }

    if (this.teclas.length > 0) {
      jugador.direccion = this.teclas[this.teclas.length - 1];

      const movimiento = this.vector(jugador.direccion);
      const colDestino = jugador.col + movimiento.dc;
      const filaDestino = jugador.fila + movimiento.df;

      if (
        this.mapaservice.esCaminable(this.datos, colDestino, filaDestino) &&
        !this.npcEn(colDestino, filaDestino)
      ) {
        jugador.moviendo = true;
        jugador.progreso = 0;
      }
    }
  }

  /**
   * Consulta al back al finalizar un paso sobre una casilla de hierba.
   * Mapaservice identifica la hierba con el valor 4.
   */
  /**
   * Consulta al back al terminar un paso sobre una casilla de hierba.
   * Si ocurre un encuentro, carga el combate y el Pokémon del jugador.
   */
  private evaluarSucesosTile(col: number, fila: number): void {
    if (this.datos.grilla[fila]?.[col] !== 4) {
      return;
    }

    if (this.explorando || this.pokemonSalvaje) {
      return;
    }

    this.explorando = true;
    this.teclas = [];

    const bushId = `${col}_${fila}`;

    this.playergameservice
      .explorarArbusto(this.MAP_ID, bushId)
      .subscribe({
        next: resultado => {
          this.zone.run(() => {
            this.explorando = false;

            if (
              !resultado.encounter ||
              resultado.battleId == null ||
              resultado.speciesId == null ||
              !resultado.pokemonName
            ) {
              return;
            }

            this.battleId = resultado.battleId;
            this.pokemonSalvaje = {
              id: resultado.speciesId,
              nombre: resultado.pokemonName,
              nivel: resultado.level,
              sprite:
                `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${resultado.speciesId}.png`,
            };

          });
        },
        error: error => {
          this.zone.run(() => {
            this.explorando = false;
            console.error(
              'No se pudo consultar el encuentro salvaje:',
              error
            );
          });
        },
      });
  }

  /**
   * Ejecuta la acción seleccionada en Wildencountercomponent.
   */
  alElegirAccionEncuentro(accion: EncounterAction): void {
    if (accion === 'bag') {
      this.mochilaAbierta = true;
      return;
    }

    if (this.battleId === null) {
      console.error('No hay un ID de combate activo.');
      return;
    }

    const battleId = this.battleId;

    if (accion === 'run') {
      this.playergameservice.huir(battleId).subscribe({
        next: estado => {
          this.zone.run(() => {
            this.estadoCombate = estado;
            this.cerrarEncuentro();
          });
        },
        error: error => {
          console.error('No se pudo huir del combate:', error);
        },
      });

      return;
    }

    if (accion === 'battle') {
      this.playergameservice.atacar(battleId).subscribe({
        next: estado => {
          this.zone.run(() => {
            this.actualizarEstadoCombate(estado);
          });
        },
        error: error => {
          console.error('No se pudo ejecutar el ataque:', error);
        },
      });
    }
  }

  /**
   * Actualiza los HP mostrados mientras el combate sigue activo.
   */
  /**
   * Actualiza los puntos de vida de ambos Pokémon con la respuesta del back.
   * Cierra el encuentro si el combate ya terminó.
   */
  private actualizarEstadoCombate(estado: BattleState): void {
    this.estadoCombate = estado;

    if (estado.status !== 'ACTIVE') {
      this.cerrarEncuentro();
      return;
    }

    if (this.pokemonSalvaje) {
      this.pokemonSalvaje.hpActual = estado.wildCurrentHp;
      this.pokemonSalvaje.hpMaximo = estado.wildMaxHp;
    }

  }

  /**
   * Limpia el estado local al terminar el combate o huir.
   */
  private cerrarEncuentro(): void {
    this.mochilaAbierta = false;
    this.pokemonSalvaje = null;
    this.battleId = null;
    this.estadoCombate = null;
  }

  alCerrarMochila(): void {
    this.mochilaAbierta = false;
  }

  /**
   * Gestiona un objeto seleccionado en la mochila.
   * Si hay un encuentro activo y el objeto es una Poké Ball, solicita
   * al back que intente capturar al Pokémon salvaje.
   *
   * @param item objeto seleccionado por el jugador.
   */
  alSeleccionarObjetoMochila(item: BagItemModel): void {
    // Si no hay encuentro o no se seleccionó una Poké Ball,
    // conserva el comportamiento actual de la mochila.
    if (
      !this.pokemonSalvaje ||
      this.battleId === null ||
      item.id !== 'pokeball'
    ) {
      this.mochilaAbierta = item.cantidad > 0;
      return;
    }

    // Evita enviar varias solicitudes mientras una captura está procesándose.
    if (this.capturaEnCurso) {
      return;
    }

    this.capturaEnCurso = true;
    const battleId = this.battleId;

    this.playergameservice.capturar(battleId).subscribe({
      next: estado => {
        this.zone.run(() => {
          this.capturaEnCurso = false;
          this.mochilaAbierta = false;

          /*
           * Si el back devuelve un combate terminado, actualizarEstadoCombate()
           * cierra el encuentro. Si la captura falla y sigue ACTIVE, actualiza
           * los HP y mantiene el Pokémon salvaje en pantalla.
           */
          this.actualizarEstadoCombate(estado);
        });
      },
      error: error => {
        this.zone.run(() => {
          this.capturaEnCurso = false;
          console.error('No se pudo intentar la captura:', error);
        });
      },
    });
  }

  private dibujar(): void {
    const ctx = this.ctx;
    const tamañoTile = this.datos.tamanoTile;
    const jugador = this.jugador;
    const movimiento = this.vector(jugador.direccion);

    const px =
      (jugador.col +
        (jugador.moviendo ? movimiento.dc * jugador.progreso : 0)) *
      tamañoTile;

    const py =
      (jugador.fila +
        (jugador.moviendo ? movimiento.df * jugador.progreso : 0)) *
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

    ctx.imageSmoothingEnabled = false;
    ctx.clearRect(0, 0, this.ANCHO, this.ALTO);
    ctx.drawImage(
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
    const img = this.spriteJugador;

    if (!img.complete || img.naturalWidth === 0) {
      return;
    }

    const sw = img.naturalWidth / 4;
    const sh = img.naturalHeight / 4;

    const filaDireccion: Record<Direccionmodel, number> = {
      abajo: 0,
      izquierda: 1,
      derecha: 2,
      arriba: 3,
    };

    const fila = filaDireccion[this.jugador.direccion];
    const columna = this.jugador.moviendo
      ? this.jugador.paso === 0
        ? 1
        : 3
      : 0;

    const destW = 28;
    const destH = 24;
    const destX = Math.round(px - camX);
    const destY = Math.round(py - camY - (destH - 16));

    this.ctx.drawImage(
      img,
      columna * sw,
      fila * sh,
      sw,
      sh,
      destX,
      destY,
      destW,
      destH
    );
  }

  private dibujarNpc(
    npc: Npcmodel,
    camX: number,
    camY: number
  ): void {
    const img = this.spritesNpc.get(npc.id);

    if (img) {
      this.dibujarSprite(
        img,
        npc.direccion,
        false,
        0,
        npc.col * 16 - camX,
        npc.fila * 16 - camY
      );
    }
  }

  private dibujarSprite(
    img: HTMLImageElement,
    direccion: Direccionmodel,
    moviendo: boolean,
    paso: number,
    x: number,
    y: number
  ): void {
    if (!img.complete || img.naturalWidth === 0) {
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

    const destY = y - 16;

    if (direccion === 'derecha') {
      this.ctx.save();
      this.ctx.translate(x + 16, destY);
      this.ctx.scale(-1, 1);
      this.ctx.drawImage(img, cuadro * 16, 0, 16, 16, 0, 0, 16, 16);
      this.ctx.restore();
    } else {
      this.ctx.drawImage(
        img,
        cuadro * 16,
        0,
        16,
        16,
        x,
        destY,
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

    const movimiento = this.vector(jugador.direccion);
    const npc = this.npcEn(
      jugador.col + movimiento.dc,
      jugador.fila + movimiento.df
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
      indice++;
      this.textoMostrado = linea.slice(0, indice);

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

  alSeleccionarOpcionMenu(accion: PauseMenuAction): void {
    this.menuLateralAbierto = false;

    if (accion === 'bag') {
      this.mochilaAbierta = true;
      return;
    }

    if (accion === 'pokedex') {
      // Pendiente de conectar la pantalla de la Pokédex.
      return;
    }

    if (accion === 'profile') {
      this.perfilAbierto = true;
      return;
    }

    if (accion === 'logout') {
      // Pendiente de conectar el cierre de sesión.
    }
  }

  alCerrarMenuLateral(): void {
    this.menuLateralAbierto = false;
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
