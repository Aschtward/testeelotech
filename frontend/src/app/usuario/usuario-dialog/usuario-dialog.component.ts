import { Component, Inject, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
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
import { Usuario } from '../usuario.service';

@Component({
  selector: 'app-usuario-dialog',
  imports: [
    MatFormFieldModule,
    FormsModule,
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './usuario-dialog.component.html',
  providers: [provideNativeDateAdapter()],
  styleUrl: './usuario-dialog.component.scss',
})
export class UsuarioDialogComponent {
  @ViewChild('form') form!: NgForm;

  protected usuario!: Usuario;

  constructor(
    private dialogRef: MatDialogRef<UsuarioDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.usuario = { ...data };
  }

  protected fechar() {
    this.dialogRef.close();
  }

  protected salvar() {
    if (this.validateUsuario()) {
      this.dialogRef.close(this.usuario);
    }
  }

  private validateUsuario(): boolean {
    this.form.form.markAllAsTouched();
    return !!(
      this.usuario.nome &&
      this.usuario.email &&
      this.usuario.telefone &&
      this.usuario.dataCadastro
    );
  }

  get hoje() {
    return new Date(Date.now());
  }
}
