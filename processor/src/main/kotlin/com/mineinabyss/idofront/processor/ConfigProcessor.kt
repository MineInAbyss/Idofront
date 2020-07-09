package com.mineinabyss.idofront.processor

import com.google.auto.service.AutoService
import com.mineinabyss.idofront.annotations.Config
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedOptions(BindFieldsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ConfigProcessor : AbstractProcessor() {
//    companion object {
//        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
//    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Config::class.java).forEach { methodElement ->
            processingEnv.messager.errormessage { "Can only be applied to functions,  element: $methodElement " }
//            if (methodElement.kind != ElementKind.METHOD) {
//                processingEnv.messager.errormessage { "Can only be applied to functions,  element: $methodElement " }
//                return false
//            }
//
//            (methodElement as ExecutableElement).parameters.forEach { variableElement ->
//                generateNewMethod(methodElement, variableElement, processingEnv.elementUtils.getPackageOf(methodElement).toString())
//            }
        }
        return false
    }

    /*private fun generateNewMethod(method: ExecutableElement, variable: VariableElement, packageOfMethod: String) {
        val generatedSourcesRoot: String = "TODO"//processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if(generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        val variableAsElement = processingEnv.typeUtils.asElement(variable.asType())
        val fieldsInArgument = ElementFilter.fieldsIn(variableAsElement.enclosedElements)
        val annotationArgs = method.getAnnotation(Config::class.java).viewIds


        val funcBuilder = FunSpec.builder("bindFields")
                .addModifiers(KModifier.PUBLIC)
                .addParameter(variable.simpleName.toString(), variableAsElement.asType().asTypeName())
                .addParameter(method.getAnnotation(Config::class.java).viewName, ClassName("android.view", "View"))

        annotationArgs.forEachIndexed { index, viewId ->
            funcBuilder.addStatement(
                    "%L.findViewById<%T>(R.id.%L).text = %L.%L",
                    method.getAnnotation(Config::class.java).viewName,
                    ClassName("android.widget", "TextView"),
                    viewId,
                    variable.simpleName,
                    fieldsInArgument[index].simpleName
            )
        }
        val file = File(generatedSourcesRoot)
        file.mkdir()
        FileSpec.builder(packageOfMethod, "BindFieldsGenerated").addFunction(funcBuilder.build()).build().writeTo(file)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Config::class.java.canonicalName)
    }*/
}