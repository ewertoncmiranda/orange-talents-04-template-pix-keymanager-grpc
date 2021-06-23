package miranda.kmanage.grpc.zup.novachave

import io.micronaut.core.annotation.Introspected
import miranda.kmanage.grpc.zup.TipoChave
import miranda.kmanage.grpc.zup.TipoConta
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.ContaCompletaResponse
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
                         val tipoDeChave:TipoDeChave?,

                         @field:NotNull
                         @field:Size(max = 77)
                         val chave:String?,

                         @Enumerated(EnumType.STRING)
                         var tipoDeConta:TipoDaConta?
  ){
 fun toModel(c: ContaCompletaResponse):ChavePix{

     val conta = ContaDoBanco(c.instituicao.nome,c.titular.nome ,c.agencia,c.numero,c.titular.cpf)

     return ChavePix(c.titular.id,tipoDeChave!!,chave!!,tipoDeConta!!,conta)

 }

 }
