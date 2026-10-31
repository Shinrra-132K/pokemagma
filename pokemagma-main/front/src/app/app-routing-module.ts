import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Startcomponent } from './components/startcomponent/startcomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Signupcomponent } from './components/signupcomponent/signupcomponent';
import { Admincomponent } from './components/admincomponent/admincomponent';

const routes: Routes = [
  {
    path: '',
    component: Startcomponent
  },
  {
    path: 'login',
    component: Logincomponent
  },
  {
    path: 'signup',
    component: Signupcomponent
  },
  {
    path: '**',
    redirectTo: ''
  }
  {
    path: 'admin',
    component: Admincomponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
