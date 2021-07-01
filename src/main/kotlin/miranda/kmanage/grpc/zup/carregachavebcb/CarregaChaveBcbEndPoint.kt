package miranda.kmanage.grpc.zup.carregachavebcb

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.CarregaChaveBcbRequester
import miranda.kmanage.grpc.zup.CarregaChaveBcbServiceGrpc
import miranda.kmanage.grpc.zup.CarregaDadosBcbResponse
import miranda.kmanage.grpc.zup.exception.ErrorGeralHandler
import javax.inject.Singleton

@Singleton
@ErrorGeralHandler
class CarregaChaveBcbEndPoint(val service: CarregaChaveBcbService)
                    :CarregaChaveBcbServiceGrpc.CarregaChaveBcbServiceImplBase() {

    override fun consulta(
        request: CarregaChaveBcbRequester?,
        responseObserver: StreamObserver<CarregaDadosBcbResponse>?
    ) {
        val chave = request!!.chave

        responseObserver!!.onNext( service.carrega(chave))
        responseObserver.onCompleted()

    }
}