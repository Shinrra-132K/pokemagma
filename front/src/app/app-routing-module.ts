import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Startcomponent } from './components/startcomponent/startcomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Signupcomponent } from './components/signupcomponent/signupcomponent';
import { Admincomponent } from './components/admincomponent/admincomponent';
import { Notfoundcomponent } from './components/notfoundcomponent/notfoundcomponent';
import { Howtoplaycomponent } from './components/howtoplaycomponent/howtoplaycomponent';
import { Forgottenpasswordcomponent } from './components/forgottenpasswordcomponent/forgottenpasswordcomponent';
import { Changepasswordcomponent } from './components/changepasswordcomponent/changepasswordcomponent';

import { Verifyemailcomponent } from './components/verifyemailcomponent/verifyemailcomponent';
import { Mapcomponent } from './components/mapcomponent/mapcomponent';
import { Introcomponent } from './components/introcomponent/introcomponent';
/**
 * Tabla de rutas principales de la aplicación.
 */
const routes: Routes = [
  /*'' y 'home': Redirigen a la vista de inicio ('Startcomponent').*/
  {
    path: '',
    component: Startcomponent,
  },
  {
    path: 'change',
    component: Changepasswordcomponent,
  },
  {
    path: 'home',
    component: Startcomponent,
  },
  /*'login': Vista de inicio de sesión de usuarios.*/
  {
    path: 'login',
    component: Logincomponent,
  },
  /*'howtoplay': Vista con las instrucciones o reglas del juego.*/
  {
    path: 'howtoplay',
    component: Howtoplaycomponent,
  },
  /*'signup': Vista de registro de nuevos usuarios.*/
  {
    path: 'signup',
    component: Signupcomponent,
  },
  {
    path: 'verifyemail',
    component: Verifyemailcomponent,
  },
  /*'admin': Panel de administración del sistema.*/
  {
    path: 'admin',
    component: Admincomponent,
  },
  {
    path: 'intro',
    component: Introcomponent
  },
  /*'forgottenpassword': Vista para enviar correo electrónico y poder reestablecer la contraseña */
  {
    path: 'forgottenpassword',
    component: Forgottenpasswordcomponent,
  },
  /*'notfound': Vista de error para páginas no encontradas.*/
  {
    path: 'notfound',
    component: Notfoundcomponent,
  },
  {
    path: 'game',
    component: Mapcomponent,
  },
  /*'**': Redirige automáticamente cualquier ruta no encontrada a 'notfound'.*/
  {
    path: '**',
    redirectTo: 'notfound',
  },
];

/**
 * @file app-routing-module.ts
 * @description Módulo de enrutamiento principal. Configura las rutas de navegación
 * de la aplicación utilizando 'RouterModule.forRoot(routes)'.
 */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
