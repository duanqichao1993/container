package org.geek.container;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Context {

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type, TypeImplementation extends Type>
    void bind(Class<Type> componentsClass, Class<TypeImplementation> implementationClass) {

        final Constructor<TypeImplementation> constructor = getInjectConstructor(implementationClass);
        providers.put(componentsClass, (Provider<Type>) () -> {
            try {
                final Object[] dependencies = Arrays.stream(constructor.getParameters()).map(p -> get(p.getType())).toArray(Object[]::new);
                return (Type) constructor.newInstance(dependencies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <Type> Constructor<Type> getInjectConstructor(Class<Type> implementationClass) {
        final List<Constructor<?>> injectConstructor = Arrays.stream(implementationClass.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        if (injectConstructor.size() > 1) {
            throw new IllegalsComponentException();
        }
        return (Constructor<Type>) injectConstructor.stream().findFirst().orElseGet(() -> {
            try {
                return implementationClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalsComponentException();
            }
        });

    }


    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }

}
