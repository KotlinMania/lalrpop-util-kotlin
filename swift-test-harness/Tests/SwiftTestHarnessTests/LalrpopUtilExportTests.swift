#if canImport(Testing)
import Testing
import LalrpopUtil

@Suite("LalrpopUtil Export Tests")
struct LalrpopUtilExportTests {
    @Test("Swift module loads and imports cleanly")
    func swiftModuleLoads() {
        #expect(Bool(true))
    }
}
#else
import XCTest
import LalrpopUtil

final class LalrpopUtilExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "LalrpopUtil swift module imported cleanly")
    }
}
#endif
