package org.geek.container;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

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

                assertSame(instance, context.get(Components.class));

            }

            @Test
            public void should_bind_type_to_a_class_whit_default() {
                context.bind(Components.class, ComponentsWithDefaultImplementation.class );
                final Components instance = context.get(Components.class);
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentsWithDefaultImplementation);

            }

            @Test
            public void should_bind_type_to_a_class_whit_inject_constructor() {
                final Dependency dependency = new Dependency() {

                };
                context.bind(Components.class, ComponentsWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);

                final Components instance = context.get(Components.class);
                assertNotNull(instance);
                assertSame(dependency, ((ComponentsWithInjectConstructor) instance).getDependency());
            }

            // TODO: 2022/11/6 A -> B -> C
            @Test
            public void should_bind_type_to_transitive_dependency() {
                context.bind(Components.class, ComponentsWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWhitInjectConstructor.class);
                context.bind(String.class,"indirect dependencies");

                final Components instance = context.get(Components.class);
                assertNotNull(instance);

                final Dependency dependency = ((ComponentsWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);

                assertSame("indirect dependencies" , ((DependencyWhitInjectConstructor)dependency).getDependency());


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