package miranda.kmanage.grpc.zup.carregachave

import io.grpc.stub.StreamObserver
import miranda.kmanage.grpc.zup.CarregaChaveRequester
import miranda.kmanage.grpc.zup.CarregaChaveResponse
import miranda.kmanage.grpc.zup.CarregaChaveServiceGrpc
import javax.inject.Singleton

@Singleton
class CarregaChaveEndPoint: CarregaChaveServiceGrpc.CarregaChaveServiceImplBase() {

    override fun consulta(request: CarregaChaveRequester?, responseObserver: StreamObserver<CarregaChaveResponse>?) {



    }


}