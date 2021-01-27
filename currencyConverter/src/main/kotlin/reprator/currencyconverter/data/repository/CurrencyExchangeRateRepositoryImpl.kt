package reprator.currencyconverter.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import reprator.currencyconverter.data.datasource.CurrencyExchangeRateRemoteDataSource
import reprator.currencyconverter.data.repository.db.DBManager
import reprator.currencyconverter.domain.repository.CurrencyExchangeRateRepository
import reprator.currencyconverter.modals.ModalCurrencyExchangeRates
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.PayPayResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.ConnectionDetector
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CurrencyExchangeRateRepositoryImpl @Inject constructor(
    private val currencyExchangeRateRemoteDataSource: CurrencyExchangeRateRemoteDataSource,
    private val dbManager: DBManager,
    private val coroutineScope: CoroutineScope,
    private val connectionDetector: ConnectionDetector
) : CurrencyExchangeRateRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrencyExchangeRates(isFromWorkManager: Boolean): Flow<PayPayResult<List<ModalCurrencyExchangeRates>>> {
        return if (isFromWorkManager)
            flow {
                Timber.e("vikram4 insideEmit")
                emit(saveCurrencyRateDB(isFromWorkManager))
            }
        else
            callbackFlow<PayPayResult<List<ModalCurrencyExchangeRates>>> {
                dbManager.getCurrencyExchangeRates().collect {
                    when (it) {
                        is Success -> {
                            offer(Success(it.data))
                            close()
                        }
                        is ErrorResult -> {
                            if (connectionDetector.isInternetAvailable) {
                                offer(saveCurrencyRateDB())
                                close()
                            } else {
                                offer(ErrorResult(message = "No internet connection."))
                                close()
                            }
                        }
                    }
                }

                awaitClose()
            }
    }

    private suspend fun saveCurrencyRateDB(isFromWorkManager: Boolean = false): PayPayResult<List<ModalCurrencyExchangeRates>> =
        suspendCancellableCoroutine { cont ->

            coroutineScope.launch {
                currencyExchangeRateRemoteDataSource.getCurrencyExchangeRates()
                    .catch {
                        cont.resumeWithException(CurrencyListException(it.message ?: "error"))
                    }.collect {
                        when (it) {
                            is Success -> {
                                Timber.e("vikram5 $isFromWorkManager")
                                if (isFromWorkManager)
                                    dbManager.deleteCurrencyExchangeRates()
                                dbManager.saveCurrencyExchangeRates(it.data)
                                Timber.e("vikram6 $isFromWorkManager ${it.data}")
                                cont.resume(Success(it.data))
                            }
                            is ErrorResult -> {
                                Timber.e("vikram11 error $isFromWorkManager")
                                cont.resumeWithException(
                                    CurrencyExchangeRateException(
                                        it.message ?: it.throwable?.message!!
                                    )
                                )
                            }
                        }
                    }
            }
        }
}

class CurrencyExchangeRateException(override val message: String) : Exception()