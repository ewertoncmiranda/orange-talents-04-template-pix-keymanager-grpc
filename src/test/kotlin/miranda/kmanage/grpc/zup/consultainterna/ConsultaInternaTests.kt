package miranda.kmanage.grpc.zup.consultainterna

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import miranda.kmanage.grpc.zup.CarregaChaveInternoRequester
import miranda.kmanage.grpc.zup.CarregaChaveInternoServiceGrpc
import miranda.kmanage.grpc.zup.DeletaChaveRequester
import miranda.kmanage.grpc.zup.carregachaveinterno.CarregaChaveInternoService
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
class ConsultaInternaTests (
            val grpc:CarregaChaveInternoServiceGrpc.CarregaChaveInternoServiceBlockingStub,
            val repositorio: ChavePixRepositorio
            ){

    /*Criação de variaveis e funções de mocking*/
    @field:Inject
    lateinit var itauBaseDeDados: ItauBaseDeDados

    @MockBean(ItauBaseDeDados::class)
    fun itauBaseDeDados(): ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }

    /*Criação de classe de Factory*/
     @Factory
     class Client{
         @Bean
         fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME)channel: ManagedChannel):
                                        CarregaChaveInternoServiceGrpc.CarregaChaveInternoServiceBlockingStub{
             return  CarregaChaveInternoServiceGrpc.newBlockingStub(channel)
         }
     }

    /*Configuração dos Testes-JUnit*/

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

    /*Testes  */
    @Test
    fun nao_deve_listar_se_chaveexiste_mas_pertence_outro_usuario(){
        /*Preparação - Cenário*/
        repositorio.save(  ChavePix(
                tipoDaChave = TipoDeChave.CPF,
                chave = "94720753043",
                clientId = "2ac09233-21b2-4276-84fb-d83dbd9f8bab",
                tipoConta = TipoDaConta.CONTA_CORRENTE,
                conta = ContaDoBanco("ITAÚ UNIBANCO S.A.",
                    "Cassio Almeida",
                    "0001",
                    "084329",
                    "94720753043")
            )
        )
        val response = assertThrows<StatusRuntimeException> {
                  grpc.consulta(
                        CarregaChaveInternoRequester
                                        .newBuilder()
                                        .setIdPix("1")
                                        .setIdClient("ae93a61c-0642-43b3-bb8e-a17072295955")
                                        .build()
                  )
        }
        with(response){
            println(response.status)
            Assertions.assertEquals(Status.FAILED_PRECONDITION.code ,response.status.code)
        }
    }

    @Test
    fun deve_listar_dados_da_chave(){

        val response = grpc.consulta(carregaChaveInterno())

        with(response){
            println(response)
            Assertions.assertNotNull(response)
            Assertions.assertEquals("rponte@gmail.com",response.chave)
            Assertions.assertEquals(CHAVE_EXISTENTE.chave,response.chave)
        }

    }

    @Test
    fun nao_deve_listar_se_chave_nao_existe(){
        val response = assertThrows<StatusRuntimeException> {
            grpc.consulta(
                CarregaChaveInternoRequester.newBuilder()
                .setIdClient("2ac09233-21b2-4276-84fb-d83dbd9f8bab")
                .setIdPix("10").build())
        }
        with(response){
            println(response.status)
            Assertions.assertEquals(Status.NOT_FOUND.code,response.status.code)
        }
    }

    fun carregaChaveInterno():CarregaChaveInternoRequester{
      return  CarregaChaveInternoRequester
                    .newBuilder()
                    .setIdPix("1")
                    .setIdClient("2ac09233-21b2-4276-84fb-d83dbd9f8bab").build()
    }

}