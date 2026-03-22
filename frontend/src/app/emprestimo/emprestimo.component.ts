import { Component, inject } from '@angular/core';
import {
  Actions,
  TabelaComponentComponent,
} from '../tabela-component/tabela-component.component';
import { Emprestimo, EmprestimoService } from './emprestimo.service';
import { EmprestimoDialogComponent } from './emprestimo-dialog/emprestimo-dialog.component';
import { of } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { EmprestimoRecomendacaoDialogComponent } from './emprestimo-recomendacao-dialog/emprestimo-recomendacao-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-emprestimo',
  imports: [TabelaComponentComponent],
  templateUrl: './emprestimo.component.html',
  styleUrl: './emprestimo.component.scss',
})
export class EmprestimoComponent {
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  protected readonly emprestimoService = inject(EmprestimoService);
  protected readonly dialogComponent = EmprestimoDialogComponent;

  protected displayedColumns: string[] = [
    'nomeUsuario',
    'tituloLivro',
    'dataEmprestimo',
    'status',
  ];
  protected displayedLabels: string[] = [
    'Usuário',
    'Título do Livro',
    'Data de Empréstimo',
    'Status',
  ];

  protected emptyEmprestimo = (): Emprestimo => ({
    id: null,
    nomeUsuario: '',
    tituloLivro: '',
    dataEmprestimo: new Date(),
    periodoVencimento: null,
    status: 'ABERTO_EM_DIA',
    livro: null,
    usuario: null,
    dataDevolucao: null,
    dataVencimento: null,
  });

  protected actions: Actions[] = [
    {
      label: 'Devolver',
      icon: 'assignment_return',
      callback: (selected: Emprestimo[]) => this.onDevolver(selected),
    },
    {
      label: 'Recomendar Empréstimo',
      icon: 'auto_stories',
      callback: (selected: Emprestimo[]) =>
        this.onEmprestarPorRecomendacao(selected),
    },
  ];

  protected onDevolver(emprestimos: Emprestimo[]) {
    return this.emprestimoService.devolver(emprestimos.map((e) => e.id!));
  }

  protected onEmprestarPorRecomendacao(emprestimos: Emprestimo[]) {
    this.dialog
      .open(EmprestimoRecomendacaoDialogComponent, {
        data: this.emptyEmprestimo(),
        width: '500px',
      })
      .afterClosed()
      .subscribe({
        next: () =>
          this.snackBar.open('Salvo com sucesso', 'Fechar', { duration: 3000 }),
        error: () =>
          this.snackBar.open('Erro ao salvar', 'Fechar', { duration: 3000 }),
      });
    return of([]);
  }
}
