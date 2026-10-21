package demo.api

import io.github.t31n.packageprivate.PackagePrivate

@PackagePrivate
class PackagePrivateType

@PackagePrivate
fun packagePrivateFunction() = "same package only"

@PackagePrivate
val packagePrivateProperty = "same package only"

@PackagePrivate
typealias PackagePrivateName = String

class PackagePrivateConstructor @PackagePrivate constructor()

class PackagePrivateCompanion {
    @PackagePrivate
    companion object {
        fun companionFunction() = "same package only"
    }
}

@PackagePrivate
fun String.packagePrivateExtension() = this
