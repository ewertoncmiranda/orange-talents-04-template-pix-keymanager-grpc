package miranda.kmanage.grpc.zup.carregachavebcb

import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.CarregaDadosBcbResponse
import miranda.kmanage.grpc.zup.carregachaveinterno.converter.CarregaChaveConverter
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.exception.ChaveNaoEncontradaException
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import javax.inject.Singleton
import javax.validation.constraints.Size

@Singleton
@Validated
class CarregaChaveBcbService(val bcbClient:SistemaBancoCentralBrasil,
                             val repositorio: ChavePixRepositorio){

    fun carrega(@Size(max = 77 )chave:String): CarregaDadosBcbResponse
    {
        //Procura chave no repositorio local
        val optionalChave = repositorio.findByChave(chave)
            if(optionalChave.isPresent){
                return CarregaChaveConverter().convertChaveInternaFromBcb(optionalChave.get())
            }
        //Procura chave no BCB
        val httpResponse = bcbClient.procurar(chave)

            if(httpResponse.status != HttpStatus.OK){
                throw ChaveNaoEncontradaException("Chave nao Encontrada")
            }
        return CarregaChaveConverter().convertChaveBcbFromBcb(httpResponse.body())
    }
}