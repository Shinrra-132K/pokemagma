
import { Component } from '@angular/core';

@Component({
  selector: 'app-admincomponent',
  standalone: false,
  templateUrl: './admincomponent.html',
  styleUrl: './admincomponent.css'
})
export class Admincomponent {

  seccionActual = 'dashboard';
  busqueda = '';

  estadisticas = [
    {
      titulo: 'Usuarios registrados',
      valor: 1248,
      icono: '👥',
      color: 'azul',
      cambio: '+12% este mes'
    },
    {
      titulo: 'Usuarios activos',
      valor: 1156,
      icono: '🟢',
      color: 'verde',
      cambio: '+8% este mes'
    },
    {
      titulo: 'Usuarios inactivos',
      valor: 92,
      icono: '😴',
      color: 'amarillo',
      cambio: '-4% este mes'
    },
    {
      titulo: 'Administradores',
      valor: 3,
      icono: '🛡️',
      color: 'morado',
      cambio: 'Personal autorizado'
    }
  ];

  usuarios = [
    {
      nombre: 'Sarah Johnson',
      correo: 'sarah.johnson@gmail.com',
      rol: 'Jugador',
      estado: 'Activo',
      fecha: '12 Oct 2026'
    },
    {
      nombre: 'Michael Chen',
      correo: 'michael.chen@gmail.com',
      rol: 'Administrador',
      estado: 'Activo',
      fecha: '10 Oct 2026'
    },
    {
      nombre: 'Emily Davis',
      correo: 'emily.davis@gmail.com',
      rol: 'Jugador',
      estado: 'Activo',
      fecha: '08 Oct 2026'
    },
    {
      nombre: 'James Wilson',
      correo: 'james.wilson@gmail.com',
      rol: 'Moderador',
      estado: 'Inactivo',
      fecha: '05 Oct 2026'
    },
    {
      nombre: 'Lisa Rodriguez',
      correo: 'lisa.rodriguez@gmail.com',
      rol: 'Jugador',
      estado: 'Activo',
      fecha: '01 Oct 2026'
    }
  ];

  pokemones = [
    {
      numero: '#001',
      nombre: 'Bulbasaur',
      tipo: 'Planta / Veneno',
      generacion: 'Primera',
      estado: 'Disponible'
    },
    {
      numero: '#004',
      nombre: 'Charmander',
      tipo: 'Fuego',
      generacion: 'Primera',
      estado: 'Disponible'
    },
    {
      numero: '#007',
      nombre: 'Squirtle',
      tipo: 'Agua',
      generacion: 'Primera',
      estado: 'Disponible'
    },
    {
      numero: '#025',
      nombre: 'Pikachu',
      tipo: 'Eléctrico',
      generacion: 'Primera',
      estado: 'Disponible'
    },
    {
      numero: '#152',
      nombre: 'Chikorita',
      tipo: 'Planta',
      generacion: 'Segunda',
      estado: 'Disponible'
    },
    {
      numero: '#252',
      nombre: 'Treecko',
      tipo: 'Planta',
      generacion: 'Tercera',
      estado: 'Disponible'
    }
  ];

  auditorias = [
    {
      usuario: 'Sarah Johnson',
      accion: 'Inicio de sesión',
      detalle: 'Nuevo inicio de sesión registrado',
      fecha: '31 Oct 2026',
      hora: '08:30 AM',
      tipo: 'login'
    },
    {
      usuario: 'Michael Chen',
      accion: 'Actualización de perfil',
      detalle: 'Modificó su información personal',
      fecha: '31 Oct 2026',
      hora: '08:15 AM',
      tipo: 'update'
    },
    {
      usuario: 'Emily Davis',
      accion: 'Captura de Pokémon',
      detalle: 'Capturó un Pikachu',
      fecha: '30 Oct 2026',
      hora: '06:45 PM',
      tipo: 'pokemon'
    },
    {
      usuario: 'James Wilson',
      accion: 'Cierre de sesión',
      detalle: 'Cerró su sesión correctamente',
      fecha: '30 Oct 2026',
      hora: '05:20 PM',
      tipo: 'logout'
    },
    {
      usuario: 'Lisa Rodriguez',
      accion: 'Cuenta creada',
      detalle: 'Se registró un nuevo usuario',
      fecha: '30 Oct 2026',
      hora: '03:10 PM',
      tipo: 'register'
    }
  ];

  cambiarSeccion(seccion: string) {
    this.seccionActual = seccion;
    this.busqueda = '';
  }

  buscar(event: Event) {
    const elemento = event.target as HTMLInputElement;
    this.busqueda = elemento.value.toLowerCase();
  }

  get usuariosFiltrados() {
    return this.usuarios.filter(usuario =>
      usuario.nombre.toLowerCase().includes(this.busqueda) ||
      usuario.correo.toLowerCase().includes(this.busqueda)
    );
  }

  get pokemonesFiltrados() {
    return this.pokemones.filter(pokemon =>
      pokemon.nombre.toLowerCase().includes(this.busqueda) ||
      pokemon.numero.toLowerCase().includes(this.busqueda)
    );
  }

  cerrarSesion() {
    alert('Cerrando sesión de administrador...');

  }

  agregarUsuario() {
    alert('Aquí se conectará la función para agregar usuarios.');
  }

  exportarAuditorias() {
    const contenido = [
      ['Usuario', 'Acción', 'Detalle', 'Fecha', 'Hora'],
      ...this.auditorias.map(a => [
        a.usuario, a.accion, a.detalle, a.fecha, a.hora
      ])
    ];

    const csv = contenido.map(fila =>
      fila.map(valor => `"${valor}"`).join(',')
    ).join('\n');

    const archivo = new Blob(
      ['\uFEFF' + csv],
      { type: 'text/csv;charset=utf-8;' }
    );

    const enlace = document.createElement('a');
    enlace.href = URL.createObjectURL(archivo);
    enlace.download = 'auditorias-pokemon.csv';
    enlace.click();
    URL.revokeObjectURL(enlace.href);
  }
}

get totalInicios() {
  return this.auditorias.filter(a => a.tipo === 'login').length;
}

get totalActualizaciones() {
  return this.auditorias.filter(a => a.tipo === 'update').length;
}

get totalAccionesJuego() {
  return this.auditorias.filter(a => a.tipo === 'pokemon').length;
}
