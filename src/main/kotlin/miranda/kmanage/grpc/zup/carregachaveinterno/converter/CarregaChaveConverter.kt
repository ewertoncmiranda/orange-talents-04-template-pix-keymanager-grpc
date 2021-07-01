package miranda.kmanage.grpc.zup.carregachaveinterno.converter

import miranda.kmanage.grpc.zup.CarregaDadosBcbResponse
import miranda.kmanage.grpc.zup.CarregaDadosInternoResponse
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.PixKeyDetailsResponse

class CarregaChaveConverter {

    fun convertChaveInternaFromInterna(chave:ChavePix):CarregaDadosInternoResponse{
        return CarregaDadosInternoResponse.newBuilder()
            .setTipoChave(chave.tipoDaChave.toProtoType())
            .setChave(chave.chave)
            .setCriadaEm(chave.criadaEm.toString())
            .setClientId(chave.clientId)
            .setPixId(chave.id.toString())
            .setContaInfo(
                CarregaDadosInternoResponse.ContaInfo.newBuilder()
                    .setTipoConta(chave.tipoConta.toProtoType())
                    .setAgencia(chave.conta!!.agencia)
                    .setNomeDoTitular(chave.conta!!.nomeDoTitular)
                    .setInstituicao(chave.conta!!.instituicao)
                    .setNumeroConta(chave.conta!!.numeroConta)
                    .setCpf(chave.conta!!.cpf)
                    .build()
                )
            .build()
    }

    fun convertChaveBcbFromBcb(pix: PixKeyDetailsResponse): CarregaDadosBcbResponse {
        return CarregaDadosBcbResponse.newBuilder()
            .setTipoChave(pix.keyType.toProtoType())
            .setChave(pix.key)
            .setCriadaEm(pix.createdAt)
            .setContaInfo(
                CarregaDadosBcbResponse.ContaInfo.newBuilder()
                    .setTipoConta(pix.bankAccount.accountType.toProtoType())
                    .setAgencia(pix.bankAccount.branch)
                    .setNomeDoTitular(pix.owner.name)
                    .setNumeroConta(pix.bankAccount.accountNumber)
                    .setCpf(pix.owner.taxIdNumber)
                    .build()
            )
            .build()
    }

    fun convertChaveInternaFromBcb(chave:ChavePix):CarregaDadosBcbResponse {
        return CarregaDadosBcbResponse .newBuilder()
            .setTipoChave(chave.tipoDaChave.toProtoType())
            .setChave(chave.chave)
            .setCriadaEm(chave.criadaEm.toString())
            .setContaInfo(
                CarregaDadosBcbResponse .ContaInfo.newBuilder()
                    .setTipoConta(chave.tipoConta.toProtoType())
                    .setAgencia(chave.conta!!.agencia)
                    .setNomeDoTitular(chave.conta!!.nomeDoTitular)
                    .setInstituicao(chave.conta!!.instituicao)
                    .setNumeroConta(chave.conta!!.numeroConta)
                    .setCpf(chave.conta!!.cpf)
                    .build()
            )
            .build()
    }
}