package app

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.junit.Assert.*

class MutableKodeinTests : Spek({
    describe("MutableKodein") {
        on("something going on") {
            it("should do something") {
                assertEquals("a", "a")
            }
        }
    }
})