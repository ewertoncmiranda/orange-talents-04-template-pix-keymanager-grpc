package miranda.kmanage.grpc.zup.validacao

import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import miranda.kmanage.grpc.zup.exception.ChaveExistenteException
import miranda.kmanage.grpc.zup.exception.ClienteNaoCadastradoNoBancoException
import java.lang.Exception
import java.lang.RuntimeException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException

@Singleton
@InterceptorBean(ErrorGeralHandler::class)
class InterceptadorDeErros :MethodInterceptor<Any,Any>{

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return  context.proceed()

        }catch (e:Exception){

            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when (e){
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(e).withDescription(e.message)
                is ChaveExistenteException -> Status.ALREADY_EXISTS.withCause(e).withDescription(e.message)
                is HttpClientResponseException -> Status.NOT_FOUND.withCause(e).withDescription("Conta/Cliente não existe no banco.")
                is ClienteNaoCadastradoNoBancoException -> Status.NOT_FOUND.withCause(e).withDescription(e.message)
                is ValidationException -> Status.INVALID_ARGUMENT.withCause(e).withDescription("ERRO NO FORMATO DO CPF")
                else -> Status.INTERNAL.withDescription("Erro interno na aplicação.")
            }

            val  statusRuntimeException = StatusRuntimeException(status)

            responseObserver.onError(statusRuntimeException)

            return null
        }
    }


}