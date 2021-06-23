package miranda.kmanage.grpc.zup

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.*
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.ContaCompletaResponse
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.InstituicaoResponse
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.TitularResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class NovaChaveEndPointTests(val grpc:PixKeymanagerServiceGrpc.PixKeymanagerServiceBlockingStub) {

    @Inject lateinit var bcbCliente:SistemaBancoCentralBrasil

    @Inject lateinit var itauBaseDeDados: ItauBaseDeDados

    @MockBean(SistemaBancoCentralBrasil::class)
    fun bcbClient():SistemaBancoCentralBrasil?{
        return Mockito.mock(SistemaBancoCentralBrasil::class.java)
    }
    @MockBean(ItauBaseDeDados::class)
    fun itauBaseDeDados():ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }
    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel):PixKeymanagerServiceGrpc.PixKeymanagerServiceBlockingStub{
            return PixKeymanagerServiceGrpc.newBlockingStub(channel)
    }  }

    @Test
    fun deve_registrar_uma_nova_chave(){

      Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(UUID.randomUUID().toString(),"CONTA_CORRENTE"))
           .thenReturn(HttpResponse.ok(contaCompletaResponse()))

       Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
              .thenReturn(HttpResponse.created(createPixKeyResponse()))

     val response = grpc.cadastra(NovaChaveRequester.newBuilder()
                        .setTipoConta(TipoConta.CONTA_CORRENTE)
                        .setTipoChave(TipoChave.CPF)
                        .setChave("13319881795")
                        .setIDcliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                        .build())
        with(response){
            Assertions.assertNotNull(response.)
        }
    }




    fun createPixKeyResponse():CreatePixKeyResponse{
        return CreatePixKeyResponse(KeyType.CPF,"13319881795",
            Owner(Type.NATURAL_PERSON,"Rafael M C Ponte","c56dfef4-7901-44fb-84e2-a2cefb157890"),
            BankAccount("60701190","0001","291900",AccountType.CACC),"789456")
    }

    fun createPixKeyRequester():CreatePixKeyRequest{
        return CreatePixKeyRequest(KeyType.CPF,"13319881795",
                    Owner(Type.NATURAL_PERSON,"Rafael M C Ponte","c56dfef4-7901-44fb-84e2-a2cefb157890"),
            BankAccount("60701190","0001","291900",AccountType.CACC)
        )
    }
    fun contaCompletaResponse():ContaCompletaResponse{
        return  ContaCompletaResponse("CONTA_CORRENTE","0001","291900",
                TitularResponse("c56dfef4-7901-44fb-84e2-a2cefb157890",
                                "Rafael M C Ponte",
                                "02467781054"),
                InstituicaoResponse("ITAÚ UNIBANCO S.A","60701190")
        )
    }


}