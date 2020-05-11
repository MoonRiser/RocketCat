package com.example.rocketcat.utils


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat.*

typealias Callback = ((Builder) -> Builder)

class MyNotification(base: Context) : ContextWrapper(base) {


    companion object {

        //const修饰的常量对应的Java代码为public final static，单独的val为private final static，需要生成额外的getter访问
        const val NORMAL_CHANNEL_ID: String = "normal"
        const val URGENT_CHANNEL_ID: String = "urgent"
    }


    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val NORMAL_CHANNEL_NAME: String = "普通通知"
    private val URGENT_CHANNEL_NAME: String = "重要通知"


    init {

        //init块里的代码相当于主构造器的函数体，同时createChannel方法重复调用不会有影响，channel只会生成一次
        //WARNING!!!这个初始代码块一定要位于notificationManager的下面，要不然会空指针,因为init块的初始化顺序跟位置有关
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val normalChannel = NotificationChannel(
                NORMAL_CHANNEL_ID,
                NORMAL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val urgentChannel = NotificationChannel(
                URGENT_CHANNEL_ID,
                URGENT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            normalChannel.apply {
                enableLights(true)
                setBypassDnd(true)
                setShowBadge(true)
                description = "常规设置"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                lightColor = Notification.COLOR_DEFAULT
            }

            urgentChannel.apply {
                enableLights(true)
                setBypassDnd(true)
                setShowBadge(true)
                description = "开启震动、浮动通知"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(normalChannel)
            notificationManager.createNotificationChannel(urgentChannel)


        }
    }

    private fun getBuilder(channelID: String = NORMAL_CHANNEL_ID): Builder {

        val builder = Builder(applicationContext, channelID)
        return when (channelID) {
            NORMAL_CHANNEL_ID -> builder.setDefaults(DEFAULT_SOUND)
            URGENT_CHANNEL_ID -> builder.apply {
                setDefaults(DEFAULT_VIBRATE or DEFAULT_SOUND)
                priority = PRIORITY_HIGH
            }
            else -> builder
        }


    }

    /**
     * 清除所有通知
     */
    fun clearAllNotifications() = notificationManager.cancelAll()

    /**
     * 可深度定制的builder模式的通知
     * @param id 通知的id，-1表示自增id
     * @param channelID 通知通道的id
     * @param builderE 函数参数，用于自定义通知,可为空
     */
    @JvmOverloads
    fun send(
        title: String?, content: String, icon: Int, id: Int = -1,
        channelID: String = NORMAL_CHANNEL_ID, callback: Callback? = null
    ) {
        val builder = getBuilder(channelID)
        val notification: Notification = run {
            callback?.let {
                it(builder).apply {
                    setContentTitle(title)
                    setContentText(content)
                    setSmallIcon(icon)
                }
            } ?: builder.apply {
                setContentTitle(title)
                setContentText(content)
                setSmallIcon(icon)
            }
        }.build()

        val id1 = if (id == -1) System.currentTimeMillis().toInt() else id
        notificationManager.notify(id1, notification)

    }

    /**
     * 常用的只有标题、内容、小图标的通知
     * @param id 通知id，不指定的时候为默认自增id（时间戳作为id）
     * @param urgent 默认是true重要通知，false普通通知
     */
    @JvmOverloads
    fun sendNotification(
        title: String?,
        content: String,
        icon: Int,
        id: Int = -1,
        urgent: Boolean = true
    ) {
        val builder = if (urgent) getBuilder(URGENT_CHANNEL_ID) else getBuilder(NORMAL_CHANNEL_ID)
        builder.apply {
            setContentTitle(title)
            setContentText(content)
            setSmallIcon(icon)
        }
        val notification = builder.build()
        val id1 = if (id == -1) System.currentTimeMillis().toInt() else id
        notificationManager.notify(id1, notification)
    }


}