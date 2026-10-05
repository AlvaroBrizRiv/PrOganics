package com.prorganics.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prorganics.mobile.data.local.dao.UsuarioDao
import com.prorganics.mobile.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    // Función para cifrar la contraseña usando SHA-256 (Garantiza Confidencialidad)
    private fun cifrarContrasena(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // Validar formato de email usando Expresiones Regulares
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return emailRegex.matches(email)
    }

    // Validar nombre: Solo admite letras, mayúsculas y minúsculas (y espacios)
    private fun isValidNombre(nombre: String): Boolean {
        val nombreRegex = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$".toRegex()
        return nombreRegex.matches(nombre)
    }

    fun iniciarSesion(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _mensaje.value = "Rellene todos los campos"
            return
        }

        if (!isValidEmail(email)) {
            _mensaje.value = "Campos inválidos"
            return
        }

        viewModelScope.launch {
            val contrasenaCifrada = cifrarContrasena(contrasena)
            val usuario = usuarioDao.login(email, contrasenaCifrada)

            if (usuario != null) {
                _usuarioActual.value = usuario
                _mensaje.value = "" // Limpiamos mensajes de error previos
            } else {
                _usuarioActual.value = null
                _mensaje.value = "Inicio de sesión fallido: Usuario o contraseña incorrectos"
            }
        }
    }

    fun registrarUsuario(nombre: String, email: String, contrasena: String) {
        if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
            _mensaje.value = "Rellene todos los campos"
            return
        }

        if (!isValidNombre(nombre) || !isValidEmail(email)) {
            _mensaje.value = "Campos inválidos"
            return
        }

        viewModelScope.launch {
            val contrasenaCifrada = cifrarContrasena(contrasena)
            
            val usuario = UsuarioEntity(
                nombre = nombre,
                email = email,
                contrasena = contrasenaCifrada
            )

            val resultado = usuarioDao.registrar(usuario)

            if (resultado != -1L) {
                _usuarioActual.value = usuario.copy(id = resultado)
                _mensaje.value = "Registro exitoso"
            } else {
                _mensaje.value = "Registro fallido: El usuario ya se encuentra registrado"
            }
        }
    }
    
    // Método para limpiar el mensaje al navegar a una nueva pantalla
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
    
    // Método para cerrar sesión
    fun cerrarSesion() {
        _usuarioActual.value = null
        _mensaje.value = ""
    }
}