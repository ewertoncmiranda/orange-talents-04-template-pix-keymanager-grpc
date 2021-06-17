package miranda.kmanage.grpc.zup.novachave

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.NovaChaveRequester
import miranda.kmanage.grpc.zup.NovaChaveResponse
import miranda.kmanage.grpc.zup.PixKeymanagerServiceGrpc
import miranda.kmanage.grpc.zup.uti.toModel
import miranda.kmanage.grpc.zup.validacao.ErrorGeralHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorGeralHandler
class NovaChaveEndPoint (@Inject val service: NovaChaveService): PixKeymanagerServiceGrpc.PixKeymanagerServiceImplBase(){

    override fun cadastra(request: NovaChaveRequester,
                          responseObserver: StreamObserver<NovaChaveResponse>?) {

         val novaChavePixRequest = request.toModel()

        responseObserver!!.onNext(service.cadastrar(novaChavePixRequest ))
        responseObserver.onCompleted()

    }
}