import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Startcomponent } from './components/startcomponent/startcomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Signupcomponent } from './components/signupcomponent/signupcomponent';
import { Admincomponent } from './components/admincomponent/admincomponent';
import { Notfoundcomponent } from './components/notfoundcomponent/notfoundcomponent';

@NgModule({
  declarations: [
    App,
    Startcomponent,
    Logincomponent,
    Signupcomponent,
    Admincomponent,
    Notfoundcomponent,
  ],
  imports: [BrowserModule, AppRoutingModule],
  providers: [provideBrowserGlobalErrorListeners()],
  bootstrap: [App],
})
export class AppModule {}
