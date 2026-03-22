import { Routes } from '@angular/router';
import { UsuarioComponent } from './usuario/usuario.component';
import { LivroComponent } from './livro/livro.component';
import { EmprestimoComponent } from './emprestimo/emprestimo.component';

export const routes: Routes = [
  {
    path: 'usuario',
    component: UsuarioComponent,
  },
  {
    path: 'livro',
    component: LivroComponent,
  },
  {
    path: 'emprestimo',
    component: EmprestimoComponent,
  },
];
