package com.prorganics.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prorganics.mobile.data.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrar(pedido: PedidoEntity): Long

    @Query("SELECT * FROM pedidos WHERE usuario_id = :usuarioId ORDER BY fecha_compra DESC")
    fun listarPorUsuario(usuarioId: Long): Flow<List<PedidoEntity>>
}
