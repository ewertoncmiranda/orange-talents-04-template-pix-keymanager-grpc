package miranda.kmanage.grpc.zup.novachave

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.*
import miranda.kmanage.grpc.zup.deletarchave.DeletarChaveService
import miranda.kmanage.grpc.zup.novachave.NovaChaveService
import miranda.kmanage.grpc.zup.uti.toModel
import miranda.kmanage.grpc.zup.validacao.ErrorGeralHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorGeralHandler
class NovaChaveEndPoint (@Inject val service: NovaChaveService,
                         @Inject val deleteService:DeletarChaveService) : PixKeymanagerServiceGrpc.PixKeymanagerServiceImplBase(){

    override fun cadastra(request: NovaChaveRequester,
                          responseObserver: StreamObserver<NovaChaveResponse>?) {
         val novaChavePixRequest = request.toModel()
        responseObserver!!.onNext(service.cadastrar(novaChavePixRequest ))
        responseObserver.onCompleted()
    }

}