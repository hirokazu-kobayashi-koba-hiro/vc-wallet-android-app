package org.idp.wallet.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        if (remoteMessage.notification != null) {
            // 通知ペイロードあり → システム通知表示（通常はOSがやってくれる）
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            showNotification(title, body)
        } else if (remoteMessage.data.isNotEmpty()) {
            // データペイロード通知（カスタム処理できる）
            handleDataMessage(remoteMessage.data)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0+ 通知チャンネル作成
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "通知", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(org.idp.wallet.verifiable_credentials_library.R.drawable.id_card)
            .setContentTitle(title ?: "通知")
            .setContentText(message ?: "")
            .setAutoCancel(true)

        notificationManager.notify(1001, notificationBuilder.build())
    }

    private fun handleDataMessage(data: Map<String, String>) {
        // ここで例えばComposeのNavController連携や、状態更新もできる！
        Log.d("FCM", "Data payload: $data")

        val type = data["type"]
        if (type == "auth") {
            showNotification("ログイン確認", "アプリで認証してください")
        }
    }
}