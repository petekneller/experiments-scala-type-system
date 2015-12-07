package di;

/*
From https://gist.github.com/JamesIry/5117983/raw/6b75bfa32baeb7aa5b15b6488199e2b57116b217/gistfile1.java
as a comment from http://blog.rintcius.nl/post/scala-traits-as-well-defined-modules-and-a-crime-scene-investigation.html
 */

/*
public abstract class Super {
    public abstract String foo();

    public Super() {
        System.out.println(foo().length());
    }
}

public class Test extends Super {
    String bar = "hello";

    public String foo() {
        return bar;
    }

    public static void main(String[] args) {
        new Test();
    }
}
*/

