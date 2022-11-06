package org.geek.container;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class ContainerTest {

    interface Components {

    }


    static class ComponentsWithDefaultImplementation implements Components {
        public ComponentsWithDefaultImplementation(){}

    }

    @Nested
    public class ComponentConstruction {
        // TODO: instance
        // TODO: abstract class
        // TODO: interface
        @Nested
        public class ConstructorInjection {
            // TODO: 2022/11/6 no args constructor
            @Test
            public void should_bind_type_to_specific_instance() {
                final Components instance = new Components() {
                };
                Context context = new Context();
                context.bind(Components.class, instance);

                assertSame(instance, context.get(Components.class));

            }

            @Test
            public void should_bind_type_to_a_class_whit_default() {
                final Context context = new Context();
                context.bind(Components.class, ComponentsWithDefaultImplementation.class );
                final Components instance = context.get(Components.class);
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentsWithDefaultImplementation);

            }

            // TODO: 2022/11/6 with dependency


            // TODO: 2022/11/6 A -> B -> C


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