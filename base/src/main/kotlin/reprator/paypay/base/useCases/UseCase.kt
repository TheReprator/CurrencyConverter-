package reprator.paypay.base.useCases

interface UseCase<Type, in Params> {
     suspend fun run(params: Params): PayPayResult<Type>
}