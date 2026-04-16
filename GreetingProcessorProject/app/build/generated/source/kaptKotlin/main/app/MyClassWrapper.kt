package app

public final class MyClassWrapper(
  public val original: MyClass,
) {
  public final fun sayHello() {
    println("Hello form MyClass!")
    original.sayHello()
  }

  public final fun compute() {
    println("Welcome to the compute function!")
    original.compute()
  }
}
