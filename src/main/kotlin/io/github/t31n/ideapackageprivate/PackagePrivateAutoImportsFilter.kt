package io.github.t31n.ideapackageprivate

import com.intellij.openapi.project.DumbService
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.codeInsight.KotlinAutoImportsFilter
import org.jetbrains.kotlin.idea.stubindex.KotlinClassShortNameIndex
import org.jetbrains.kotlin.idea.stubindex.KotlinFunctionShortNameIndex
import org.jetbrains.kotlin.idea.stubindex.KotlinPropertyShortNameIndex
import org.jetbrains.kotlin.idea.stubindex.KotlinTypeAliasShortNameIndex
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class PackagePrivateAutoImportsFilter : KotlinAutoImportsFilter {
    private val filteredSuggestions = ThreadLocal.withInitial { emptySet<FqName>() }

    override fun forceAutoImportForElement(file: KtFile, suggestions: Collection<FqName>): Boolean {
        filteredSuggestions.remove()
        if (DumbService.isDumb(file.project)) return false

        val blocked = suggestions.filterTo(mutableSetOf()) { suggestion ->
            declarationsFor(suggestion, file).any { candidate ->
                isRestrictedOutsidePackage(candidate, file) ||
                    isRestrictedConstructorUse(candidate, file)
            }
        }
        if (blocked.isEmpty()) return false

        filteredSuggestions.set(blocked)
        return true
    }

    override fun filterSuggestions(suggestions: Collection<FqName>): Collection<FqName> {
        val blocked = filteredSuggestions.get()
        filteredSuggestions.remove()
        return suggestions.filterNot(blocked::contains)
    }

    private fun declarationsFor(fqName: FqName, context: KtFile): List<KtDeclaration> {
        val shortName = fqName.shortName().asString()
        val scope = GlobalSearchScope.allScope(context.project)
        val declarations = buildList<KtDeclaration> {
            addAll(KotlinClassShortNameIndex.Helper.get(shortName, context.project, scope))
            addAll(KotlinTypeAliasShortNameIndex.Helper.get(shortName, context.project, scope))
            addAll(KotlinFunctionShortNameIndex.Helper.get(shortName, context.project, scope))
            addAll(KotlinPropertyShortNameIndex.Helper.get(shortName, context.project, scope))
        }
        return declarations.filter { (it as? KtNamedDeclaration)?.fqName?.asString() == fqName.asString() }
    }

    private fun isRestrictedOutsidePackage(candidate: KtDeclaration, context: KtFile): Boolean {
        val restricted = PackagePrivateSupport.packagePrivateDeclaration(candidate) ?: return false
        return !PackagePrivateSupport.isAccessibleFrom(restricted, context)
    }

    private fun isRestrictedConstructorUse(candidate: KtDeclaration, context: KtFile): Boolean {
        val klass = candidate as? KtClass ?: return false
        val calls = PsiTreeUtil.findChildrenOfType(context, KtCallExpression::class.java)
        return calls.any { call ->
            val callee = call.calleeExpression as? KtSimpleNameExpression ?: return@any false
            if (callee.getReferencedName() != klass.name) return@any false
            val constructor = PackagePrivateSupport.constructorForCall(call, klass) ?: return@any false
            val restricted = PackagePrivateSupport.packagePrivateDeclaration(constructor) ?: return@any false
            !PackagePrivateSupport.isAccessibleFrom(restricted, context)
        }
    }
}
