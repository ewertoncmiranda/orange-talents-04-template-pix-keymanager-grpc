package miranda.kmanage.grpc.zup.novachave

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import miranda.kmanage.grpc.zup.NovaChaveResponse
import miranda.kmanage.grpc.zup.chavepix.ChavePixRepositorio
import miranda.kmanage.grpc.zup.enum.TipoDaConta
import miranda.kmanage.grpc.zup.exception.ChaveJaCadastradaException
import miranda.kmanage.grpc.zup.exception.ClienteNaoCadastradoNoBancoException
import miranda.kmanage.grpc.zup.exception.FalhaAoRegistrarNoBcbException
import miranda.kmanage.grpc.zup.sistemasexternos.ItauBaseDeDados
import miranda.kmanage.grpc.zup.sistemasexternos.SistemaBancoCentralBrasil
import org.slf4j.LoggerFactory
import java.lang.Exception
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class NovaChaveService(
    private val chavePixRepositorio: ChavePixRepositorio,
    private val itauBaseDeDados: ItauBaseDeDados,
    private val bcbClient:SistemaBancoCentralBrasil

) {
    fun cadastrar(@Valid novaChavePix: NovaChavePix): NovaChaveResponse{

        if(chavePixRepositorio.existsByChave(novaChavePix.chave!!)) throw ChaveJaCadastradaException("Chave ${novaChavePix.chave} ja cadastrada")

        //Busca no sistema do Itau um idCliente e um tipo de conta
        val response = itauBaseDeDados.buscarCLientePorIdEConta(novaChavePix.clienteId.toString(),novaChavePix.tipoDeConta!!.name)

        //verifica se o cliente foi encontrado no sistema Itau
        if(response.status.code == HttpStatus.NOT_FOUND.code) { throw ClienteNaoCadastradoNoBancoException("Cliente não encontrado no banco.")}

        //recebe a conta vinda do erp itau , passa para novaChave.toModel e retorna uma ChavePix
        var chavepix = novaChavePix.toModel(response.body()!!)

        val teste = chavepix.toBcbModel()

        val resposta = bcbClient.cadastrar(teste)

        if (resposta.status.code != HttpStatus.CREATED.code){
            throw FalhaAoRegistrarNoBcbException("Erro ao registrar a chave no Banco Central.")
        }

        chavepix.chave =  resposta.body()!!.key
        chavepix = chavePixRepositorio.save(chavepix)

        return NovaChaveResponse.newBuilder()
                                .setClienteId(chavepix.clientId.toString())
                                .setIdPix(chavepix.chave).build()

    }

    }
