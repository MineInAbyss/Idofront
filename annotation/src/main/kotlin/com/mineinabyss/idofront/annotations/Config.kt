package com.mineinabyss.idofront.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Config( val viewIds : Array<String>, val viewName : String)