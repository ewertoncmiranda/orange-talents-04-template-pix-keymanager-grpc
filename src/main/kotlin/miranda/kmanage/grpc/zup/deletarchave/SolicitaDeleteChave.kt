package miranda.kmanage.grpc.zup.deletarchave

import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.validacao.ValidarUUID
import javax.validation.constraints.NotBlank


data class SolicitaDeleteChave  (val idPix:Long,
                                 val idCliente:String)
