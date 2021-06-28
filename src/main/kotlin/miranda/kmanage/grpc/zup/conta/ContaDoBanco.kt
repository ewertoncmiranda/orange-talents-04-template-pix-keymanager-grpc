package miranda.kmanage.grpc.zup.conta

import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Embeddable
class ContaDoBanco (
    @field:NotNull val instituicao :String,
    @field:NotNull val nomeDoTitular :String,
    @field:NotNull val agencia :String,
    @field:NotNull val numeroConta :String,
    @Column(name = "conta_titular_cpf", length = 11, nullable = false)
    @field:NotNull val cpf :String
    ) {
    companion object {
        public val ITAU_UNIBANCO_ISPB: String = "60701190"
    }

}