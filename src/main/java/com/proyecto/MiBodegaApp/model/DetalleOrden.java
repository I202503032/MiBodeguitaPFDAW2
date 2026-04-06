package com.proyecto.MiBodegaApp.model;

public class DetalleOrden {
	       private Integer id;
	       private String nombre;
	       private double cantidad;
	       private double precion;
	       private double total;
	       public DetalleOrden() {
	    	   
	    	   
	       }
		   public DetalleOrden(Integer id, String nombre, double cantidad, double precion, double total) {
			super();
			this.id = id;
			this.nombre = nombre;
			this.cantidad = cantidad;
			this.precion = precion;
			this.total = total;
		   }
		   public Integer getId() {
			   return id;
		   }
		   public void setId(Integer id) {
			   this.id = id;
		   }
		   public String getNombre() {
			   return nombre;
		   }
		   public void setNombre(String nombre) {
			   this.nombre = nombre;
		   }
		   public double getCantidad() {
			   return cantidad;
		   }
		   public void setCantidad(double cantidad) {
			   this.cantidad = cantidad;
		   }
		   public double getPrecion() {
			   return precion;
		   }
		   public void setPrecion(double precion) {
			   this.precion = precion;
		   }
		   public double getTotal() {
			   return total;
		   }
		   public void setTotal(double total) {
			   this.total = total;
		   }
		   @Override
		   public String toString() {
			return "DetalleOrden [id=" + id + ", nombre=" + nombre + ", cantidad=" + cantidad + ", precion=" + precion
					+ ", total=" + total + "]";
		   }

}
