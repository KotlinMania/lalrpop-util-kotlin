// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "SwiftTestHarness",
    platforms: [
        .macOS(.v15),
    ],
    dependencies: [
        .package(name: "LalrpopUtil", path: "../build/SPMPackage/macosArm64/Debug")
    ],
    targets: [
        .testTarget(
            name: "SwiftTestHarnessTests",
            dependencies: [
                .product(name: "LalrpopUtilLibrary", package: "LalrpopUtil")
            ],
            cSettings: [
                .unsafeFlags([
                ]),
            ],
            linkerSettings: [
                .unsafeFlags([
                    "-L", "../build/swift-test",
                    "-lLalrpopUtil",
                    "-framework", "Testing",
                ]),
            ]
        ),
    ]
)
