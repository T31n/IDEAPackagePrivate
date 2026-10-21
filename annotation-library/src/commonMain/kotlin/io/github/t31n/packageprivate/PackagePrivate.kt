package io.github.t31n.packageprivate

/**
 * Marks a declaration as package-private for the Package Private for Kotlin IDE plugin.
 *
 * This annotation is retained in compiled metadata so the IDE plugin can enforce it
 * when the annotated declaration is consumed from a dependency.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS,
)
@Retention(AnnotationRetention.BINARY)
public annotation class PackagePrivate
