package es.jtp.graphql.server.dsl

import es.jtp.graphql.server.dsl.interfaces.*
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
                query<TypeClass>()
                mutation<Type2Class>()
                rootOperation<Type2Class>("Operation")
            }

            scalar<Date> {
                description = "Date scalar type"

                serializer { _ -> "date" }
                deserializer { _ -> Date() }
            }

            type<TypeClass> {
                description = "TypeClass type"
            }

            type<Type2Class> {
                description = "TypeClass 2 type"
            }

            interfaceType<ISandra> {
                description = "InterfaceType type"
            }

            union("UnionType") {
                description = "UnionType type"
                ofTwo<Int, String>()
            }
        }

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
