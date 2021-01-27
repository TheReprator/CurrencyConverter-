package reprator.currencyconverter.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import reprator.currencyconverter.data.datasource.CurrencyListRemoteDataSource
import reprator.currencyconverter.data.repository.db.DBManager
import reprator.currencyconverter.domain.repository.CurrencyListRepository
import reprator.currencyconverter.modals.ModalCurrency
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.PayPayResult
import reprator.paypay.base.useCases.Success
import reprator.paypay.base.util.ConnectionDetector
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CurrencyListRepositoryImpl @Inject constructor(
    private val currencyListRemoteDataSource: CurrencyListRemoteDataSource,
    private val dbManager: DBManager,
    private val coroutineScope: CoroutineScope,
    private val connectionDetector: ConnectionDetector,
) : CurrencyListRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun currencyList(isFromWorkManager: Boolean): Flow<PayPayResult<List<ModalCurrency>>> {
        return if (isFromWorkManager)
            flow {
                Timber.e("vikram7 currecyList")
                emit(saveCurrencyListInDB(isFromWorkManager))
            }
        else
            callbackFlow<PayPayResult<List<ModalCurrency>>> {
                dbManager.currencyList().collect {
                    when (it) {
                        is Success -> {
                            offer(Success(it.data))
                            close()
                        }
                        is ErrorResult -> {
                            if (connectionDetector.isInternetAvailable) {
                                offer(saveCurrencyListInDB())
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

    private suspend fun saveCurrencyListInDB(isFromWorkManager: Boolean = false): PayPayResult<List<ModalCurrency>> =
        suspendCancellableCoroutine { cont ->

            coroutineScope.launch {

                currencyListRemoteDataSource.currencyList().catch {
                    cont.resumeWithException(CurrencyListException(it.message ?: "error"))
                }.collect {
                    when (it) {
                        is Success -> {
                            Timber.e("vikram8 $isFromWorkManager")
                            if (isFromWorkManager)
                                dbManager.deleteCurrencyList()
                            dbManager.saveCurrencyList(it.data)
                            Timber.e("vikram9 $isFromWorkManager ${it.data}")
                            cont.resume(Success(it.data))
                        }
                        is ErrorResult -> {
                            Timber.e("vikram10 error $isFromWorkManager")
                            cont.resumeWithException(
                                CurrencyListException(
                                    it.message ?: it.throwable?.message!!
                                )
                            )
                        }
                    }
                }
            }
        }
}

class CurrencyListException(override val message: String) : Exception()