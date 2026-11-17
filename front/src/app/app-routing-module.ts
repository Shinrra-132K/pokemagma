import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Startcomponent } from './components/startcomponent/startcomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Signupcomponent } from './components/signupcomponent/signupcomponent';
import { Admincomponent} from './components/admincomponent/admincomponent';
import { Notfoundcomponent} from './components/notfoundcomponent/notfoundcomponent';
import { Howtoplaycomponent } from'./components/howtoplaycomponent/howtoplaycomponent';

const routes: Routes = [
  {
    path: '',
    component: Startcomponent
  },
  {
    path: 'home',
    component: Startcomponent
  },
  {
    path: 'login',
    component: Logincomponent
  },
  {
    path: 'howtoplay',
    component: Howtoplaycomponent
  },
  {
    path: 'signup',
    component: Signupcomponent
  },
  {
    path: 'admin',
    component: Admincomponent
  },
  {
    path: 'notfound',
    component: Notfoundcomponent
  },
  {
    path: '**',
    redirectTo: 'notfound'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
