package miranda.kmanage.grpc.zup.novachave

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.NovaChaveRequester
import miranda.kmanage.grpc.zup.PixKeymanagerServiceGrpc
import miranda.kmanage.grpc.zup.TipoChave
import miranda.kmanage.grpc.zup.TipoConta
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.*
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.ContaCompletaResponse
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.InstituicaoResponse
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.TitularResponse
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class NovaChaveEndPointTests(val grpc: PixKeymanagerServiceGrpc.PixKeymanagerServiceBlockingStub,
                                      val repositorio: ChavePixRepositorio) {

    @field:Inject lateinit var bcbCliente:SistemaBancoCentralBrasil

    @MockBean(SistemaBancoCentralBrasil::class)
    fun bcbClient():SistemaBancoCentralBrasil?{
        return Mockito.mock(SistemaBancoCentralBrasil::class.java)
    }

    @field:Inject lateinit var itauBaseDeDados: ItauBaseDeDados

    lateinit var  novaChaveRequester: NovaChaveRequester

    @MockBean(ItauBaseDeDados::class)
    fun itauBaseDeDados():ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel): PixKeymanagerServiceGrpc.PixKeymanagerServiceBlockingStub {
            return PixKeymanagerServiceGrpc.newBlockingStub(channel) }
    }
    @AfterEach
    fun cleanUp(){ repositorio.deleteAll()   }

    @BeforeEach
    fun setup(){

       novaChaveRequester = NovaChaveRequester.newBuilder()
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setTipoChave(TipoChave.CPF)
            .setChave("13319881795")
            .setIDcliente(UUID.randomUUID().toString())
            .build()
    }

    @Test
    fun deve_registrar_uma_nova_chave(){

    Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))

    Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(novaChaveRequester.iDcliente,novaChaveRequester.tipoConta.name))
            .thenReturn(HttpResponse.ok(contaCompletaResponse()))

     val response = grpc.cadastra(novaChaveRequester)
        with(response){
            Assertions.assertNotNull(response.idPix)
        }
    }

    @Test
    fun nao_deve_registrar_uma_chave_quando_chave_existir(){
        repositorio.save(ChavePix(UUID.randomUUID().toString(),TipoDeChave.CPF,"13319881795",TipoDaConta.CONTA_CORRENTE,
                        ContaDoBanco("","","","","13319881795")
        ))

        Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))

        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(novaChaveRequester.iDcliente,novaChaveRequester.tipoConta.name))
            .thenReturn(HttpResponse.ok(contaCompletaResponse()))

        val response = assertThrows<StatusRuntimeException> {grpc.cadastra(novaChaveRequester)   }

        with(response){
          Assertions.assertEquals(Status.ALREADY_EXISTS.code,response.status.code)
        }
    }

    @Test
    fun nao_deve_registrar_quando_nao_encontrar_cliente_no_erp_itau(){
        Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))

        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(novaChaveRequester.iDcliente,novaChaveRequester.tipoConta.name))
            .thenReturn(HttpResponse.notFound())

        val response = assertThrows<StatusRuntimeException> {grpc.cadastra(novaChaveRequester)   }

        with(response){
            Assertions.assertEquals(Status.NOT_FOUND.code,response.status.code)
        }

    }

    @Test
    fun nao_deve_registrar_quando_nao_conseguir_registrar_no_bcb(){
        Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
            .thenReturn(HttpResponse.badRequest())

        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(novaChaveRequester.iDcliente,novaChaveRequester.tipoConta.name))
            .thenReturn(HttpResponse.ok(contaCompletaResponse()))

        val response = assertThrows<StatusRuntimeException> {grpc.cadastra(novaChaveRequester)   }

        with(response){
            Assertions.assertEquals(Status.FAILED_PRECONDITION.code,response.status.code)
        }
    }

    @Test
    fun nao_deve_registrar_quando_cpf_invalido(){
        val response = assertThrows<StatusRuntimeException> {
            grpc.cadastra(
                NovaChaveRequester.newBuilder()
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .setTipoChave(TipoChave.CPF)
                .setChave("10000000000")
                .setIDcliente(UUID.randomUUID().toString())
                .build())
        }

        with(response){
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,response.status.code)
        }
    }

    @Test
    fun nao_deve_registrar_quando_tipochave_diferente_chave(){
        Mockito.`when`(bcbCliente.cadastrar(createPixKeyRequester()))
            .thenReturn(HttpResponse.badRequest())

        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(novaChaveRequester.iDcliente,novaChaveRequester.tipoConta.name))
            .thenReturn(HttpResponse.ok(contaCompletaResponse()))

        val response = assertThrows<StatusRuntimeException> {
                    grpc.cadastra(
                        NovaChaveRequester.newBuilder()
                                .setTipoConta(TipoConta.CONTA_CORRENTE)
                                .setTipoChave(TipoChave.EMAIL)
                                .setChave("40049282069")
                                .setIDcliente(UUID.randomUUID().toString())
                                .build())  }

        with(response){
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,response.status.code)
        }
    }

    fun createPixKeyResponse():PixKeyDetailsResponse{
        return PixKeyDetailsResponse(KeyType.CPF,"13319881795",
            Owner(Type.NATURAL_PERSON,"Rafael M C Ponte","c56dfef4-7901-44fb-84e2-a2cefb157890"),
            BankAccount("60701190","0001","291900",AccountType.CACC),"789456")
    }

    fun createPixKeyRequester():CreatePixKeyRequest{
        return CreatePixKeyRequest(KeyType.CPF,"13319881795",
                    Owner(Type.NATURAL_PERSON,"Rafael M C Ponte","02467781054"),
                    BankAccount("60701190","0001","291900",AccountType.CACC)
        )
    }

    fun contaCompletaResponse():ContaCompletaResponse{
        return  ContaCompletaResponse("CONTA_CORRENTE","0001","291900",
                TitularResponse(
                                "c56dfef4-7901-44fb-84e2-a2cefb157890",
                                "Rafael M C Ponte",
                                "02467781054"),
                InstituicaoResponse("ITAÚ UNIBANCO S.A","60701190")
        )
    }


}