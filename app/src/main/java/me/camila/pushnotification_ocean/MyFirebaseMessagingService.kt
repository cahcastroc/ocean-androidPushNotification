package me.camila.pushnotification_ocean

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //Podemos trabalhar com o token, enviando para o Backend ou algo do tipo
        //Vamos somente exibir log
        Log.d("FIREBASE", "Token atualizado: $token")
    }

        override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FIREBASE", "Mensagem recebida: $message")

            val notification = message.notification

            notification?.let {
                val title= it.title
                val body = it.body

                pushNotification(title,body)
            }
    }

    private fun pushNotification(title: String?, body: String?) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        //Canal de notificação - a partir do Oreo

        val channelId= "OCEAN_PRINCIPAL"

        val channelName = "Ocean - Canal Principal"

        val channelDescription = "Ocean - Canal utilizado para notificações Notion"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description= channelDescription

            }
            notificationManager.createNotificationChannel(channel)
        }

        //Criar a PendingIntent para clicar na notificação e abrir o app

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        }else{
            PendingIntent.FLAG_ONE_SHOT
        }

        val pendingIntent = PendingIntent.getActivity(this,0,intent,pendingIntentFlags)

        //Criação da notificação

        val notificationBuilder = NotificationCompat.Builder(this, channelId) //contexto e o channel id
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)  //Para abrir o app ao clicar na notificação
            .setAutoCancel(true) //Limpar a notificação ao clicar

        //Envio da notificação

        val notification = notificationBuilder.build()
        val notificationId = 1 //importante para editar a notificação posteriormente. Notificação diferentes id, edição mesmo id
        notificationManager.notify(notificationId, notification)

    }
}