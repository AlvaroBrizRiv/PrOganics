package com.prorganics.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prorganics.mobile.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrar(producto: ProductoEntity): Long

    // ESCALABILIDAD Y DISPONIBILIDAD: Retornar 'Flow' permite a la UI escuchar cambios 
    // en la BD en tiempo real, sin bloquear el hilo principal ni requerir recargas manuales.
    @Query("SELECT * FROM productos WHERE usuario_id = :usuarioId ORDER BY fecha_creacion DESC")
    fun listarPorUsuario(usuarioId: Long): Flow<List<ProductoEntity>>
}
