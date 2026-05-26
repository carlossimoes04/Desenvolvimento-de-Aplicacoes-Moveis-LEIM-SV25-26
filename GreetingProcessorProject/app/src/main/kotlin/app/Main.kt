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


    // Cria-se a String principal (o input)
    val input = "Name: John Address: 123 Street"

    // Instancia-se a classe que foi gerada pelo compilador e injeta-se o input lá
    val extractor = DataProcessorExtractor(input)

    // Ao chamar o .getName(), ele vai executar o Regex "Name: (\\w+)" que foi injetado pelo código gerado,
    // e extrair automaticamente "John"
    println("Name: ${extractor.getName()}")

    // Da mesma forma, o .getAddress() vai acionar a linha de código gerada automaticamente com Regex "Address: (.+)"
    // e imprimir "123 Street"
    println("Address: ${extractor.getAddress()}")
}