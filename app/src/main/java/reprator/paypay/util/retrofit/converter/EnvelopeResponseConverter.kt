package reprator.paypay.util.retrofit.converter

import okhttp3.ResponseBody
import reprator.paypay.util.retrofit.FailureException
import reprator.paypay.util.retrofit.wrapperModal.EnvelopeResponse
import retrofit2.Converter

internal class EnvelopeResponseConverter<T>(private val delegate: Converter<ResponseBody, EnvelopeResponse<T>>) :
    Converter<ResponseBody, T> {

    @Throws(FailureException::class)
    override fun convert(responseBody: ResponseBody): T? {
        val envelope = delegate.convert(responseBody)
        return if (true == envelope?.success)
            envelope.responseData
        else
            throw FailureException(envelope?.error?.info ?: UNKNOWN_ERROR)
    }

    companion object {
        private const val UNKNOWN_ERROR = "An Unknown error occurred"
    }
}