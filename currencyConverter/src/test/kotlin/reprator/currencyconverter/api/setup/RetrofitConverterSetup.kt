package reprator.currencyconverter.api.setup

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.type.TypeFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import javax.inject.Inject

class EnvelopeConverterFactory @Inject constructor(private val typeFactory: TypeFactory) :
    Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {

        val constructType = typeFactory.constructType(type)
        val envelopedResponse =
            typeFactory.constructParametricType(EnvelopeResponse::class.java, constructType)

        val delegate: Converter<ResponseBody, EnvelopeResponse<WildcardType>> =
            retrofit.nextResponseBodyConverter(this, envelopedResponse, annotations)
        return EnvelopeResponseConverter(delegate)
    }

}


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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("success", "error", "currencies", "quotes")
class EnvelopeResponse<out T>(
    val success: Boolean,
    val error: EnvelopeErrorBody?,
    @JsonAlias("currencies", "quotes") val responseData: T?
)


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("code", "info")
class EnvelopeErrorBody(val code: Int, val info: String)

class FailureException(private val errorMessage: String) : RuntimeException(errorMessage)