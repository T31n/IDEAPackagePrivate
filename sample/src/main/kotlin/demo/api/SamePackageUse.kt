package demo.api

fun useFromSamePackage() {
    PackagePrivateType()
    packagePrivateFunction()
    packagePrivateProperty.length
    val alias: PackagePrivateName = "visible here"
    PackagePrivateConstructor()
    PackagePrivateCompanion.companionFunction()
    "extension".packagePrivateExtension()
    check(alias.isNotEmpty())
}
