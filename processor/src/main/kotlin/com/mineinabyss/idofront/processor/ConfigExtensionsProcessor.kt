package com.mineinabyss.idofront.processor

import com.google.auto.service.AutoService
import com.mineinabyss.idofront.annotations.GenerateConfigExtensions
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@Suppress("unused")
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ConfigExtensionsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ConfigExtensionsProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GenerateConfigExtensions::class.java.name)
    }

    private val elements: Elements by lazy { processingEnv.elementUtils }
    private val types: Types by lazy { processingEnv.typeUtils }

    @KotlinPoetMetadataPreview
    val classInspector by lazy { ElementsClassInspector.create(elements, types) }

    @KotlinPoetMetadataPreview
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GenerateConfigExtensions::class.java)
                .map { it as TypeElement }
                .associateWith { it.toTypeSpec(classInspector) }
                .forEach { (element, typeSpec) ->
                    if(!createType(element, typeSpec)) return true
                }
        return true
    }

    //TODO name things better
    @KotlinPoetMetadataPreview
    private fun createType(element: TypeElement, classData: TypeSpec): Boolean {
        val outerClassName = element.simpleName
        val fileName = "${outerClassName}DataExt"
        val packageName = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
        val fileBuilder = FileSpec.builder(packageName, fileName)
        val configAnnotations = element.getAnnotation(GenerateConfigExtensions::class.java)
        val delegatingPropName = configAnnotations.property
        val dataClassName = configAnnotations.insideClassName

        //get the inside class and its spec
        val dataElement = (element.enclosedElements
                .find { it.kind.isClass && it.simpleName.toString() == dataClassName }
                as? TypeElement)
                ?: processingEnv.messager.errormessage { "Could not find class called $dataClassName inside $outerClassName" }.let { return false }
        val dataSpec = dataElement.toTypeSpec(classInspector)

        //TODO ensure property is defined (it appears propertySpecs doesn't see properties coming from a superclass)
//        if (classData.propertySpecs.none { it.name == delegatingPropName
//                        /*&& it.modifiers.run { contains(KModifier.PUBLIC) || contains(KModifier.INTERNAL) } */})
//            processingEnv.messager.errormessage { "$outerClassName must define a property named $delegatingPropName of type $dataClassName" }.let { return false }
        //go through this class' properties and generate extensions for them
        dataSpec.propertySpecs.filter {
            it.modifiers.run { !contains(KModifier.PRIVATE) || contains(KModifier.PROTECTED) }
        }.forEach { prop ->
            val propName = prop.name
            val propType = prop.type

            //add a global extension property to the file
            fileBuilder.addProperty(
                    PropertySpec.builder(propName, propType)
                            .receiver(element.asType().asTypeName())
                            //TODO find a way of copying KDoc (prop.kdoc is empty)
                            .addKdoc(CodeBlock.of("@see [$outerClassName.$dataClassName.$propName]"))
                            .getter(FunSpec.getterBuilder()
                                    .addStatement("return this.$delegatingPropName.$propName")
                                    .build())
                            .addModifiers(prop.modifiers)
                            .apply {
                                if (prop.mutable) {
                                    mutable(true)
                                    setter(
                                            FunSpec.setterBuilder()
                                                    .addParameter("value", propType)
                                                    .addStatement("this.$delegatingPropName.$propName = value ")
                                                    //TODO ensure this function exists
                                                    .addStatement("this.queueSave()")
                                                    .build()
                                    )
                                }
                            }
                            .build()
            )
        }

        val file = fileBuilder.build()
        val generatedDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
        file.writeTo(File(generatedDirectory))
        return true
    }
}