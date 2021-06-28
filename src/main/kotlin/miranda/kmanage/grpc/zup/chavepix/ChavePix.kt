package miranda.kmanage.grpc.zup.chavepix

import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.*
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class ChavePix (

    @field:NotNull
    @Column(nullable=false)
    @Lob
    val clientId:String ,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDaChave:TipoDeChave ,

    @field:NotNull
    @Column(nullable = false,unique = true)
    var chave : String ,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val tipoConta:TipoDaConta ,

    @Embedded
    var conta : ContaDoBanco?
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
    val criadaEm:LocalDateTime = LocalDateTime.now()

    fun toBcbModel():CreatePixKeyRequest{
        val bank_acc = BankAccount("60701190",conta!!.agencia,conta!!.numeroConta,tipoConta.toBcbType())
        val owner = Owner(Type.NATURAL_PERSON,conta!!.nomeDoTitular,conta!!.cpf)
        return CreatePixKeyRequest(tipoDaChave.toBcbType() ,chave,owner,bank_acc)
    }
}