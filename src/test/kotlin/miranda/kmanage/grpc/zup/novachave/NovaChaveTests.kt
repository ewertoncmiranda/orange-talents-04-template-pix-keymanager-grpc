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
import miranda.kmanage.grpc.zup.*
import miranda.kmanage.grpc.zup.chavepix.ChavePix
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.conta.ContaDoBanco
import miranda.kmanage.grpc.zup.conta.ContaDoBancoRepositorio
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.enum.TipoDeChave
import miranda.kmanage.grpc.zup.exception.ChaveExistenteException
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel.ContaCompletaResponse
import miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel.InstituicaoResponse
import miranda.kmanage.grpc.zup.sistemasexternos.datatransfermodel.TitularResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class NovaChaveTests(
    val clienteGrpc:PixKeymanagerServiceGrpc.PixKeymanagerServiceBlockingStub,
    val repositorio:ChavePixRepositorio,
    val contaDoBancoRepositorio: ContaDoBancoRepositorio) {

    private lateinit var  novoPixRequester:NovaChaveRequester

    @Inject
    lateinit var itauBaseDeDados : ItauBaseDeDados

    companion object {
        val CLIENT_ID = UUID.randomUUID()
    }


    @BeforeEach
    internal  fun  setUp(){
        repositorio.deleteAll()

        novoPixRequester =  NovaChaveRequester
                            .newBuilder()
                            .setChave("43068789885")
                            .setIDcliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                            .setTipoChave(TipoChave.CPF)
                            .setTipoConta(TipoConta.CONTA_CORRENTE).build()

        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(novoPixRequester.iDcliente , TipoDaConta.CONTA_CORRENTE.name))
               .thenReturn(HttpResponse.ok(ContaCompletaResponse("CONTA_CORRENTE"
                                              ,"0001"
                                              ,"12345678"
                                              ,TitularResponse("1bb194-3c52-4e67-8c35-a93c0af9284f","Yuri Matheus" ,"06628726061" )
                                              , InstituicaoResponse("ITAU UNIBANCO S.A" ,"60701190") )))


    }

    @Test
    fun deve_salvar_uma_nova_chave(){

        val response = clienteGrpc.cadastra(novoPixRequester)

        Assertions.assertTrue(repositorio.existsByChave(response.idPix))
        Assertions.assertNotNull(response)
    }
    @Test
    fun nao_deve_registrar_chave_duplicada(){
        repositorio.save(novaChavePix())
        val  novaChaveRequester =NovaChaveRequester
                                    .newBuilder()
                                    .setChave("15372266066")
                                    .setIDcliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                                    .setTipoChave(TipoChave.CPF)
                                    .setTipoConta(TipoConta.CONTA_CORRENTE).build();

          val error = assertThrows<StatusRuntimeException> {
              clienteGrpc.cadastra(novaChaveRequester)
          }
        Assertions.assertEquals(Status.ALREADY_EXISTS.code , error.status.code)
    }
    @Test
    fun nao_deve_registrar_chave_com_valor_diferente_do_tipo(){
        repositorio.save(novaChavePix())
        val  novaChaveRequester =NovaChaveRequester
                                    .newBuilder()
                                    .setChave("15372266066") //CADASTRAR UM CPF
                                    .setIDcliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                                    .setTipoChave(TipoChave.CELULAR) //COM O TIPO CELULAR DEFINIDO
                                    .setTipoConta(TipoConta.CONTA_CORRENTE).build();

        val error = assertThrows<StatusRuntimeException> {
            clienteGrpc.cadastra(novaChaveRequester)
        }
        Assertions.assertEquals(Status.INVALID_ARGUMENT.code , error.status.code)
    }
    @Test
    fun nao_deve_registrar_chave_quando_conta_nao_encontrada(){

        Mockito.`when`(itauBaseDeDados.buscarCLientePorIdEConta(CLIENT_ID.toString(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.notFound())

        val  novaChaveRequester =NovaChaveRequester
            .newBuilder()
            .setChave("15372266066") //CADASTRAR UM CPF
            .setIDcliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CELULAR) //COM O TIPO CELULAR DEFINIDO
            .setTipoConta(TipoConta.CONTA_CORRENTE).build();

        val error = assertThrows<StatusRuntimeException> {  clienteGrpc.cadastra(novaChaveRequester)}
        Assertions.assertEquals(Status.INVALID_ARGUMENT.code , error.status.code)
    }

    @MockBean(ItauBaseDeDados::class)
    fun mockItauBaseDeDados():ItauBaseDeDados?{
        return Mockito.mock(ItauBaseDeDados::class.java)
    }

    //Objeto de Chave Pix
    fun novaChavePix():ChavePix{
       var conta = ContaDoBanco("",TipoDaConta.CONTA_CORRENTE,"Tiago de Freitas","0001","889976","15372266066","")
       contaDoBancoRepositorio.save(conta)

       var chave = ChavePix("bc35591d-b547-4151-a325-4a9d2cd19614",TipoDeChave.CPF,"15372266066",TipoDaConta.CONTA_CORRENTE,conta,)

       return chave;
    }

    //Objeto populado de NovaChaveRequester
    @Factory
    class Clients {
        @Bean
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ):PixKeymanagerServiceGrpc.PixKeymanagerServiceBlockingStub? {
            return  PixKeymanagerServiceGrpc.newBlockingStub(channel)
        }
    }
}