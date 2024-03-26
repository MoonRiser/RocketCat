package com.example.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

internal class IntSummableVisitor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
    val resolver: Resolver,
) : KSVisitorVoid() {

    private val summables: MutableList<String> = mutableListOf()


    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val qualifiedName = classDeclaration.qualifiedName?.asString()
        //1. 合法性检查
        if (!classDeclaration.isDataClass()) {
            logger.error(
                "@IntSummable cannot target non-data class $qualifiedName",
                classDeclaration
            )
            return
        }

        if (qualifiedName == null) {
            logger.error(
                "@IntSummable must target classes with qualified names",
                classDeclaration
            )
            return
        }

        //2. 解析Class信息
        classDeclaration.getAllProperties()
            .forEach {
                it.accept(this, Unit)
            }

        if (summables.isEmpty()) {
            return
        }

        //3. 代码生成

        val fileSpec = FileSpec.builder(
            packageName = classDeclaration.packageName.asString(),
            fileName = classDeclaration.simpleName.asString()
        ).apply {
            addFunction(
                FunSpec.builder("sumInts")
                    .receiver(ClassName.bestGuess(qualifiedName))
                    .returns(Int::class)
                    .addStatement("val sum = ${summables.joinToString(" + ")}")
                    .addStatement("return sum")
                    .build()
            )
        }.build()

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false),
            packageName = classDeclaration.packageName.asString(),
            fileName = classDeclaration.simpleName.asString()
        ).use { outputStream ->
            outputStream.writer()
                .use {
                    fileSpec.writeTo(it)
                }
        }
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        if (property.type.resolve().isAssignableFrom(resolver.builtIns.intType)) {
            val name = property.simpleName.asString()
            summables.add(name)
        }
    }

    private fun KSClassDeclaration.isDataClass() = modifiers.contains(Modifier.DATA)
}