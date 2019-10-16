package es.jtp.graphql.server.dsl.enums

import graphql.introspection.*

/**
 * Locations where directives can be applied to.
 */
enum class DirectiveLocation(internal val introspectionDirective: Introspection.DirectiveLocation) {
    // Executable
    Query(Introspection.DirectiveLocation.ARGUMENT_DEFINITION),
    Mutation(Introspection.DirectiveLocation.MUTATION),
    Subscription(Introspection.DirectiveLocation.SUBSCRIPTION),
    Field(Introspection.DirectiveLocation.FIELD),
    FragmentDefinition(Introspection.DirectiveLocation.FRAGMENT_DEFINITION),
    FragmentSpread(Introspection.DirectiveLocation.FRAGMENT_SPREAD),
    InlineFragment(Introspection.DirectiveLocation.INLINE_FRAGMENT),

    // Type system
    Schema(Introspection.DirectiveLocation.SCHEMA),
    Scalar(Introspection.DirectiveLocation.SCALAR),
    Object(Introspection.DirectiveLocation.OBJECT),
    FieldDefinition(Introspection.DirectiveLocation.FIELD_DEFINITION),
    ArgumentDefinition(Introspection.DirectiveLocation.ARGUMENT_DEFINITION),
    Interface(Introspection.DirectiveLocation.INTERFACE),
    Union(Introspection.DirectiveLocation.UNION),
    Enum(Introspection.DirectiveLocation.ENUM),
    EnumValue(Introspection.DirectiveLocation.ENUM_VALUE),
    InputObject(Introspection.DirectiveLocation.INPUT_OBJECT),
    InputFieldDefinition(Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION)
}
