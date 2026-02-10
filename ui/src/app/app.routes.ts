import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { PostCreateComponent } from './features/post/post-create/post-create.component';
import { HomeComponent } from './features/home/home.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: '', component: HomeComponent, canActivate: [authGuard] },
    { path: 'posts/new', component: PostCreateComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: '' }
];
