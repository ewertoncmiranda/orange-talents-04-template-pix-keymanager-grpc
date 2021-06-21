package miranda.kmanage.grpc.zup.exception

import java.lang.RuntimeException

class ChaveJaCadastradaException(override val message:String):RuntimeException(message) {
}