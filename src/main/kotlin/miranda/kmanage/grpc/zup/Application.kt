package miranda.kmanage.grpc.zup

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("miranda.kmanage.grpc.zup")
		.start()
}

