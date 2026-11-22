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
import { Verifyemailcomponent } from './components/verifyemailcomponent/verifyemailcomponent';

import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { Mapcomponent } from './components/mapcomponent/mapcomponent';
import { Introcomponent } from './components/introcomponent/introcomponent';
import { Wildencountercomponent } from './components/wildencountercomponent/wildencountercomponent';
import { Bagcomponent } from './components/bagcomponent/bagcomponent';
import { Menucomponent } from './components/menucomponent/menucomponent';
import { Profilecomponent } from './components/profilecomponent/profilecomponent';
import { Pokedexcomponent } from './components/pokedexcomponent/pokedexcomponent';
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
    Verifyemailcomponent,
    Mapcomponent,
    Introcomponent,
    Wildencountercomponent,
    Bagcomponent,
    Menucomponent,
    Profilecomponent,
    Pokedexcomponent,
  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient()],
  bootstrap: [App],
})
export class AppModule {}
