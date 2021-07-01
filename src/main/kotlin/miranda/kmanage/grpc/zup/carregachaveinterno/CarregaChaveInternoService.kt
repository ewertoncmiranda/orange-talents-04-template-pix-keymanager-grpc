package miranda.kmanage.grpc.zup.carregachaveinterno

import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.CarregaDadosInternoResponse
import miranda.kmanage.grpc.zup.carregachaveinterno.converter.CarregaChaveConverter
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.exception.ChaveNaoEncontradaException
import miranda.kmanage.grpc.zup.exception.ChaveNaoPertenceAoUsuarioException
import miranda.kmanage.grpc.zup.validacao.ValidarUUID
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Singleton
@Validated
class CarregaChaveInternoService (@Inject val repositorio: ChavePixRepositorio ){

    fun carrega(@ValidarUUID idClient: String,
                    @NotNull idPix: Long  ): CarregaDadosInternoResponse {

        if(!repositorio.existsById(idPix)){throw ChaveNaoEncontradaException("Chave nao encontrada!")}

        //o id da chave deve pertencer ao cliente
        val possivelChave = repositorio.buscarPorIdEChave(idPix,idClient)

        if (possivelChave.isEmpty){throw ChaveNaoPertenceAoUsuarioException("chave não pertence ao cliente!")}

        //a chave deve estar devidamente cadastrada no bacen
        //ora, se a chave esta salva em nosso sistema, ela foi salva primeiro no bacen ._.
        return CarregaChaveConverter().convertChaveInternaFromInterna(possivelChave.get())

    }


}