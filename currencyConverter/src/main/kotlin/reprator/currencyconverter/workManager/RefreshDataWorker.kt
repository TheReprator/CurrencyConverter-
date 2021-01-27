package reprator.currencyconverter.workManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import reprator.currencyconverter.domain.usecase.CurrencyExchangeRateUseCase
import reprator.currencyconverter.domain.usecase.CurrencyListUseCase
import reprator.currencyconverter.modals.ModalCurrency
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.PayPayResult
import reprator.paypay.base.util.AppCoroutineDispatchers
import timber.log.Timber


class RefreshDataWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val applicationDispatchers: AppCoroutineDispatchers,
    private val currencyListUseCase: CurrencyListUseCase,
    private val currencyExchangeRateUseCase: CurrencyExchangeRateUseCase
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.e("in work manager doWork")
        showNotification()
        try {
            val res1 = getCurrencyRateUseCaseAsync()
            val res2 = getCurrencyListUseCaseAsync()

            Timber.e(
                "in work manager result ${res1.await().single()} different ${
                    res2.await().single()
                }"
            )
        } catch (e: Exception) {
            Timber.e("in work manager Exception occurred ${e.message}")
            e.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    private suspend fun getCurrencyListUseCaseAsync(): Deferred<Flow<PayPayResult<List<ModalCurrency>>>> =
        coroutineScope {
            async(applicationDispatchers.io) {
                Timber.e("in work manager currency")
                currencyListUseCase.invoke(true)
            }
        }

    private suspend fun getCurrencyRateUseCaseAsync(): Deferred<Flow<PayPayResult<List<ModalCurrencyExchangeRates>>>> =
        coroutineScope {
            async(applicationDispatchers.io) {
                Timber.e("in work manager rates")
                currencyExchangeRateUseCase.invoke(true)
            }
        }


    private suspend fun showNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "my_channel_id_01"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Configure the notification channel.
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }


        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, NOTIFICATION_CHANNEL_ID
        )

        notificationBuilder.setAutoCancel(false)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setTicker("PayPay")
            .setPriority(IMPORTANCE_HIGH)
            .setContentTitle("PayPay Notification")
            .setContentText("Periodic Notification for db update")
            .setContentInfo("PayPayInfo")

        //notificationManager.notify( 10901, notificationBuilder.build())
        val foregroundInfo = ForegroundInfo(10901, notificationBuilder.build())
        setForeground(foregroundInfo)
    }
}
