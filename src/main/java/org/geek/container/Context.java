package org.geek.container;

import jakarta.inject.Provider;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <ComponentType, ComponentImplementation extends ComponentType>
    void bind(Class<ComponentType> componentsClass, Class<ComponentImplementation> componentsImplementationClass) {
        providers.put(componentsClass, (Provider<ComponentType>) () -> {
            try {
                return (ComponentType) ((Class<?>) componentsImplementationClass).getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }

}
