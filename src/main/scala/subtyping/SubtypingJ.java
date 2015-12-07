package subtyping;

public class SubtypingJ {

    private static class X {}
    private static class Y extends X {}
    private static class Z extends Y {}

    private static class A {
        public Y doSomething(Y y) {
            return null;
        }
    }

    private static class B extends A {
        @Override
        public Y doSomething(Y y) {
            return null;
        }
    }

    private static class B2 extends A {
        //@Override  // this does not compile, as the JVM does not allow contravariance of arg types
        public Y doSomething(X y) {
            return null;
        }
    }

    private static class B3 extends A {
        @Override
        public Z doSomething(Y y) {
            return null;
        }
    }


}
