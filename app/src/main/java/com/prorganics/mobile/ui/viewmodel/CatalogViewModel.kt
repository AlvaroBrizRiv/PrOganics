package com.prorganics.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prorganics.mobile.data.local.dao.ProductoDao
import com.prorganics.mobile.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val productoDao: ProductoDao
) : ViewModel() {

    // Estado interno del filtro
    private val _filtro = MutableStateFlow("")

    // Estado expuesto para que Compose pueda observarlo
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    // Estado interno del listado de productos
    private val _productos = MutableStateFlow<List<ProductoEntity>>(emptyList())

    // Listado expuesto para que Compose pueda observarlo
    val productos: StateFlow<List<ProductoEntity>> = _productos.asStateFlow()

    // Carga los productos y aplica el filtro de búsqueda
    fun cargarProductos() {
        viewModelScope.launch {
            combine(
                productoDao.listarTodos(),
                filtro
            ) { productos, texto ->

                if (texto.isBlank()) {
                    productos
                } else {
                    productos.filter { producto ->
                        producto.nombre.contains(texto, ignoreCase = true) ||
                                producto.descripcion.contains(texto, ignoreCase = true)
                    }
                }
            }.collect { productosFiltrados ->
                _productos.value = productosFiltrados
                
                // Si el catálogo está vacío, ejecutar el script para inyectar datos de prueba globales
                if (productosFiltrados.isEmpty() && _filtro.value.isBlank()) {
                    inyectarProductosDePrueba()
                }
            }
        }
    }

    // Actualiza el filtro de búsqueda
    fun filtrarProductos(texto: String) {
        _filtro.value = texto
    }

    // Inyectar productos de prueba en la base de datos (Script de inicialización)
    fun inyectarProductosDePrueba() {
        viewModelScope.launch {
            val productosPrueba = listOf(
                ProductoEntity(nombre = "Manzanas Fuji Orgánicas (1kg)", descripcion = "Frutas", precio = 2500.0, stock = 100),
                ProductoEntity(nombre = "Plátanos Orgánicos (1kg)", descripcion = "Frutas", precio = 1500.0, stock = 100),
                ProductoEntity(nombre = "Naranjas Bio (Malla 2kg)", descripcion = "Frutas", precio = 3000.0, stock = 100),
                ProductoEntity(nombre = "Arándanos Frescos (500g)", descripcion = "Frutas", precio = 4500.0, stock = 100),
                ProductoEntity(nombre = "Frutillas Orgánicas (500g)", descripcion = "Frutas", precio = 3800.0, stock = 100),
                
                ProductoEntity(nombre = "Zanahorias Bio (1kg)", descripcion = "Verduras", precio = 1200.0, stock = 100),
                ProductoEntity(nombre = "Lechuga Romana Fresca", descripcion = "Verduras", precio = 1000.0, stock = 100),
                ProductoEntity(nombre = "Tomates Cherry (500g)", descripcion = "Verduras", precio = 1800.0, stock = 100),
                ProductoEntity(nombre = "Espinaca Orgánica (Manojo)", descripcion = "Verduras", precio = 1100.0, stock = 100),
                ProductoEntity(nombre = "Brócoli Fresco", descripcion = "Verduras", precio = 1500.0, stock = 100),
                ProductoEntity(nombre = "Papas Nativas (1kg)", descripcion = "Verduras", precio = 2000.0, stock = 100),
                
                ProductoEntity(nombre = "Miel de Abeja Pura (500g)", descripcion = "Miel", precio = 6500.0, stock = 100),
                ProductoEntity(nombre = "Miel de Ulmo (500g)", descripcion = "Miel", precio = 7800.0, stock = 100),
                ProductoEntity(nombre = "Miel con Propóleo (250g)", descripcion = "Miel", precio = 4500.0, stock = 100),
                
                ProductoEntity(nombre = "Leche Entera Orgánica (1L)", descripcion = "Lácteos", precio = 2200.0, stock = 100),
                ProductoEntity(nombre = "Queso Fresco Artesanal", descripcion = "Lácteos", precio = 4200.0, stock = 100),
                ProductoEntity(nombre = "Yogurt Natural (500g)", descripcion = "Lácteos", precio = 2800.0, stock = 100),
                ProductoEntity(nombre = "Mantequilla de Campo (250g)", descripcion = "Lácteos", precio = 3500.0, stock = 100),
                ProductoEntity(nombre = "Huevos de Gallina Libre (Docena)", descripcion = "Lácteos", precio = 4800.0, stock = 100),
                ProductoEntity(nombre = "Queso Mantecoso Bio (250g)", descripcion = "Lácteos", precio = 3900.0, stock = 100)
            )
            
            productosPrueba.forEach { producto ->
                productoDao.registrar(producto)
            }
        }
    }
}