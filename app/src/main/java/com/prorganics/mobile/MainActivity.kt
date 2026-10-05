package com.prorganics.mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.prorganics.mobile.data.local.PrOrganicsDatabase
import com.prorganics.mobile.data.local.entity.UsuarioEntity
import com.prorganics.mobile.ui.screens.CatalogScreen
import com.prorganics.mobile.ui.screens.LoginScreen
import com.prorganics.mobile.ui.screens.RegisterScreen
import com.prorganics.mobile.ui.theme.PrOrganicsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Integridad: Control de estado simple para la navegación (sin dependencias extra complejas)
enum class AppScreen {
    CATALOG, LOGIN, REGISTER
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Prueba de ejecución de consultas a la BD local
        lifecycleScope.launch(Dispatchers.IO) {
            val db = PrOrganicsDatabase.getDatabase(applicationContext)
            Log.d("MainActivityLog", "Solicitando productos a la base de datos...")
            
            db.productoDao().listarPorUsuario(1).collect { productos ->
                Log.d("MainActivityLog", "Se obtuvieron ${productos.size} productos desde SQLite/Room:")
                productos.forEach { producto ->
                    Log.d("MainActivityLog", " - [ID ${producto.id}] ${producto.nombre} ($${producto.precio})")
                }
            }
        }

        setContent {
            PrOrganicsTheme {
                // Estado que controla qué pantalla se muestra. Inicia en CATALOG.
                var currentScreen by remember { mutableStateOf(AppScreen.CATALOG) }

                when (currentScreen) {
                    AppScreen.CATALOG -> {
                        CatalogScreen(
                            onNavigateToLogin = { currentScreen = AppScreen.LOGIN },
                            onNavigateToRegister = { currentScreen = AppScreen.REGISTER }
                        )
                    }
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginClick = { email, _ -> 
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val db = PrOrganicsDatabase.getDatabase(applicationContext)
                                    val usuario = db.usuarioDao().login(email)
                                    
                                    if (usuario != null) {
                                        Log.d("MainActivityLog", "Login exitoso. Bienvenido, ${usuario.nombre}")
                                        currentScreen = AppScreen.CATALOG // Redirigir al catálogo
                                    } else {
                                        Log.d("MainActivityLog", "Error: Credenciales inválidas o usuario no existe.")
                                    }
                                }
                            },
                            onBackClick = { currentScreen = AppScreen.CATALOG }
                        )
                    }
                    AppScreen.REGISTER -> {
                        RegisterScreen(
                            onRegisterClick = { nombre, email, _ -> 
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val db = PrOrganicsDatabase.getDatabase(applicationContext)
                                    
                                    // Se registra el nuevo usuario en Room (SQLite)
                                    // (Nota: No se almacena la contraseña porque no está en la Entidad por motivos de prueba/seguridad básica)
                                    val nuevoUsuario = UsuarioEntity(
                                        nombre = nombre,
                                        email = email
                                    )
                                    
                                    val id = db.usuarioDao().registrar(nuevoUsuario)
                                    Log.d("MainActivityLog", "Registro exitoso en BD. Nuevo ID de usuario: $id")
                                    
                                    currentScreen = AppScreen.LOGIN // Redirigir al login tras registrarse
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}