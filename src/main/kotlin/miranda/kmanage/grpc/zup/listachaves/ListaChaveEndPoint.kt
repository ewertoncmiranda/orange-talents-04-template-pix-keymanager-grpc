package miranda.kmanage.grpc.zup.listachaves

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.ListaChavesPixRequest
import miranda.kmanage.grpc.zup.ListaChavesPixResponse
import miranda.kmanage.grpc.zup.ListaTodasChavesClientServiceGrpc
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import java.lang.IllegalArgumentException
import javax.inject.Singleton

@Singleton
class ListaChaveEndPoint(val repositorio: ChavePixRepositorio)
                :ListaTodasChavesClientServiceGrpc.ListaTodasChavesClientServiceImplBase() {

    override fun listar(
        request: ListaChavesPixRequest,
        responseObserver: StreamObserver<ListaChavesPixResponse>
    ) {

    if(request.clientId.isNullOrBlank()){
        throw IllegalArgumentException("Clienta não pode ser nulo ou vazio!")
    }

        val clientId = request.clientId

        val chaves = repositorio.findAllByClientId(clientId).map {
            ListaChavesPixResponse.ChavePix.newBuilder()
                .setCriadaEm(it.criadaEm.toString())
                .setChave(it.chave)
                .setPixId(it.id.toString())
                .setTipoChave(it.tipoDaChave.toProtoType())
                .setTipoConta(it.tipoConta.toProtoType() ).build()
        }

        responseObserver.onNext(ListaChavesPixResponse.newBuilder()
                                                        .setClientId(clientId)
                                                        .addAllListaDeChaves(chaves)
                                                        .build())
        responseObserver.onCompleted()


    }
}