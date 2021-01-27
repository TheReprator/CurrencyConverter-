package reprator.paypay.base.useCases

sealed class PayPayResult<out T> {
    open fun get(): T? = null
}

data class Success<T>(val data: T, val responseModified: Boolean = true) : PayPayResult<T>() {
    override fun get(): T = data
}

data class ErrorResult(
    val throwable: Throwable? = null,
    val message: String? = null
) : PayPayResult<Nothing>()