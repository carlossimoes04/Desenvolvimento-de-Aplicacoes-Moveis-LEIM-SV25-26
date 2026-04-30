package app
import annotations.Greeting

open class MyClass {

    @Greeting("Hello form MyClass!")
    open fun sayHello() {
        println("Executing sayHello method")
    }

    @Greeting("Welcome to the compute function!")
    open fun compute(){
        println("Computing something important...")
    }
}