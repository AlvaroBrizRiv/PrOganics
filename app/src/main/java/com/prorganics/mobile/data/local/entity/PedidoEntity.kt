package com.prorganics.mobile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa la tabla de Pedidos (Historial de compras) en la base de datos local.
 * Integridad relacional: Un pedido pertenece a un usuario. Si el usuario es eliminado, 
 * sus pedidos también se eliminarán en cascada.
 */
@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuario_id")]
)
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Long,
    
    @ColumnInfo(name = "fecha_compra")
    val fechaCompra: Long = System.currentTimeMillis(),
    
    val total: Double,
    
    val estado: String = "COMPLETADO"
)
