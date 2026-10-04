package com.prorganics.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AuthViewModel : ViewModel() {

    private val _mensaje = MutableLiveData<String>()

    val mensaje: LiveData<String> = _mensaje

    fun iniciarSesion(email: String) {
        if (email.isBlank()) {
            _mensaje.value = "Ingrese un correo electrónico"
        } else {
            _mensaje.value = "Inicio de sesión correcto"
        }
    }

    fun registrarUsuario(nombre: String, email: String) {
        if (nombre.isBlank() || email.isBlank()) {
            _mensaje.value = "Complete todos los campos"
        } else {
            _mensaje.value = "Usuario registrado correctamente"
        }
    }
}