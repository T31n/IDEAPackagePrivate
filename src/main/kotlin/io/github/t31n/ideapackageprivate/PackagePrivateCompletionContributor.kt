package io.github.t31n.ideapackageprivate

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.idea.KotlinLanguage

class PackagePrivateCompletionContributor : CompletionContributor() {
    init {
        val provider = object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: ProcessingContext,
                result: CompletionResultSet,
            ) {
                result.runRemainingContributors(parameters) { completionResult ->
                    val declaration = PackagePrivateSupport.packagePrivateDeclarationForUse(
                        completionResult.lookupElement.psiElement,
                        parameters.position,
                    )
                    if (declaration == null ||
                        PackagePrivateSupport.isAccessibleFrom(declaration, parameters.position)
                    ) {
                        result.passResult(completionResult)
                    }
                }
            }
        }
        val pattern = PlatformPatterns.psiElement().withLanguage(KotlinLanguage.INSTANCE)
        listOf(CompletionType.BASIC, CompletionType.SMART)
            .forEach { extend(it, pattern, provider) }
    }
}
