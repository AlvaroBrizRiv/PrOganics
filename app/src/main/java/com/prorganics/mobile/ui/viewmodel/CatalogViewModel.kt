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
            }
        }
    }

    // Actualiza el filtro de búsqueda
    fun filtrarProductos(texto: String) {
        _filtro.value = texto
    }
}