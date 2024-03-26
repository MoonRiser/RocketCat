package com.example.ksp.processor

import com.example.ksp.annotation.IntSummable
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

internal class IntSummableProcessor(
    val options: Map<String, String>,
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {

    private var hasProcessed = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (hasProcessed) {
            return emptyList()
        }
        val symbols = resolver.getSymbolsWithAnnotation(IntSummable::class.qualifiedName!!).filter { it.validate() }

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach {
                it.accept(IntSummableVisitor(codeGenerator, logger, resolver), Unit)
            }
        hasProcessed = true
        return symbols.toList()
    }
}