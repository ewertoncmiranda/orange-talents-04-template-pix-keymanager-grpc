package miranda.kmanage.grpc.zup.sistemasexternos

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.CreatePixKeyRequest
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.PixKeyDetailsResponse
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.DeletePixKeyRequest

@Client("http://localhost:8082")
interface SistemaBancoCentralBrasil {

    @Post("/api/v1/pix/keys" ,produces = [MediaType.APPLICATION_XML]
         , consumes = [MediaType.APPLICATION_XML])
    fun cadastrar(@Body request:CreatePixKeyRequest):HttpResponse<PixKeyDetailsResponse>

    @Delete("/api/v1/pix/keys/{chave}" ,produces = [MediaType.APPLICATION_XML]
        , consumes = [MediaType.APPLICATION_XML])
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun delete(@Body request: DeletePixKeyRequest, @PathVariable chave:String):HttpResponse<Any>

    @Get("/api/v1/pix/keys/{chave}",consumes = [MediaType.APPLICATION_XML])
    fun procurar(@PathVariable chave:String):HttpResponse<PixKeyDetailsResponse>



}