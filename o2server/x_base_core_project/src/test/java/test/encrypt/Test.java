package test.encrypt;

public class Test {
	public static void main(String[] args) {

		B b = new B();

		System.out.println(b.getClass().isAssignableFrom(B.class));
		System.out.println(B.class.isAssignableFrom(b.getClass()));
		
		System.out.println(b.getClass().isAssignableFrom(A.class));
		System.out.println(A.class.isAssignableFrom(b.getClass()));

	}

	public static class A {

	}

	public static class B extends A {

	}

}
