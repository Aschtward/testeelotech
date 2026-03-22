import { Component, inject, Inject, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import {
  MatNativeDateModule,
  provideNativeDateAdapter,
} from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Usuario, UsuarioService } from '../../usuario/usuario.service';
import {
  debounceTime,
  distinctUntilChanged,
  from,
  of,
  Subject,
  switchMap,
} from 'rxjs';
import { Livro, LivroService } from '../../livro/livro.service';
import { Emprestimo } from '../emprestimo.service';

@Component({
  selector: 'app-emprestimo-dialog',
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
  templateUrl: './emprestimo-dialog.component.html',
  styleUrl: './emprestimo-dialog.component.scss',
  providers: [provideNativeDateAdapter()],
})
export class EmprestimoDialogComponent {
  @ViewChild('form') form!: NgForm;

  private readonly usuarioService = inject(UsuarioService);
  private readonly livroService = inject(LivroService);

  private buscaUsuarioSubject = new Subject<string>();
  private buscaLivrosSubject = new Subject<string>();

  protected emprestimo!: Emprestimo;
  protected usuarios: Usuario[] = [];
  protected livros: Livro[] = [];

  protected buscaUsuario: string = '';
  protected buscaLivro: string = '';

  constructor(
    private dialogRef: MatDialogRef<EmprestimoDialogComponent>,
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
    this.buscaLivrosSubject.next(value);
  }

  protected selecionarLivro(livro: Livro) {
    this.emprestimo.livro = livro!;
  }

  protected selecionarUsuario(usuario: Usuario) {
    this.emprestimo.usuario = usuario!;
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

    this.buscaLivrosSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          query.length >= 2 ? this.livroService.find(query) : of([]),
        ),
      )
      .subscribe((results: Livro[]) => {
        this.livros = results;
      });
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
