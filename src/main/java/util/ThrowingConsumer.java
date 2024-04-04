package util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

	void acceptOrThrow(T t) throws Throwable;

	@Override
	default void accept(T t) {
		try {
			acceptOrThrow(t);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
