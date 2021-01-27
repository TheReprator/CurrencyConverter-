package reprator.paypay.util.retrofit.wrapperModal

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder


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