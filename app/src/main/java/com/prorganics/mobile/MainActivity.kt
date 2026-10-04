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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

    // Solicita el permiso de notificaciones en Android 13 o superior
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

        // Crea el canal necesario para mostrar notificaciones
        crearCanalNotificaciones(this)

        // Comprueba el permiso y muestra la notificación
        comprobarPermisoNotificaciones()

        setContent {
            PrOrganicsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WelcomeContent(
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
fun WelcomeContent(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.welcome_message),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun WelcomeContentPreview() {
    PrOrganicsTheme {
        WelcomeContent()
    }
}