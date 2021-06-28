package miranda.kmanage.grpc.zup.util

import miranda.kmanage.grpc.zup.DeletaChaveRequester
import miranda.kmanage.grpc.zup.NovaChaveRequester
import miranda.kmanage.grpc.zup.TipoChave
import miranda.kmanage.grpc.zup.TipoConta
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.novachave.NovaChavePix

fun NovaChaveRequester.toModel(): NovaChavePix {
    return NovaChavePix(
         chave = chave,
         clienteId =  iDcliente,
         tipoDeChave = when(tipoChave){
            TipoChave.CELULAR ->TipoDeChave.valueOf(tipoChave.name)
            TipoChave.CPF->TipoDeChave.valueOf(tipoChave.name)
            TipoChave.ALEATORIO ->TipoDeChave.valueOf(tipoChave.name)
            TipoChave.EMAIL->TipoDeChave.valueOf(tipoChave.name)
            else -> null
        },
        tipoDeConta =   when(tipoConta){
            TipoConta.CONTA_CORRENTE ->TipoDaConta.CONTA_CORRENTE
            TipoConta.CONTA_POUPANCA->TipoDaConta.CONTA_POUPANCA
            else -> null
        }

    )
}

