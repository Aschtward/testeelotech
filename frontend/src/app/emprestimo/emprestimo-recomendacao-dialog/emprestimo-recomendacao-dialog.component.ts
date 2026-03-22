import { Component, inject, Inject, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  of,
  Observable,
} from 'rxjs';
import { LivroService, Livro } from '../../livro/livro.service';
import { UsuarioService, Usuario } from '../../usuario/usuario.service';
import { Emprestimo } from '../emprestimo.service';

@Component({
  selector: 'app-emprestimo-recomendacao-dialog',
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
  templateUrl: './emprestimo-recomendacao-dialog.component.html',
  styleUrl: './emprestimo-recomendacao-dialog.component.scss',
})
export class EmprestimoRecomendacaoDialogComponent {
  @ViewChild('form') form!: NgForm;

  private readonly usuarioService = inject(UsuarioService);
  private readonly livroService = inject(LivroService);

  private buscaUsuarioSubject = new Subject<string>();
  private buscaLivroSubject = new Subject<string>();

  protected emprestimo!: Emprestimo;
  protected usuarios: Usuario[] = [];
  protected livros: Livro[] = [];
  private todosLivros: Livro[] = [];

  protected buscaUsuario: string = '';
  protected buscaLivro: string = '';

  constructor(
    private dialogRef: MatDialogRef<EmprestimoRecomendacaoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.emprestimo = { ...data };
    this.buscaUsuario = data.nomeUsuario ?? '';
    this.buscaLivro = data.tituloLivro ?? '';

    this.initSubjects();
  }

  protected onUsuarioInput(value: string) {
    this.buscaUsuarioSubject.next(value);
  }

  protected onLivroInput(value: string) {
    this.buscaLivroSubject.next(value);
  }

  protected selecionarLivro(livro: Livro) {
    this.emprestimo.livro = livro!;
  }

  protected selecionarUsuario(usuario: Usuario) {
    this.emprestimo.usuario = usuario!;
    if (usuario.id) {
      this.livroService
        .listFromRecomendacao(usuario.id)
        .subscribe((results: Livro[]) => {
          this.todosLivros = results;
          this.livros = results;
        });
    }
  }

  protected fechar() {
    this.dialogRef.close();
  }

  protected salvar() {
    if (this.validateEmprestimo()) {
      this.dialogRef.close(this.emprestimo);
    }
  }

  private initSubjects() {
    this.buscaUsuarioSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          query.length >= 2 ? this.usuarioService.find(query) : of([]),
        ),
      )
      .subscribe((results: Usuario[]) => {
        this.usuarios = results;
      });

    this.buscaLivroSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          query.length >= 2 ? this.filtrarLivros(query) : of(this.todosLivros),
        ),
      )
      .subscribe((results: Livro[]) => {
        this.livros = results;
      });
  }

  private filtrarLivros(query: string): Observable<Livro[]> {
    const termo = query.toLowerCase();
    return of(
      this.todosLivros.filter((livro) =>
        livro.titulo.toLowerCase().includes(termo),
      ),
    );
  }

  private validateEmprestimo(): boolean {
    this.form.form.markAllAsTouched();
    return !!(
      this.emprestimo.usuario &&
      this.emprestimo.livro &&
      this.emprestimo.dataEmprestimo
    );
  }

  get hoje() {
    return new Date(Date.now());
  }
}
