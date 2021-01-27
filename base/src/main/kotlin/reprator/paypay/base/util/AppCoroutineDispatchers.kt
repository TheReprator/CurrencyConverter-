package reprator.paypay.base.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface AppCoroutineDispatchers {
    val main: CoroutineDispatcher
    val computation: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val singleThread: CoroutineDispatcher
}