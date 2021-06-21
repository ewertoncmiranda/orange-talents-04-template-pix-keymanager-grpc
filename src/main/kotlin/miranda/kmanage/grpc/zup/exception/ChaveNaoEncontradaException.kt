package miranda.kmanage.grpc.zup.exception

import java.lang.RuntimeException

class ChaveNaoEncontradaException(override val message:String):RuntimeException(message) {
}