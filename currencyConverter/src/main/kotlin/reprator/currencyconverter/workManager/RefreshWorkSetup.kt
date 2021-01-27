package reprator.currencyconverter.workManager

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RefreshWorkSetup {
    fun startRefreshPeriodicWork(@ApplicationContext context: Context)
}

class RefreshWorkSetupImpl @Inject constructor() :
    RefreshWorkSetup {

    companion object {
        private const val WORKER_TAG = "paypay workmanager"
        private const val WORKER_MANAGER_TAG = "workmanager"
    }

    override fun startRefreshPeriodicWork(@ApplicationContext context: Context) {
        Timber.e("in work manager start")

        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshWork = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            30, TimeUnit.MINUTES,                  // repeatInterval (the period cycle)
            5, TimeUnit.MINUTES
        ).setConstraints(myConstraints)
            .addTag(WORKER_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORKER_MANAGER_TAG,
            ExistingPeriodicWorkPolicy.KEEP, refreshWork
        )
    }
}