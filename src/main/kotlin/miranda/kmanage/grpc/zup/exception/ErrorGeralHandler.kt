package miranda.kmanage.grpc.zup.exception

import io.micronaut.aop.Around

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
@Around
annotation class ErrorGeralHandler
