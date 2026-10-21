package demo.client

import demo.api.PackagePrivateCompanion
import demo.api.PackagePrivateConstructor
import demo.api.PackagePrivateName
import demo.api.PackagePrivateType
import demo.api.packagePrivateExtension
import demo.api.packagePrivateFunction
import demo.api.packagePrivateProperty

fun useFromDifferentPackage() {
    PackagePrivateType()
    packagePrivateFunction()
    packagePrivateProperty.length
    val alias: PackagePrivateName = "not package-private at compile time"
    PackagePrivateConstructor()
    PackagePrivateCompanion.companionFunction()
    "extension".packagePrivateExtension()
    check(alias.isNotEmpty())
}
