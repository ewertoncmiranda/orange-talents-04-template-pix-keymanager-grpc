package miranda.kmanage.grpc.zup.sistemasexternos.itaudto

import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta

data class ContaCompletaResponse (val tipo:String,
                                  val agencia:String,
                                  val numero:String,
                                  val titular:TitularResponse,
                                  val instituicao : InstituicaoResponse )

