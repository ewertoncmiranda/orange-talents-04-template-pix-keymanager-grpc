package miranda.kmanage.grpc.zup.chavepix

import jdk.jfr.Timestamp
import miranda.kmanage.grpc.zup.TipoChave
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
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
    val chave : String ,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val tipoConta:TipoDaConta ,

    @ManyToOne
    var conta : ContaDoBanco?



){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
    val criadaEm:LocalDateTime = LocalDateTime.now()




}