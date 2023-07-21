package io.hiwepy.boot.api.subject;

public class SubjectUtils {

	public static Subject getSubject() {
		Subject subject = ThreadContext.getSubject();
		if (subject == null) {
			ThreadContext.bind(subject);
		}
		return subject;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPrincipal(Class<T> clazz) {
		Object principal = getSubject().getPrincipal();
		if (clazz.isAssignableFrom(principal.getClass())) {
			return (T) principal;
		}
		return null;
	}

	public static Object getPrincipal() {
		return getSubject().getPrincipal();
	}

	public static boolean isAuthenticated() {
		return getSubject().isAuthenticated();
	}

}
