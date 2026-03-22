import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CrudService } from '../tabela-component/BasicCrudService';

export interface Usuario {
  id: number | null;
  nome: string;
  email: string;
  dataCadastro: Date;
  telefone: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService implements CrudService<Usuario> {

  private readonly API = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient) {}

  list(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.API);
  }

  find(nome: string): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.API}/${nome}`);
  }

  save(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.API, usuario);
  }

  update(usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(this.API, usuario);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
