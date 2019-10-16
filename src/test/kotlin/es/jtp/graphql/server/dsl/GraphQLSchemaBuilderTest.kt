package es.jtp.graphql.server.dsl

import es.jtp.graphql.*
import es.jtp.graphql.server.dsl.enums.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.schema.idl.*
import org.junit.jupiter.api.*
import java.util.*

internal class GraphQLSchemaBuilderTest {
    @Test
    fun y() {
        val schema = graphQL {
            schema {
                query<TestType>()
            }

            scalar<Date> {
                description = "Date scalar type"

                serializer { _ ->
                    "date"
                }
                deserializer { _ ->
                    Date()
                }
            }

            type<TestType> {
                field(TestType::scalar) {
                    arguments<TestType.ScalarArguments> {
                        argument(TestType.ScalarArguments::x) {
                            defaultValue("--")
                        }
                    }

                    resolver { env ->
                        Date()
                    }
                }
            }
            type<TestUnion1> {
                directive<TestDirective2> {
                    argument(TestDirective2::x, "x")
                }
            }
            type<TestUnion2> {}
            type<TestInterface1> {}
            type<TestInterface2> {
                implementOne<TestInterface>()
            }

            interfaceType<TestInterface> {
                description = "InterfaceType type"
            }

            union<TestUnion> {
                description = "UnionType type"
                ofTwo<TestUnion1, TestUnion2>()
            }

            enum<TestEnum> {
                value(TestEnum.A) {
                    description = "JEJEJ"
                }
            }

            directive<TestDirective> {
                on(DirectiveLocation.Enum)
            }

            directive<TestDirective2> {
                onAnyLocation()
                argument(TestDirective2::x) {
                    defaultValue("def")
                }
            }

            inputType<TestInputType> {
                description = "TestInputType"
                field(TestInputType::a) {
                    defaultValue("A default")
                }
            }
        }

        val input = """
            query {
                scalar
            }
        """.trimIndent()

        val textualSchema = schema.toGraphQLString()
        val exeSchema = schema.build()
        val res = exeSchema.execute(input)

        print(schema)


        val graphQLSchema = SchemaParser().parse(textualSchema)
    }
}
