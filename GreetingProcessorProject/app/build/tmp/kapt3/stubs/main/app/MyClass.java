package app;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0005H\u0016\u00a8\u0006\u0007"}, d2 = {"Lapp/MyClass;", "", "<init>", "()V", "sayHello", "", "compute", "app"})
public class MyClass {
    
    public MyClass() {
        super();
    }
    
    @annotations.Greeting(message = "Hello form MyClass!")
    public void sayHello() {
    }
    
    @annotations.Greeting(message = "Welcome to the compute function!")
    public void compute() {
    }
}