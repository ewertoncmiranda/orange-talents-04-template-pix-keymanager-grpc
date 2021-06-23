package miranda.kmanage.grpc.zup.sistemasexternos.bcbdto

data class CreatePixKeyRequest(val keyType:KeyType,
                               val key:String,
                               val owner:Owner,
                               val bankAccount: BankAccount)

