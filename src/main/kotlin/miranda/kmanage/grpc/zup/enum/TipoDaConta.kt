package miranda.kmanage.grpc.zup.enum

import miranda.kmanage.grpc.zup.sistemasexternos.bcbdto.AccountType

enum class TipoDaConta {
    CONTA_CORRENTE {
        override fun toBcbType(): AccountType {
           return  AccountType.CACC
        }
    },
    CONTA_POUPANCA {
        override fun toBcbType(): AccountType {
            return  AccountType.SVGS
        }
    };

    abstract fun toBcbType():AccountType
}