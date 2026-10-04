package com.prorganics.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.prorganics.mobile.data.local.dao.ProductoDao
import com.prorganics.mobile.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class CatalogViewModel(
    private val productoDao: ProductoDao
) : ViewModel() {

    private val filtro = MutableStateFlow("")

    fun obtenerProductos(usuarioId: Long): Flow<List<ProductoEntity>> {
        return combine(
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
        }
    }

    fun filtrarProductos(texto: String) {
        filtro.value = texto
    }
}