package reprator.paypay.util.retrofit.converter

import com.fasterxml.jackson.databind.type.TypeFactory
import okhttp3.ResponseBody
import reprator.paypay.util.retrofit.wrapperModal.EnvelopeResponse
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