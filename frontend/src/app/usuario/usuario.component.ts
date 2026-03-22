import { Component, inject } from '@angular/core';
import { TabelaComponentComponent } from '../tabela-component/tabela-component.component';
import { Usuario, UsuarioService } from './usuario.service';
import { UsuarioDialogComponent } from './usuario-dialog/usuario-dialog.component';

@Component({
  selector: 'app-usuario',
  imports: [TabelaComponentComponent],
  templateUrl: './usuario.component.html',
  styleUrl: './usuario.component.scss',
})
export class UsuarioComponent {
  protected readonly usuarioService = inject(UsuarioService);
  protected readonly dialogComponent = UsuarioDialogComponent;

  protected displayedColumns: string[] = [
    'nome',
    'email',
    'dataCadastro',
    'telefone',
  ];
  protected displayedLabels: string[] = [
    'Usuário',
    'Email',
    'Data de Cadastro',
    'Telefone',
  ];

  protected emptyUsuario = (): Usuario => ({
    id: null,
    nome: '',
    email: '',
    dataCadastro: new Date(),
    telefone: '',
  });
}
