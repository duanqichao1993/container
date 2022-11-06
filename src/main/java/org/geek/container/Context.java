package org.geek.container;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Context {

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type, TypeImplementation extends Type>
    void bind(Class<Type> componentsClass, Class<TypeImplementation> componentsImplementationClass) {
        providers.put(componentsClass, (Provider<Type>) () -> {
            try {

                final Constructor<TypeImplementation> constructor = getInjectConstructor(componentsImplementationClass);
                final Object[] dependencies = Arrays.stream(constructor.getParameters()).map(p -> get(p.getType())).toArray(Object[]::new);
                return (Type) constructor.newInstance(dependencies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private  <Type> Constructor<Type> getInjectConstructor(Class<Type> implementationClass) {
        final Stream<Constructor<?>> injectConstructorStream = Arrays.stream(implementationClass.getConstructors())
                                                                      .filter(c -> c.isAnnotationPresent(Inject.class));
        return (Constructor<Type>) injectConstructorStream.findFirst().orElseGet(()-> {
            try {
                return implementationClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }

}
