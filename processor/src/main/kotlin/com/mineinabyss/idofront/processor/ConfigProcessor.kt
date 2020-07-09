package com.mineinabyss.idofront.processor

import com.google.auto.service.AutoService
import com.mineinabyss.idofront.annotations.Config
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ConfigProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ConfigProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Config::class.java.name)
    }


    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var output: String = "nothing"
        roundEnv.getElementsAnnotatedWith(Config::class.java).forEach { element ->
//            processingEnv.messager.errormessage { "Can only be applied to functions,  element: $methodElement " }
//            if (methodElement.kind != ElementKind.METHOD) {
//                processingEnv.messager.errormessage { "Can only be applied to functions,  element: $element " }
//                return false
//            }
            processingEnv.messager.noteMessage { "" }
            output = element.enclosedElements.toString()
//            output = (element as ExecutableElement).parameters.toString()
//            (methodElement as ExecutableElement).parameters.forEach { variableElement ->
//                generateNewMethod(methodElement, variableElement, processingEnv.elementUtils.getPackageOf(methodElement).toString())
//            }
//            processAnnotation(element)
        }
        val fileName = "GeneratedStuff"
//        val objBuilder = TypeSpec.objectBuilder(fileName)
//        objBuilder.addProperty(PropertySpec.builder(
//                name = "test",
//                type = String::class
//        ).mutable(false).initializer("\"$output\"").build())
        val packageName = "com.mineinabyss.sdk.generated"
        val file = FileSpec.builder(packageName, fileName)
                .addProperty(PropertySpec.builder(
                        name = "test",
                        type = String::class
                ).mutable(false).initializer("\"$output\"").build())
//                .addType(objBuilder.build())
                .build()
        val generatedDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(generatedDirectory, "$fileName.kt"))
        return true
    }
}