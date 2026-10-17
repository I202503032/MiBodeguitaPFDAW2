import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, Usuario } from '../models/usuario.model';
import { Producto, ProductoRequest } from '../models/producto.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api';

  login(datos: LoginRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.baseUrl}/auth/login`, datos);
  }

  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/productos`);
  }

  crearProducto(datos: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(`${this.baseUrl}/productos`, datos);
  }

  actualizarProducto(id: number, datos: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.baseUrl}/productos/${id}`, datos);
  }

  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/productos/${id}`);
  }

  registrarMovimiento(productoId: number, tipo: string, cantidad: number, motivo: string, usuarioId: number | null): Observable<Producto> {
    return this.http.post<Producto>(`${this.baseUrl}/productos/${productoId}/movimiento`, null, {
      params: { tipo, cantidad, motivo, ...(usuarioId ? { usuarioId } : {}) }
    });
  }

  exportarExcel(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/productos/exportar`, {
      responseType: 'blob'
    });
    }
    exportarPDF(): Observable<Blob> {
      return this.http.get(`${this.baseUrl}/productos/exportar/pdf`, { responseType: 'blob' });
    }
    exportarCSV(): Observable<Blob> {
      return this.http.get(`${this.baseUrl}/productos/exportar/csv`, { responseType: 'blob' });
  }
}
