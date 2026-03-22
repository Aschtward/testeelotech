import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { CrudService } from '../tabela-component/BasicCrudService';
import { Usuario } from '../usuario/usuario.service';
import { Livro } from '../livro/livro.service';

export interface Emprestimo {
  id: number | null;
  nomeUsuario: string;
  tituloLivro: string;
  dataEmprestimo: Date;
  dataDevolucao: Date | null;
  dataVencimento: Date | null;
  periodoVencimento: number | null;
  status: string;
  usuario: Usuario | null;
  livro: Livro | null;
}

@Injectable({
  providedIn: 'root',
})
export class EmprestimoService implements CrudService<Emprestimo> {
  private readonly API = 'http://localhost:8080/emprestimos';

  constructor(private http: HttpClient) {}

  list(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(this.API);
  }

  find(query: string): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.API}/${query}`);
  }

  save(emprestimo: Emprestimo): Observable<Emprestimo> {
    return this.http.post<Emprestimo>(this.API, emprestimo);
  }

  update(emprestimo: Emprestimo): Observable<Emprestimo> {
    return this.http.put<Emprestimo>(this.API, emprestimo);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  devolver(ids: number[]): Observable<void> {
    return this.http.patch<void>(this.API, ids);
  }
}
