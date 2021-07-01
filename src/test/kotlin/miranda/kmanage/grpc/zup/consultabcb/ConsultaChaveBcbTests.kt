package miranda.kmanage.grpc.zup.consultabcb

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.CarregaChaveBcbRequester
import miranda.kmanage.grpc.zup.CarregaChaveBcbServiceGrpc
import miranda.kmanage.grpc.zup.CarregaChaveInternoRequester
import miranda.kmanage.grpc.zup.CarregaChaveInternoServiceGrpc
import miranda.kmanage.grpc.zup.carregachavebcb.CarregaChaveBcbService
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.*
import org.junit.jupiter.api.*
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest(transactional = false)
class ConsultaChaveBcbTests(
            val grcp:CarregaChaveBcbServiceGrpc.CarregaChaveBcbServiceBlockingStub,
            val repositorio: ChavePixRepositorio
            ){

    /*Criação de variaveis e funções de mocking*/
    @field:Inject
    lateinit var itauBaseDeDados: ItauBaseDeDados

    @MockBean(ItauBaseDeDados::class)
    fun itauBaseDeDados(): ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }

    @field:Inject
    lateinit var bcbClient:SistemaBancoCentralBrasil

    @MockBean(SistemaBancoCentralBrasil::class)
    fun bcbClient():SistemaBancoCentralBrasil?{
        return  Mockito.mock(SistemaBancoCentralBrasil::class.java)
    }

    /*Criação de classe de Factory*/
    @Factory
    class Client{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel):
                CarregaChaveBcbServiceGrpc.CarregaChaveBcbServiceBlockingStub{
            return  CarregaChaveBcbServiceGrpc.newBlockingStub(channel)
        }
    }

    /*Configuração dos Testes / JUnit*/
    private lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repositorio.save(
            ChavePix(
                tipoDaChave = TipoDeChave.EMAIL,
                chave = "rponte@gmail.com",
                clientId = "2ac09233-21b2-4276-84fb-d83dbd9f8bab",
                tipoConta = TipoDaConta.CONTA_CORRENTE,
                conta = ContaDoBanco("ITAÚ UNIBANCO S.A.",
                    "Cassio Almeida",
                    "0001",
                    "084329",
                    "02467781054")
            )
        )
    }

    @AfterEach
    fun cleanUp(){
        repositorio.deleteAll()
    }

    @Test
    fun deve_listar_dados_da_chave(){

        Mockito.`when`(bcbClient.procurar("rponte@gmail.com"))
               .thenReturn(HttpResponse.ok(createPixKeyResponse()))

        val response = grcp.consulta(carregaChaveBcb())
        with(response){
            Assertions.assertNotNull(response)
            Assertions.assertEquals("rponte@gmail.com",response.chave)
            Assertions.assertEquals(CHAVE_EXISTENTE.chave,response.chave)
        }
    }

    @Test
    fun nao_deve_listar_se_chave_nao_existe(){

        Mockito.`when`(bcbClient.procurar("teste"))
              .thenReturn(HttpResponse.notFound())

        val response = assertThrows<StatusRuntimeException> {
                      grcp.consulta(CarregaChaveBcbRequester.newBuilder()
                                     .setChave("teste").build()) }
        with(response){
            Assertions.assertEquals(Status.NOT_FOUND.code,response.status.code)
        }
    }


    fun carregaChaveBcb():CarregaChaveBcbRequester{
        return CarregaChaveBcbRequester.newBuilder()
                                    .setChave("rponte@gmail.com").build()
    }

    fun createPixKeyResponse(): PixKeyDetailsResponse {
        return PixKeyDetailsResponse(
            KeyType.CPF,"13319881795",
            Owner(Type.NATURAL_PERSON,"Rafael M C Ponte","c56dfef4-7901-44fb-84e2-a2cefb157890"),
            BankAccount("60701190","0001","291900", AccountType.CACC),"789456")
    }


}