package miranda.kmanage.grpc.zup.sistemasexternos

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.ContaCompletaResponse
import miranda.kmanage.grpc.zup.sistemasexternos.itaudto.TitularResponse

@Client("http://localhost:9091")
open interface ItauBaseDeDados {

    @Get("/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscarCLientePorIdEConta (@PathVariable idCliente:String, @QueryValue tipo:String): HttpResponse<ContaCompletaResponse>

    @Get("/api/v1/clientes/{idCliente}")
    fun buscarClientePeloId(@PathVariable idCliente: String):HttpResponse<TitularResponse>

}