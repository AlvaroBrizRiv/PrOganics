package com.prorganics.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prorganics.mobile.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    
    // INTEGRIDAD: IGNORE previene que se sobrescriban datos si el usuario ya existe.
    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registrar(usuario: UsuarioEntity): Long

    // CONFIDENCIALIDAD: Consulta filtrada estrictamente. 
    // (Nota de seguridad: Si escalas a contraseñas, usa hashes o tokens, nunca texto plano).
    @JvmSuppressWildcards
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun login(email: String): UsuarioEntity?
}
