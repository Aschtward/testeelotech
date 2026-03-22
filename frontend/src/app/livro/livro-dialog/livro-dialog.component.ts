import { Component, Inject, inject, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MatNativeDateModule,
  provideNativeDateAdapter,
} from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  of,
} from 'rxjs';
import { Livro, LivroService } from '../livro.service';

@Component({
  selector: 'app-livro-dialog',
  imports: [
    MatFormFieldModule,
    FormsModule,
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './livro-dialog.component.html',
  styleUrl: './livro-dialog.component.scss',
})
export class LivroDialogComponent {
  @ViewChild('form') form!: NgForm;

  private readonly livroService = inject(LivroService);
  private buscaSubject = new Subject<string>();

  protected livro: Livro;
  protected busca: string = '';
  protected readonly separatorKeysCodes = [ENTER, COMMA] as const;
  protected sugestoes: Livro[] = [];

  constructor(
    private dialogRef: MatDialogRef<LivroDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.livro = { ...data };
    if (!this.livro.categoria) {
      this.livro.categoria = [];
    }

    this.buscaSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          query.length >= 2
            ? this.livroService.listFromGoogleBooks(query)
            : of([]),
        ),
      )
      .subscribe((results: Livro[]) => {
        this.sugestoes = results;
      });
  }

  protected onTituloInput(value: string) {
    this.buscaSubject.next(value);
  }

  protected adicionarCategoria(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this.livro.categoria.push(value);
    }

    event.chipInput!.clear();
  }

  protected removerCategoria(categoria: string) {
    this.livro.categoria = this.livro.categoria.filter(
      (c: string) => c !== categoria,
    );
  }

  protected fechar() {
    this.dialogRef.close();
  }

  protected salvar() {
    if (this.validateLivro()) {
      this.dialogRef.close(this.livro);
    }
  }

  protected selecionarLivro(livro: Livro) {
    this.livro.titulo = livro.titulo;
    this.livro.autor = livro.autor;
    this.livro.isbn = livro.isbn;
    this.livro.dataPublicacao = livro.dataPublicacao;
    this.livro.categoria = livro.categoria?.length
      ? livro.categoria
      : this.livro.categoria;
  }

  private validateLivro(): boolean {
    this.form.form.markAllAsTouched();
    return !!(
      this.livro.titulo &&
      this.livro.autor &&
      this.livro.isbn &&
      this.livro.dataPublicacao &&
      this.livro.categoria &&
      this.livro.categoria.length > 0
    );
  }
}
