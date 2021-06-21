package miranda.kmanage.grpc.zup.deletarchave

import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.exception.ChaveNaoEncontradaException
import miranda.kmanage.grpc.zup.exception.ChaveNaoPertenceAoUsuarioException
import miranda.kmanage.grpc.zup.exception.ClienteNaoCadastradoNoBancoException
import miranda.kmanage.grpc.zup.exception.MinhaException
import miranda.kmanage.grpc.zup.validacao.ValidarUUID
import javax.inject.Singleton
import javax.validation.constraints.NotNull


@Singleton
@Validated
class DeletarChaveService( val repositorio: ChavePixRepositorio) {

    fun deleta(@ValidarUUID @NotNull idPix:Long ,
               @ValidarUUID @NotNull idCliente:String): Boolean{

        if(!repositorio.existsById(idPix)){
            throw ChaveNaoEncontradaException("Chave Pix não encontrada!")
        }

        val chaveResponse = repositorio.buscarPorIdEChave(idPix,idCliente)

        return if(chaveResponse.isPresent){
                 repositorio.deleteById(idPix)
                 true
             }else {
                throw ChaveNaoPertenceAoUsuarioException("Chave informada nao pertence ao usuario informado!")
                false
             }
    }
}