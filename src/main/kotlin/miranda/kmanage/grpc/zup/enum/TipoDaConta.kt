package miranda.kmanage.grpc.zup.enum

import miranda.kmanage.grpc.zup.TipoConta
import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.AccountType

enum class TipoDaConta {
    CONTA_CORRENTE {
        override fun toBcbType(): AccountType {
           return  AccountType.CACC
        }
        override fun toProtoType(): TipoConta {
         return TipoConta.CONTA_CORRENTE
        }
    },
    CONTA_POUPANCA {
        override fun toBcbType(): AccountType {
            return  AccountType.SVGS
        }
        override fun toProtoType(): TipoConta {
            return TipoConta.CONTA_POUPANCA
        }
    };

    abstract fun toBcbType():AccountType
    abstract fun toProtoType():TipoConta
}