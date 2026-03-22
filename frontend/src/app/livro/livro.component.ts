import { Component, inject } from '@angular/core';
import { TabelaComponentComponent } from '../tabela-component/tabela-component.component';
import { Livro, LivroService } from './livro.service';
import { LivroDialogComponent } from './livro-dialog/livro-dialog.component';

@Component({
  selector: 'app-livro',
  imports: [TabelaComponentComponent],
  templateUrl: './livro.component.html',
  styleUrl: './livro.component.scss',
})
export class LivroComponent {
  protected readonly livroService = inject(LivroService);
  protected readonly dialogComponent = LivroDialogComponent;

  protected displayedColumns: string[] = [
    'titulo',
    'autor',
    'isbn',
    'dataPublicacao',
    'categoria',
  ];
  protected displayedLabels: string[] = [
    'Título',
    'Autor',
    'ISBN',
    'Data de Publicação',
    'Categoria',
  ];

  protected emptyLivro = (): Livro => ({
    id: null,
    titulo: '',
    autor: '',
    isbn: '',
    dataPublicacao: new Date(),
    categoria: [],
  });
}
