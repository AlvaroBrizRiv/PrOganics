package com.prorganics.mobile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla de Productos (Catálogo global) en la base de datos local.
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val nombre: String,
    
    val descripcion: String,
    
    val precio: Double,
    
    val stock: Int,
    
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis()
)
