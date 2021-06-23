package miranda.kmanage.grpc.zup.sistemasexternos.bcbdto

data class BankAccount(val participant:String,
                       val branch:String ,
                       val accountNumber:String,
                       val accountType: AccountType)