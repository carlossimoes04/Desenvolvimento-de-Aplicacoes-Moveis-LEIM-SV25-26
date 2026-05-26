package app
import annotations.Greeting

/**
 * Classe que atua como prova de conceito para testar o Annotation Processor
 */
// 'open' permite que a classe base pudesse vir a ser herdada por outras
open class MyClass {

    // Aplica a anotação importada definindo a mensagem injetada à medida da função
    @Greeting("Hello form MyClass!")
    // Um método qualquer da nossa lógica de negócio
    open fun sayHello() {
        // A lógica do método intocável
        println("Executing sayHello method")
    }

    // Aplica a anotação importada com uma mensagem injetada diferente
    @Greeting("Welcome to the compute function!")
    // Outro método da nossa lógica de negócio
    open fun compute(){
        // A lógica do método intocável
        println("Computing something important...")
    }
}