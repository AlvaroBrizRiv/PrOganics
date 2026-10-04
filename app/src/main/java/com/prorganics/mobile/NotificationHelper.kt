package com.prorganics.mobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID = "ofertas_especiales"
private const val NOTIFICATION_ID = 1001

fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nombre = "Ofertas especiales"
        val descripcion = "Notificaciones de ofertas y promociones"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val canal = NotificationChannel(
            CHANNEL_ID,
            nombre,
            importancia
        ).apply {
            description = descripcion
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(canal)
    }
}

fun mostrarNotificacionOfertas(context: Context) {
    val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Ofertas especiales")
        .setContentText("¡Tenemos nuevas ofertas para ti!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, notificacion)
    }
}