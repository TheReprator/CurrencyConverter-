package reprator.paypay.base.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import reprator.paypay.base.useCases.ErrorResult
import reprator.paypay.base.useCases.PayPayResult

suspend fun <T : Any> safeApiCall(
    call: suspend () -> Flow<PayPayResult<T>>,
    errorMessage: String? = null
): Flow<PayPayResult<T>> {
    return try {
        call()
    } catch (e: Exception) {
        println(e.printStackTrace())
        flow {
            emit(ErrorResult(message = errorMessage ?: e.message))
        }
    }
}