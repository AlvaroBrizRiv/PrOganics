package com.prorganics.mobile.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prorganics.mobile.data.local.dao.PedidoDao
import com.prorganics.mobile.data.local.dao.ProductoDao
import com.prorganics.mobile.data.local.dao.UsuarioDao
import com.prorganics.mobile.data.local.entity.PedidoEntity
import com.prorganics.mobile.data.local.entity.ProductoEntity
import com.prorganics.mobile.data.local.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * Base de datos principal de la aplicación.
 * Mantiene la integridad al ser abstracta y delegar la implementación a Room.
 */
@Database(
    entities = [UsuarioEntity::class, ProductoEntity::class, PedidoEntity::class],
    version = 2, // Aumentamos la versión para forzar la migración
    exportSchema = false // Escalabilidad: false para proyectos iniciales, true cuando implementes migraciones complejas
)
abstract class PrOrganicsDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun pedidoDao(): PedidoDao

    private class PrOrganicsDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.usuarioDao(), database.productoDao())
                }
            }
        }

        suspend fun populateDatabase(usuarioDao: UsuarioDao, productoDao: ProductoDao) {
            Log.d("PrOrganicsDatabase", "Iniciando la inserción de datos de prueba en la BD...")
            
            // 1. Crear un usuario de prueba
            val usuarioId = usuarioDao.registrar(
                UsuarioEntity(
                    nombre = "Agricultor Prueba",
                    email = "demo@prorganics.com"
                )
            )
            Log.d("PrOrganicsDatabase", "Usuario registrado exitosamente con ID: $usuarioId")

            // 2. Inyectar productos orgánicos de prueba vinculados a ese usuario
            val productosPrueba = listOf(
                ProductoEntity(usuarioId = usuarioId, nombre = "Manzanas Fuji Orgánicas", descripcion = "Dulces y crujientes, cultivadas sin pesticidas.", precio = 3.50, stock = 100),
                ProductoEntity(usuarioId = usuarioId, nombre = "Zanahorias Bio", descripcion = "Directas de la huerta, llenas de vitaminas.", precio = 1.20, stock = 200),
                ProductoEntity(usuarioId = usuarioId, nombre = "Miel de Abeja Pura", descripcion = "Miel cruda sin procesar de apicultura sostenible.", precio = 8.00, stock = 50),
                ProductoEntity(usuarioId = usuarioId, nombre = "Lechuga Romana Fresca", descripcion = "Ideal para ensaladas. Cosecha del día.", precio = 1.80, stock = 60),
                ProductoEntity(usuarioId = usuarioId, nombre = "Tomates Cherry de Rama", descripcion = "Pequeños, dulces y 100% orgánicos.", precio = 2.90, stock = 150)
            )

            productosPrueba.forEach { producto ->
                val id = productoDao.registrar(producto)
                Log.d("PrOrganicsDatabase", "Producto registrado en BD [ID: $id]: ${producto.nombre}")
            }
            Log.d("PrOrganicsDatabase", "Población inicial de datos de catálogo completada exitosamente.")
        }
    }

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
                )
                .fallbackToDestructiveMigration() // Destruye y recrea la BD si hay cambio de versión
                .addCallback(PrOrganicsDatabaseCallback())
                .setQueryCallback({ sqlQuery, bindArgs ->
                    Log.d("RoomQueryLog", "SQL Executed: $sqlQuery | Args: $bindArgs")
                }, Executors.newSingleThreadExecutor())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
