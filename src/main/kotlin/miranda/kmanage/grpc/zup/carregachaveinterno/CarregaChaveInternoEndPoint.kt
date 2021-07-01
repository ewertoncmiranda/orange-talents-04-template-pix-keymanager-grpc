package miranda.kmanage.grpc.zup.carregachaveinterno

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.CarregaChaveInternoRequester
import miranda.kmanage.grpc.zup.CarregaChaveInternoServiceGrpc
import miranda.kmanage.grpc.zup.CarregaDadosInternoResponse
import miranda.kmanage.grpc.zup.exception.ErrorGeralHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorGeralHandler
class CarregaChaveInternoEndPoint(@Inject val service: CarregaChaveInternoService)
                            : CarregaChaveInternoServiceGrpc.CarregaChaveInternoServiceImplBase(){

    override fun consulta(
        request: CarregaChaveInternoRequester?,
        responseObserver: StreamObserver<CarregaDadosInternoResponse>?
    ) {
        val idClient = request!!.idClient
        val idPix = request.idPix.toLong()

        responseObserver!!.onNext(service.carrega(idClient,idPix))

        responseObserver!!.onCompleted()

    }
}