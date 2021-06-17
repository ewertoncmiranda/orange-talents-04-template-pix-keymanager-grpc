package miranda.kmanage.grpc.zup.conta

import miranda.kmanage.grpc.zup.enum.TipoDaConta
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
class ContaDoBanco (@field:NotNull val instituicao :String,
                    @Enumerated(EnumType.STRING) val tipo: TipoDaConta,
                    @field:NotNull val titular :String,
                    @field:NotNull val agencia :String,
                    @field:NotNull val numeroConta :String,
                    @field:NotNull val cpf :String,
                    @field:NotNull val clientId:String   ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}