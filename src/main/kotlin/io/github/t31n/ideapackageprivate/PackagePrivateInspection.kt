package io.github.t31n.ideapackageprivate

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class PackagePrivateInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val reportedImports = mutableSetOf<KtImportDirective>()

        fun reportImport(directive: KtImportDirective) {
            if (reportedImports.add(directive)) {
                holder.registerProblem(directive, PackagePrivateSupport.PROBLEM_MESSAGE)
            }
        }

        return object : KtTreeVisitorVoid() {
            override fun visitImportDirective(importDirective: KtImportDirective) {
                val target = resolveImportTarget(importDirective) ?: return
                val restricted = PackagePrivateSupport.packagePrivateDeclaration(target) ?: return
                if (PackagePrivateSupport.isAccessibleFrom(restricted, importDirective)) return
                reportImport(importDirective)
            }

            override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
                if (PsiTreeUtil.getParentOfType(expression, KtImportDirective::class.java, false) != null) {
                    super.visitSimpleNameExpression(expression)
                    return
                }

                val target = expression.references.asSequence().mapNotNull { it.resolve() }.firstOrNull()
                val restricted = PackagePrivateSupport.packagePrivateDeclarationForUse(target, expression)
                    ?: return super.visitSimpleNameExpression(expression)
                if (!PackagePrivateSupport.isAccessibleFrom(restricted, expression)) {
                    holder.registerProblem(expression, PackagePrivateSupport.PROBLEM_MESSAGE)
                    val file = expression.containingFile as? KtFile
                    file?.importDirectives
                        ?.filter { importsRestrictedDeclaration(it, restricted) }
                        ?.forEach(::reportImport)
                }

                super.visitSimpleNameExpression(expression)
            }
        }
    }

    private fun resolveImportTarget(directive: KtImportDirective): PsiElement? {
        if (directive.isAllUnder) return null
        val importedReference = directive.importedReference ?: return null
        val lastName = when (importedReference) {
            is KtSimpleNameExpression -> importedReference
            else -> PsiTreeUtil.findChildrenOfType(importedReference, KtSimpleNameExpression::class.java).lastOrNull()
        } ?: return null
        return lastName.references.asSequence().mapNotNull { it.resolve() }.lastOrNull()
    }

    private fun importsRestrictedDeclaration(
        directive: KtImportDirective,
        restricted: org.jetbrains.kotlin.psi.KtDeclaration,
    ): Boolean {
        if (!directive.isAllUnder) {
            val target = resolveImportTarget(directive) ?: return false
            val importedRestriction = PackagePrivateSupport.packagePrivateDeclaration(target)
            if (importedRestriction?.isEquivalentTo(restricted) == true) return true

            val constructor = restricted as? KtConstructor<*>
            val importedDeclaration = PackagePrivateSupport.asKotlinDeclaration(target)
            return constructor != null &&
                importedDeclaration?.isEquivalentTo(constructor.getContainingClassOrObject()) == true
        }

        val prefix = directive.importedFqName?.asString() ?: return false
        val importTarget = (restricted as? KtConstructor<*>)?.getContainingClassOrObject() ?: restricted
        val enclosingClasses = generateSequence(importTarget as PsiElement) { it.parent }
            .filterIsInstance<KtClassOrObject>()
            .toList()
        val validPrefixes = enclosingClasses.mapNotNullTo(mutableSetOf()) { it.fqName?.asString() }

        if (generateSequence(importTarget.parent) { it.parent }.none { it is KtClassOrObject }) {
            val packageName = (importTarget.containingFile as? KtFile)?.packageFqName?.asString()
            if (packageName != null) validPrefixes.add(packageName)
        }
        return prefix in validPrefixes
    }
}
