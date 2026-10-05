package com.prorganics.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prorganics.mobile.data.local.dao.ProductoDao
import com.prorganics.mobile.data.local.dao.UsuarioDao

class ViewModelFactory(
    private val usuarioDao: UsuarioDao,
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(usuarioDao) as T

            modelClass.isAssignableFrom(CatalogViewModel::class.java) ->
                CatalogViewModel(productoDao) as T

            else ->
                throw IllegalArgumentException(
                    "ViewModel desconocido: ${modelClass.name}"
                )
        }
    }
}