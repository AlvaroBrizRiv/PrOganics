package com.prorganics.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prorganics.mobile.data.local.dao.ProductoDao
import com.prorganics.mobile.data.local.dao.UsuarioDao
import com.prorganics.mobile.data.local.entity.ProductoEntity
import com.prorganics.mobile.data.local.entity.UsuarioEntity

/**
 * Base de datos principal de la aplicación.
 * Mantiene la integridad al ser abstracta y delegar la implementación a Room.
 */
@Database(
    entities = [UsuarioEntity::class, ProductoEntity::class],
    version = 1,
    exportSchema = false // Escalabilidad: false para proyectos iniciales, true cuando implementes migraciones complejas
)
abstract class PrOrganicsDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile // Disponibilidad/Confiabilidad: Asegura que los cambios en INSTANCE sean visibles para todos los hilos
        private var INSTANCE: PrOrganicsDatabase? = null

        // Escalabilidad y Rendimiento: Implementa el patrón Singleton usando Double-Check Locking
        // Evita crear múltiples instancias de la BD (operación costosa) y reduce el código.
        fun getDatabase(context: Context): PrOrganicsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrOrganicsDatabase::class.java,
                    "prorganics_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
