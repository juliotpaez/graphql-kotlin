package es.jtp.graphql

import java.util.*

interface TestUnion

interface TestInterface

data class TestType(val scalar: Date, val union: TestUnion, val iface: TestInterface)

data class TestUnion1(val x1: Int) : TestUnion
data class TestUnion2(val x2: String) : TestUnion

data class TestInterface1(val x1: Int) : TestInterface
data class TestInterface2(val x2: String) : TestInterface

enum class TestEnum {
    A,
    B,
    C
}
