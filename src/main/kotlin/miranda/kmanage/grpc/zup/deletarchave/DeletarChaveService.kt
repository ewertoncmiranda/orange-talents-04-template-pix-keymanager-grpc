package miranda.kmanage.grpc.zup.deletarchave

import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.exception.*
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.DeletePixKeyRequest
import miranda.kmanage.grpc.zup.validacao.ValidarUUID
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull


@Singleton
@Validated
class DeletarChaveService(@Inject @field:NotNull val repositorio: ChavePixRepositorio,
                          @Inject @field:NotNull val bancoCentralBrasil: SistemaBancoCentralBrasil) {

    fun deleta( @NotNull idPix:Long ,
                @NotNull idCliente:String): Boolean{

        if(!repositorio.existsById(idPix)){
            throw ChaveNaoEncontradaException("Chave Pix não encontrada!")
        }

        val chave = repositorio.buscarPorIdEChave(idPix,idCliente)

        if(chave.isEmpty) { throw ChaveNaoPertenceAoUsuarioException("Chave informada nao pertence ao usuario informado!")}

        val chaveResponse = chave.get().chave

        val response = bancoCentralBrasil.delete(DeletePixKeyRequest
                                                (chaveResponse,
                                                "60701190"),chaveResponse)

        return if(response.status == HttpStatus.OK){
                    repositorio.deleteById(idPix)
                    true
                }else {
                    throw ErroAoDeletarChaveNoBcbException("Erro ao deletar chave no BCB!")
                }
    }
}