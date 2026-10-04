package com.prorganics.mobile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa la tabla de Productos en la base de datos local.
 * Integridad relacional: Un producto pertenece a un usuario. Si el usuario es eliminado, 
 * sus productos también se eliminarán en cascada.
 */
@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE // Mantiene la integridad referencial
        )
    ],
    indices = [Index("usuario_id")] // Mejora el rendimiento de consultas (disponibilidad/velocidad)
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Long,
    
    val nombre: String,
    
    val descripcion: String,
    
    val precio: Double,
    
    val stock: Int,
    
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis()
)
