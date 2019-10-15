package es.jtp.graphql.server.dsl

import es.jtp.graphql.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.*
import graphql.schema.*
import graphql.schema.idl.*
import org.junit.jupiter.api.*
import java.util.*

internal class GraphQLSchemaBuilderTest {
    @Test
    fun x() {
        val sdl = """
            schema @directive(a : 4 b: 3) {
                query: x
            }
            
            "xxx"
type x {
    alfa: Int @deprecated 
}


extend type x @newDirective

scalar u

directive @newDirective on OBJECT
        """.trimIndent()

        val graphQLSchema = buildSchema(sdl)
        val graphQLBuilder = GraphQL.newGraphQL(graphQLSchema)
        val graphQL = graphQLBuilder.build()


        print(graphQLSchema)
    }

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
                    resolver { env ->
                        Date()
                    }
                }
            }
            type<TestUnion1> {}
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
    }


    private fun buildSchema(sdl: String): GraphQLSchema {
        val typeRegistry = SchemaParser().parse(sdl)
        val schemaGenerator = SchemaGenerator()
        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
        runtimeWiring.directive("xx", object : SchemaDirectiveWiring {
            override fun onObject(
                    environment: SchemaDirectiveWiringEnvironment<GraphQLObjectType>?): GraphQLObjectType {
                return super.onObject(environment)
            }
        })

        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring.build())
    }
}
