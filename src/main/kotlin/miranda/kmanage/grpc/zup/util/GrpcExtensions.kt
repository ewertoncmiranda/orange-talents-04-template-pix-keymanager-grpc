package miranda.kmanage.grpc.zup.uti

import miranda.kmanage.grpc.zup.DeletaChaveRequester
import miranda.kmanage.grpc.zup.NovaChaveRequester
import miranda.kmanage.grpc.zup.TipoChave
import miranda.kmanage.grpc.zup.TipoConta
import miranda.kmanage.grpc.zup.deletarchave.SolicitaDeleteChave
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.novachave.NovaChavePix

fun NovaChaveRequester.toModel(): NovaChavePix {
    return NovaChavePix(
         chave = chave,
         clienteId =  iDcliente,
         tipo = when(tipoChave){
            TipoChave.CELULAR ->TipoDeChave.CELULAR
            TipoChave.CPF->TipoDeChave.CPF
            TipoChave.ALEATORIO ->TipoDeChave.ALEATORIO
            TipoChave.EMAIL->TipoDeChave.EMAIL
            else -> null
        },
        conta =  when(tipoConta){
            TipoConta.CONTA_CORRENTE ->TipoDaConta.CONTA_CORRENTE
            TipoConta.CONTA_POUPANCA->TipoDaConta.CONTA_POUPANCA
            else -> null
        }

    )
}

fun DeletaChaveRequester.toModel(): SolicitaDeleteChave {
     return SolicitaDeleteChave(idPix ,clienteId)
}