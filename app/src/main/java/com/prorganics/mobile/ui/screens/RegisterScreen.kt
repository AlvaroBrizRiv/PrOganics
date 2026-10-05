package com.prorganics.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prorganics.mobile.ui.theme.PrOrganicsTheme

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados para almacenar el texto ingresado por el usuario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp), // Espaciado generoso para dar "respiro" al diseño (UX)
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título principal con la tipografía y color del tema existente
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo: Nombre de Usuario
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next // Tecla "Siguiente" en el teclado para mejor flujo (UX)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Campo: Correo Electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, // Teclado adaptado para emails (arroba visible)
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Campo: Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta los caracteres escritos
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done // Tecla "Listo" en el teclado para finalizar (UX)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )

        // Botón de Registro
        Button(
            onClick = { onRegisterClick(nombre, email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // Altura táctil recomendada por Material Design (Touch target > 48dp)
            // UX: El botón solo se habilita si todos los campos tienen información
            enabled = nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// Vista previa para Android Studio (Split View)
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    PrOrganicsTheme {
        Surface {
            RegisterScreen(onRegisterClick = { _, _, _ -> })
        }
    }
}
