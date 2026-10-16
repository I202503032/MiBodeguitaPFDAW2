export interface Producto {
  id: number;
  nombre: string;
  descripcion: string | null;
  imagen: string | null;
  precio: number;
  cantidad: number;
  stockMinimo: number;
  usuarioId: number | null;
}

export interface ProductoRequest {
  nombre: string;
  descripcion: string;
  imagen: string;
  precio: number;
  cantidad: number;
  stockMinimo: number;
  usuarioId: number | null;
}

export interface MovimientoInventario {
  id: number;
  tipo: 'ENTRADA' | 'SALIDA';
  cantidad: number;
  motivo: string;
  fecha: string;
}
