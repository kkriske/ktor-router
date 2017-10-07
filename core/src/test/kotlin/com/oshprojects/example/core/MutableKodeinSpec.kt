package com.oshprojects.example.core

import com.github.salomonbrys.kodein.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import kotlin.test.*

class MutableKodeinSpec : Spek({

    given("An empty MutableKodein") {
        var kodein = MutableKodein()
        var container = kodein.container

        beforeEachTest {
            kodein = MutableKodein()
            container = kodein.container
        }

        given("A Kodein Module") {
            val id = "id"
            val answer = 5
            val module = Kodein.Module { constant("answer") with answer }

            on("Kodein Module added") {
                kodein.addModule(id, module)

                it("should resolve dependencies of that module") {
                    val ans = kodein.instance<Int>("answer")
                    assertEquals(answer, ans)
                }

                it("should create a new internal instance") {
                    assertNotEquals(container, kodein.container)
                }
            }

            on("Module is removed without being added") {
                kodein.removeModule(id)

                it("should not change the internal instance") {
                    assertEquals(container, kodein.container)
                }
            }
        }
    }

    given("A MutableKodein with one module installed") {
        var kodein = MutableKodein()
        var container = kodein.container
        val id = "id"
        val answer = 5
        val module = Kodein.Module { constant("answer") with answer }

        beforeEachTest {
            kodein = MutableKodein()
            kodein.addModule(id, module)
            container = kodein.container
        }

        on("A module with the same id is added") {

            it("should throw an exception.") {
                assertFailsWith(DuplicateModuleException::class) {
                    kodein.addModule(id, module)
                }
            }

            it("should not change the internal instance") {
                assertEquals(container, kodein.container)
            }
        }

        on("Kodein module is removed") {
            kodein.removeModule(id)

            it("should no longer resolve dependencies of that module.") {
                assertFailsWith(Kodein.NotFoundException::class) {
                    kodein.instance<Int>("answer")
                }
            }

            it("should create a new internal Kodein instance") {
                assertNotEquals(container, kodein.container)
            }
        }
    }

})