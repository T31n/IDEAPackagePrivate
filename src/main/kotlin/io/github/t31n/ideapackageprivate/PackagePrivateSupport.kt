package io.github.t31n.ideapackageprivate

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

internal object PackagePrivateSupport {
    const val ANNOTATION_FQ_NAME = "io.github.t31n.packageprivate.PackagePrivate"
    const val PROBLEM_MESSAGE = "Package-private declaration cannot be accessed outside its package."

    fun packagePrivateDeclaration(element: PsiElement?): KtDeclaration? {
        val declaration = asKotlinDeclaration(element) ?: return null
        return generateSequence(declaration as PsiElement) { it.parent }
            .filterIsInstance<KtDeclaration>()
            .firstOrNull(::hasPackagePrivateAnnotation)
    }

    fun packagePrivateDeclarationForUse(element: PsiElement?, usage: PsiElement): KtDeclaration? {
        packagePrivateDeclaration(element)?.let { return it }

        val call = PsiTreeUtil.getParentOfType(usage, KtCallExpression::class.java, false) ?: return null
        val callee = call.calleeExpression ?: return null
        if (!PsiTreeUtil.isAncestor(callee, usage, false)) return null

        val klass = asKotlinDeclaration(element) as? KtClass ?: return null
        val constructor = constructorForCall(call, klass) ?: return null
        return packagePrivateDeclaration(constructor)
    }

    fun constructorForCall(call: KtCallExpression, klass: KtClass): KtConstructor<*>? {
        val argumentCount = call.valueArgumentList?.arguments?.size ?: 0
        val constructors = buildList<KtConstructor<*>> {
            klass.primaryConstructor?.let(::add)
            addAll(klass.secondaryConstructors)
        }
        return constructors.singleOrNull { it.valueParameters.size == argumentCount }
    }

    fun isAccessibleFrom(declaration: KtDeclaration, context: PsiElement): Boolean {
        val declaringPackage = (declaration.containingFile as? KtFile)?.packageFqName ?: return true
        val usingPackage = (context.containingFile as? KtFile)?.packageFqName ?: return true
        return declaringPackage == usingPackage
    }

    fun hasPackagePrivateAnnotation(declaration: KtDeclaration): Boolean =
        declaration.annotationEntries.any { annotation ->
            annotation.typeReference?.references
                ?.asSequence()
                ?.mapNotNull { it.resolve() }
                ?.any { qualifiedName(it) == ANNOTATION_FQ_NAME } == true
        }

    fun asKotlinDeclaration(element: PsiElement?): KtDeclaration? {
        if (element == null) return null
        return sequenceOf(element, element.navigationElement, element.originalElement)
            .filterIsInstance<KtDeclaration>()
            .firstOrNull()
    }

    fun isSameDeclaration(first: PsiElement?, second: PsiElement): Boolean =
        asKotlinDeclaration(first)?.isEquivalentTo(second) == true

    private fun qualifiedName(element: PsiElement): String? =
        when (element) {
            is KtNamedDeclaration -> element.fqName?.asString()
            is PsiClass -> element.qualifiedName
            else -> when (val navigationElement = element.navigationElement) {
                is KtNamedDeclaration -> navigationElement.fqName?.asString()
                is PsiClass -> navigationElement.qualifiedName
                else -> null
            }
        }
}
