package miranda.kmanage.grpc.zup.novachave

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.*
import miranda.kmanage.grpc.zup.deletarchave.DeletarChaveService
import miranda.kmanage.grpc.zup.exception.ErrorGeralHandler
import miranda.kmanage.grpc.zup.uti.toModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovaChaveEndPoint (@Inject val service: NovaChaveService) : PixKeymanagerServiceGrpc.PixKeymanagerServiceImplBase(){

    override fun cadastra(request: NovaChaveRequester,
                          responseObserver: StreamObserver<NovaChaveResponse>?) {
         val novaChavePixRequest = request.toModel()
        responseObserver!!.onNext(service.cadastrar(novaChavePixRequest ))
        responseObserver.onCompleted()
    }

}