package app

fun main() {

    val myClass = MyClass() // Instancia-se a classe original

    // Instancia-se a classe Wrapper, gerada pelo GreetingProcessor.kt
    // O construtor dela espera por receber a instância original pelo argumento que nomeado de "original"
    val wrappedMyClass = MyClassWrapper(myClass)

    // Invoca-se os métodos da classe Wrapper
    // O que vai acontecer :
    // - O Wrapper faz o print de @Greeting("...")
    // - O Wrapper invoca a seguir myClass.sayHello()
    wrappedMyClass.sayHello()

    // Novamente para o segundo método:
    // - O Wrapper faz o print do segundo @Greeting("...")
    // - O Wrapper invoca a seguir myClass.compute()
    wrappedMyClass.compute()


    val input = "Name: John Address: 123 Street"

    val extractor = DataProcessorExtractor(input)

    println("Name: ${extractor.getName()}")

    println("Address: ${extractor.getAddress()}")
}