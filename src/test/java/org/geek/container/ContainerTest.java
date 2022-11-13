package org.geek.container;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContainerTest {


    Context context ;
    @BeforeEach
    public void setUp(){
        context = new Context();
    }


    @Nested
    public class ComponentConstruction {
        // TODO: instance
        // TODO: abstract class
        // TODO: interface
        @Nested
        public class ConstructorInjection {
            @Test
            public void should_bind_type_to_specific_instance() {
                final Components instance = new Components() {
                };
                context.bind(Components.class, instance);

                assertSame(instance, context.get(Components.class).orElseThrow(DependencyNotFindException::new));

            }

            @Test
            public void should_bind_type_to_a_class_whit_default() {
                context.bind(Components.class, ComponentsWithDefaultImplementation.class );
                final Components instance = context.get(Components.class).get();
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentsWithDefaultImplementation);

            }

            @Test
            public void should_bind_type_to_a_class_whit_inject_constructor() {
                final Dependency dependency = new Dependency() {

                };
                context.bind(Components.class, ComponentsWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);

                final Components instance = context.get(Components.class).get();
                assertNotNull(instance);
                assertSame(dependency, ((ComponentsWithInjectConstructor) instance).getDependency());
            }

            // TODO: 2022/11/6 A -> B -> C
            @Test
            public void should_bind_type_to_transitive_dependency() {
                context.bind(Components.class, ComponentsWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWhitInjectConstructor.class);
                context.bind(String.class,"indirect dependencies");

                final Components instance = context.get(Components.class).get();
                assertNotNull(instance);

                final Dependency dependency = ((ComponentsWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);

                assertSame("indirect dependencies" , ((DependencyWhitInjectConstructor)dependency).getDependency());

            }


            @Test
            public void should_throw_exception_if_with_multi_inject_constructor(){
                assertThrows(IllegalsComponentException.class, ()->{
                    context.bind(Components.class, ComponentWithMultiInjectConstructor.class);
                });
            }
            
            @Test
            public void should_throw_exception_if_no_inject_nor_no_default_constructor_provided() {
                assertThrows(IllegalsComponentException.class, () -> {
                    context.bind(Components.class, ComponentWithNoInjectNorDefaultConstructorProvided.class);
                });

            }

            // TODO: 2022/11/13 dependencies not exist
            @Test
            public void should_throw_exception_if_dependency_not_exist(){
                context.bind(Components.class, ComponentsWithInjectConstructor.class);
                assertThrows(DependencyNotFindException.class, () -> context.get(Components.class).orElseThrow(DependencyNotFindException::new));

            }

            @Test
            public void should_return_empty_if_component_not_defined(){
               Optional<Components> result = context.get(Components.class);
               assertTrue(result.isEmpty());
            }

        }

        @Nested
        public class FiledConstruction {

        }


    }

    @Nested
    public class DependencySelection {

    }

    @Nested
    public class LifeCycleManagement {

    }
}


interface Components {

}

interface Dependency{

}

class ComponentsWithDefaultImplementation implements Components {
    public ComponentsWithDefaultImplementation(){}

}

class ComponentsWithInjectConstructor implements Components {

    private Dependency dependency;

    public ComponentsWithInjectConstructor(){}

    @Inject
    public ComponentsWithInjectConstructor(Dependency dependency){

        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }
}


class DependencyWhitInjectConstructor implements Dependency{
    String dependency;

    public DependencyWhitInjectConstructor(){};


    @Inject
    public DependencyWhitInjectConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency( ) {
        return dependency;
    }
}

class ComponentWithMultiInjectConstructor implements Components {

    @Inject
    public ComponentWithMultiInjectConstructor(String str , Double value){

    }

    @Inject
    public ComponentWithMultiInjectConstructor(String str) {
    }
}

class ComponentWithNoInjectNorDefaultConstructorProvided implements  Components {

    public ComponentWithNoInjectNorDefaultConstructorProvided(String name) {
    }
}