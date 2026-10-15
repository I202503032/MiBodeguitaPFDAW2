import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService } from './services/api.service';
import { Producto, ProductoRequest } from './models/producto.model';
import { Usuario } from './models/usuario.model';

@Component({
  selector: 'app-root', standalone: true, imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  private readonly api = inject(ApiService);
  usuario: Usuario | null = null;
  email = '';
  password = '';
  productos: Producto[] = [];
  mensaje = '';
  error = '';
  editandoId: number | null = null;
  formulario: ProductoRequest = this.formularioVacio();

  ngOnInit(): void { this.cargarProductos(); }

  iniciarSesion(): void {
    this.error = ''; this.mensaje = '';
    this.api.login({ email: this.email, password: this.password }).subscribe({
      next: usuario => { this.usuario = usuario; this.password = ''; this.mensaje = `Bienvenido/a, ${usuario.nombre}.`; this.cargarProductos(); },
      error: (e: HttpErrorResponse) => this.error = e.status === 401 ? 'Correo o contraseña incorrectos.' : 'No se pudo iniciar sesión. Verifica que el backend esté ejecutándose y revisa los datos.'
    });
  }

  cerrarSesion(): void { this.usuario = null; this.formulario = this.formularioVacio(); this.editandoId = null; this.mensaje = 'Sesión cerrada.'; }

  cargarProductos(): void {
    this.api.listarProductos().subscribe({
      next: data => { this.productos = data; },
      error: () => this.error = 'No se pudieron cargar los productos. Comprueba que Spring Boot y MySQL estén activos.'
    });
  }

  guardarProducto(): void {
    this.error = ''; this.mensaje = '';
    const datos: ProductoRequest = { ...this.formulario, usuarioId: this.usuario?.id ?? null };
    const peticion = this.editandoId === null
      ? this.api.crearProducto(datos)
      : this.api.actualizarProducto(this.editandoId, datos);
    peticion.subscribe({
      next: () => { this.mensaje = this.editandoId === null ? 'Producto registrado correctamente.' : 'Producto actualizado correctamente.'; this.limpiarFormulario(); this.cargarProductos(); },
      error: (e: HttpErrorResponse) => this.error = e.error?.mensaje ?? 'No se pudo guardar el producto. Revisa los campos e intenta nuevamente.'
    });
  }

  editar(producto: Producto): void {
    this.editandoId = producto.id;
    this.formulario = { nombre: producto.nombre, descripcion: producto.descripcion ?? '', imagen: producto.imagen ?? '', precio: producto.precio, cantidad: producto.cantidad, usuarioId: producto.usuarioId };
    this.mensaje = `Editando el producto #${producto.id}.`;
  }

  eliminar(producto: Producto): void {
    if (!confirm(`¿Eliminar el producto "${producto.nombre}"?`)) return;
    this.api.eliminarProducto(producto.id).subscribe({
      next: () => { this.mensaje = 'Producto eliminado correctamente.'; this.cargarProductos(); },
      error: () => this.error = 'No se pudo eliminar el producto.'
    });
  }

  limpiarFormulario(): void { this.formulario = this.formularioVacio(); this.editandoId = null; }
  private formularioVacio(): ProductoRequest { return { nombre: '', descripcion: '', imagen: '', precio: 0.01, cantidad: 0, usuarioId: null }; }
}
