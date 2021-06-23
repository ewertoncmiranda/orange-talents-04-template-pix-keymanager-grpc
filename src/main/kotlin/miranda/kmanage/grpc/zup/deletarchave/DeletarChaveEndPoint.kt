package miranda.kmanage.grpc.zup.deletarchave

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.DeletaChaveRequester
import miranda.kmanage.grpc.zup.DeletaChaveResponse
import miranda.kmanage.grpc.zup.DeletarChaveServiceGrpc
import miranda.kmanage.grpc.zup.exception.ErrorGeralHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorGeralHandler
class DeletarChaveEndPoint (@Inject val deleteService: DeletarChaveService):DeletarChaveServiceGrpc.DeletarChaveServiceImplBase(){

    override fun exclui(request: DeletaChaveRequester?, responseObserver: StreamObserver<DeletaChaveResponse>?) {

        responseObserver!!.onNext(DeletaChaveResponse.newBuilder()
                          .setDeletado(deleteService.deleta(request!!.idPix,request.clienteId))
                          .build())
        responseObserver.onCompleted()

    }
}