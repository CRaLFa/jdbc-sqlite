package util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

	@Override
	default void accept(T t) {
		try {
			acceptOrThrow(t);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	void acceptOrThrow(T t) throws Throwable;

}
