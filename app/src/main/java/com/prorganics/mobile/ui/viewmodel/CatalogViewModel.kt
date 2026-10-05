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

    // Carga los productos del usuario y aplica el filtro de búsqueda
    fun cargarProductos(usuarioId: Long) {
        viewModelScope.launch {
            combine(
                productoDao.listarPorUsuario(usuarioId),
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
                
                // Si el catálogo está vacío, ejecutar el script para inyectar datos de prueba
                if (productosFiltrados.isEmpty() && _filtro.value.isBlank()) {
                    inyectarProductosDePrueba(usuarioId)
                }
            }
        }
    }

    // Actualiza el filtro de búsqueda
    fun filtrarProductos(texto: String) {
        _filtro.value = texto
    }

    // Inyectar productos de prueba en la base de datos (Script de inicialización)
    fun inyectarProductosDePrueba(usuarioId: Long) {
        viewModelScope.launch {
            val productosPrueba = listOf(
                ProductoEntity(usuarioId = usuarioId, nombre = "Manzanas Fuji Orgánicas", descripcion = "Frutas", precio = 3.50, stock = 100),
                ProductoEntity(usuarioId = usuarioId, nombre = "Zanahorias Bio", descripcion = "Verduras", precio = 1.20, stock = 100),
                ProductoEntity(usuarioId = usuarioId, nombre = "Miel de Abeja Pura", descripcion = "Miel", precio = 8.00, stock = 100),
                ProductoEntity(usuarioId = usuarioId, nombre = "Lechuga Romana Fresca", descripcion = "Verduras", precio = 1.80, stock = 100),
                ProductoEntity(usuarioId = usuarioId, nombre = "Tomates Cherry", descripcion = "Verduras", precio = 2.90, stock = 100),
                ProductoEntity(usuarioId = usuarioId, nombre = "Plátanos Orgánicos", descripcion = "Frutas", precio = 2.10, stock = 100)
            )
            
            productosPrueba.forEach { producto ->
                productoDao.registrar(producto)
            }
        }
    }
}