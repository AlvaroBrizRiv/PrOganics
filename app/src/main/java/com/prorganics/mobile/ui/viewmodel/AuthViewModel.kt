package com.prorganics.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prorganics.mobile.data.local.dao.UsuarioDao
import com.prorganics.mobile.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    fun iniciarSesion(email: String) {
        if (email.isBlank()) {
            _mensaje.value = "Ingrese un correo electrónico"
            return
        }

        viewModelScope.launch {
            val usuario = usuarioDao.login(email)

            if (usuario != null) {
                _usuarioActual.value = usuario
                _mensaje.value = "Inicio de sesión correcto"
            } else {
                _usuarioActual.value = null
                _mensaje.value = "Usuario no encontrado"
            }
        }
    }

    fun registrarUsuario(nombre: String, email: String) {
        if (nombre.isBlank() || email.isBlank()) {
            _mensaje.value = "Complete todos los campos"
            return
        }

        viewModelScope.launch {
            val usuario = UsuarioEntity(
                nombre = nombre,
                email = email
            )

            val resultado = usuarioDao.registrar(usuario)

            if (resultado != -1L) {
                _usuarioActual.value = usuario.copy(id = resultado)
                _mensaje.value = "Usuario registrado correctamente"
            } else {
                _mensaje.value = "El usuario ya se encuentra registrado"
            }
        }
    }
}