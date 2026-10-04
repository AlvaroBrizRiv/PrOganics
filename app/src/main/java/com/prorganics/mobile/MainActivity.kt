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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.prorganics.mobile.data.local.PrOrganicsDatabase
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
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    AuthScreen(
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
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

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val mensaje by authViewModel.mensaje.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "PrOrganics",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Registro e inicio de sesión",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                authViewModel.registrarUsuario(
                    nombre = nombre,
                    email = email
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar usuario")
        }

        Button(
            onClick = {
                authViewModel.iniciarSesion(email)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}