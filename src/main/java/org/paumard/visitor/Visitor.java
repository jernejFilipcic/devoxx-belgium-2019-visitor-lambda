package org.paumard.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface Visitor<R> {

    R visit(Object o);

    static <R> Visitor<R> of(VisitorInitializer<R> visitorInitializer) {
        Map<Class<?>, Function<Object, R>> registry = new HashMap<>();
        visitorInitializer.init(registry::put);
        return o -> registry.get(o.getClass()).apply(o);
    }

    static <T, R> X<T, R> forType(Class<T> type) {
        return function -> builder -> builder.register(type, function.compose(type::cast));
    }

    interface X<T, R> {
        Y<R> execute(Function<T, R> function);
    }

    interface Y<R> extends VisitorInitializer<R> {

        default <T> X<T, R> forType(Class<T> type) {
            return function -> builder -> {
                this.init(builder);
                builder.register(type, function.compose(type::cast));
            };
        }
    }
}
