import { DatePipe } from '@angular/common';
import { ComponentType } from '@angular/cdk/portal';
import { SelectionModel } from '@angular/cdk/collections';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { CrudService, Entity } from './BasicCrudService';
import {
  Observable,
  debounceTime,
  distinctUntilChanged,
  of,
  Subject,
  switchMap,
} from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

export interface Actions {
  label: string;
  icon: string;
  callback: (selected: any[]) => Observable<any>;
}

@Component({
  selector: 'app-tabela-component',
  standalone: true,
  imports: [
    MatTableModule,
    MatIcon,
    MatMenuModule,
    MatButtonModule,
    MatCheckboxModule,
    DatePipe,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './tabela-component.component.html',
  styleUrl: './tabela-component.component.scss',
})
export class TabelaComponentComponent<T extends Entity> implements OnInit {
  @Input() displayedColumns: string[] = [];
  @Input() displayedLabels: string[] = [];
  @Input() service!: CrudService<T>;
  @Input() dialogComponent?: ComponentType<any>;
  @Input() actions: Actions[] = [];
  @Input() emptyItem?: () => T;
  @Output() selectionChange = new EventEmitter<T[]>();

  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private buscaSubject = new Subject<string>();

  protected selection = new SelectionModel<T>(true, []);
  protected displayedColumnsWithActions: string[] = [];

  protected dataSource: T[] = [];
  protected busca: string = '';
  protected loading: boolean = false;

  ngOnInit() {
    const prefix = this.actions.length > 0 ? ['select'] : [];
    this.displayedColumnsWithActions = [
      ...prefix,
      ...this.displayedColumns,
      ...['actions'],
    ];
    this.buscarRegistros();
    this.buscaSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          query.length >= 2 ? this.service.find(query) : of([]),
        ),
      )
      .subscribe((results: T[]) => {
        this.dataSource = results;
      });
  }

  protected onBuscaInput(value: string) {
    if (value.length == 0) {
      this.buscarRegistros();
      return;
    }
    this.buscaSubject.next(value);
  }

  protected onEditar(element: T): void {
    if (!this.dialogComponent) return;
    this.dialog
      .open(this.dialogComponent, {
        width: '500px',
        data: element,
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.service.update(result).subscribe({
            next: () => {
              this.buscarRegistros();
              this.snackBar.open('Editado com sucesso', 'Fechar', {
                duration: 3000,
              });
            },
            error: () =>
              this.snackBar.open('Erro ao editar', 'Fechar', {
                duration: 3000,
              }),
          });
        }
      });
  }

  protected onExcluir(element: T) {
    this.service.delete(element.id!).subscribe({
      next: () => {
        this.buscarRegistros();
        this.snackBar.open('Excluído com sucesso', 'Fechar', {
          duration: 3000,
        });
      },
      error: () =>
        this.snackBar.open('Erro ao excluir', 'Fechar', { duration: 3000 }),
    });
  }

  protected onAdicionar(): void {
    if (!this.dialogComponent || !this.emptyItem) return;
    this.dialog
      .open(this.dialogComponent, {
        width: '500px',
        data: this.emptyItem(),
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.service.save(result).subscribe({
            next: () => {
              this.buscarRegistros();
              this.snackBar.open('Adicionado com sucesso', 'Fechar', {
                duration: 3000,
              });
            },
            error: () =>
              this.snackBar.open('Erro ao adicionar', 'Fechar', {
                duration: 3000,
              }),
          });
        }
      });
  }

  protected isAllSelected(): boolean {
    return this.selection.selected.length === this.dataSource.length;
  }

  protected toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.selection.select(...this.dataSource);
    }
    this.selectionChange.emit(this.selection.selected);
  }

  protected toggleRow(row: T) {
    this.selection.toggle(row);
    this.selectionChange.emit(this.selection.selected);
  }

  protected executeAction(action: Actions) {
    action.callback(this.selection.selected).subscribe({
      next: () => {
        this.selection.clear();
        this.buscarRegistros();
      },
      error: () =>
        this.snackBar.open('Erro ao executar ação', 'Fechar', {
          duration: 3000,
        }),
    });
  }

  private buscarRegistros() {
    this.service.list().subscribe({
      next: (res) => {
        this.dataSource = res;
        this.selection.clear();
      },
      error: () =>
        this.snackBar.open('Erro ao buscar registros', 'Fechar', {
          duration: 3000,
        }),
    });
  }

  isDate(value: any): boolean {
    if (!value) return false;

    if (value instanceof Date) return true;

    if (typeof value === 'string') {
      const parsed = Date.parse(value);

      return !isNaN(parsed) && value.includes('-');
    }

    return false;
  }
}
