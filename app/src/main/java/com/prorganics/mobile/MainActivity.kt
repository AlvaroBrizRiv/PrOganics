package com.prorganics.mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.prorganics.mobile.data.local.PrOrganicsDatabase
import com.prorganics.mobile.ui.screens.CatalogScreen
import com.prorganics.mobile.ui.screens.LoginScreen
import com.prorganics.mobile.ui.screens.RegisterScreen
import com.prorganics.mobile.ui.theme.PrOrganicsTheme
import com.prorganics.mobile.ui.viewmodel.AuthViewModel
import com.prorganics.mobile.ui.viewmodel.CatalogViewModel
import com.prorganics.mobile.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy {
        PrOrganicsDatabase.getDatabase(applicationContext)
    }

    private val viewModelFactory by lazy {
        ViewModelFactory(
            usuarioDao = database.usuarioDao(),
            productoDao = database.productoDao()
        )
    }

    private val authViewModel: AuthViewModel by viewModels {
        viewModelFactory
    }

    private val catalogViewModel: CatalogViewModel by viewModels {
        viewModelFactory
    }

    private val solicitarPermisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permisoConcedido ->

            if (permisoConcedido) {
                mostrarNotificacionOfertas(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        crearCanalNotificaciones(this)
        comprobarPermisoNotificaciones()

        setContent {
            PrOrganicsTheme {
                val navController = rememberNavController()
                val usuarioActual by authViewModel.usuarioActual.collectAsState()

                // Cargar los productos inmediatamente, sin importar el usuario
                LaunchedEffect(Unit) {
                    catalogViewModel.cargarProductos()
                }

                LaunchedEffect(usuarioActual) {
                    usuarioActual?.let {
                        // Si se inició sesión/registró exitosamente y no estamos en el catálogo, volvemos a él.
                        if (navController.currentBackStackEntry?.destination?.route != "catalog") {
                            navController.popBackStack("catalog", inclusive = false)
                        }
                    }
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "catalog",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("catalog") {
                            CatalogScreen(
                                catalogViewModel = catalogViewModel,
                                authViewModel = authViewModel,
                                onNavigateToLogin = { navController.navigate("login") },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("login") {
                            // Limpiamos los mensajes del ViewModel antes de entrar a la pantalla
                            LaunchedEffect(Unit) {
                                authViewModel.limpiarMensaje()
                            }
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    authViewModel.iniciarSesion(email, password)
                                },
                                onBackClick = { navController.popBackStack() },
                                onNavigateToRegister = { navController.navigate("register") },
                                authViewModel = authViewModel
                            )
                        }
                        composable("register") {
                            // Limpiamos los mensajes del ViewModel antes de entrar a la pantalla
                            LaunchedEffect(Unit) {
                                authViewModel.limpiarMensaje()
                            }
                            RegisterScreen(
                                onRegisterClick = { nombre, email, password ->
                                    authViewModel.registrarUsuario(nombre, email, password)
                                },
                                onBackClick = { navController.popBackStack() },
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                authViewModel = authViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    private fun comprobarPermisoNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mostrarNotificacionOfertas(this)
            } else {
                solicitarPermisoNotificaciones.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

        } else {
            mostrarNotificacionOfertas(this)
        }
    }
}

