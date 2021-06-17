package miranda.kmanage.grpc.zup.exception

import java.lang.RuntimeException

class ChaveExistenteException(override val message:String):RuntimeException(message) {
}