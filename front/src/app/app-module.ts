import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Startcomponent } from './components/startcomponent/startcomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Signupcomponent } from './components/signupcomponent/signupcomponent';
import { Admincomponent } from './components/admincomponent/admincomponent';
import { Notfoundcomponent } from './components/notfoundcomponent/notfoundcomponent';
import { Howtoplaycomponent } from './components/howtoplaycomponent/howtoplaycomponent';
import { Forgottenpasswordcomponent } from './components/forgottenpasswordcomponent/forgottenpasswordcomponent';
import { Changepasswordcomponent } from './components/changepasswordcomponent/changepasswordcomponent';

/**
 * @file app.module.ts
 * @description Módulo raíz de la aplicación Angular. Agrupa todos los componentes,
 * rutas y proveedores de servicios globales necesarios para el funcionamiento inicial del sistema.
 */
@NgModule({
  declarations: [
    App,
    Startcomponent,
    Logincomponent,
    Signupcomponent,
    Admincomponent,
    Notfoundcomponent,
    Howtoplaycomponent,
    Forgottenpasswordcomponent,
    Changepasswordcomponent,
  ],
  imports: [BrowserModule, AppRoutingModule],
  providers: [provideBrowserGlobalErrorListeners()],
  bootstrap: [App],
})
export class AppModule {}
