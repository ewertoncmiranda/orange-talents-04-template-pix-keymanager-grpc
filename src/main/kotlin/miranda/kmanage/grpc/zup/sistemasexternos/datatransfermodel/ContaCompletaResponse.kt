package miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel

import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta

data class ContaCompletaResponse (val tipo:String,
                                  val agencia:String,
                                  val numero:String,
                                  val titular:TitularResponse,
                                  val instituicao : InstituicaoResponse ){
    fun toModel():ContaDoBanco{
        val tipo = when(tipo){
            "CONTA_CORRENTE"-> TipoDaConta.CONTA_CORRENTE
            "CONTA_POUPANCA"-> TipoDaConta.CONTA_POUPANCA
             else -> null
        }

        return  ContaDoBanco(instituicao.nome,
                                       tipo!!,
                                 titular.nome,
                                      agencia,
                                       numero,
                                  titular.cpf,
                                   titular.id )
    }
}
