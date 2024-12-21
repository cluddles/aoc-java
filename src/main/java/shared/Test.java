package shared;

public class Test {

	public static void check(Object val1, Object val2) {
		if (val1 == val2) return;
		if (val1.equals(val2)) return;
		throw new RuntimeException(
				desc(val1) + classSuffix(val1) +
				" does not equal " +
				desc(val2) + classSuffix(val2));
	}

	private static String desc(Object obj) {
		return (obj == null? "<null>" : obj.toString());
	}

	private static String classSuffix(Object obj) {
		return (obj == null? "" : " <" + obj.getClass().getSimpleName()) + ">";
	}

}
