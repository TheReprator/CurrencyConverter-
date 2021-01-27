package reprator.paypay.util.retrofit

class FailureException(private val errorMessage: String) : RuntimeException(errorMessage)