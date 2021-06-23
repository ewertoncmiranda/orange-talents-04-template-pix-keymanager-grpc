package miranda.kmanage.grpc.zup.validacao

import io.micronaut.core.annotation.AnnotationValue
import miranda.kmanage.grpc.zup.novachave.NovaChavePix
import javax.inject.Singleton
import javax.validation.*
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValidadorDeChavePix::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS,AnnotationTarget.TYPE)
annotation class ValidarChave(
    val message :String="Erro no formato da Chave",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidadorDeChavePix(): ConstraintValidator<ValidarChave, NovaChavePix> {

    override fun isValid(value: NovaChavePix?,
                         context: ConstraintValidatorContext?): Boolean {

        if(value?.tipoDeChave == null) return false

        return value.tipoDeChave.valida(value.chave)

    }

}