package miranda.kmanage.grpc.zup.novachave

import io.micronaut.core.annotation.Introspected
import miranda.kmanage.grpc.zup.TipoChave
import miranda.kmanage.grpc.zup.TipoConta
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.validacao.ValidarChave
import miranda.kmanage.grpc.zup.validacao.ValidarUUID
import java.util.*
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidarChave
data class NovaChavePix (@field:NotBlank @ValidarUUID
                         val clienteId:String?,

                         @Enumerated(EnumType.STRING)
                         val tipo:TipoDeChave?,

                         @field:NotNull
                         @field:Size(max = 77)
                         val chave:String?,

                         @Enumerated(EnumType.STRING)
                         var conta:TipoDaConta?
  ){
 fun toModel():ChavePix{

     val valorChave = when(tipo){
                        TipoDeChave.ALEATORIO-> UUID.randomUUID().toString()
                        else -> chave
     }
     return ChavePix(clienteId!!,tipo!!,valorChave!!,conta!!,null)

 }

 }
