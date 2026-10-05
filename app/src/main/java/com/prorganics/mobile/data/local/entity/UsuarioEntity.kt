package com.prorganics.mobile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla de Usuarios en la base de datos local.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val nombre: String,
    
    @ColumnInfo(index = true) // Índice para búsquedas rápidas por email
    val email: String,
    
    val contrasena: String, // Contraseña cifrada (Hash SHA-256)
    
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis()
)
