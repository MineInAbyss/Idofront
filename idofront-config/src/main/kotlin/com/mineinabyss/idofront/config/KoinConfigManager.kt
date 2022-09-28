package com.mineinabyss.idofront.config

//inline fun <reified T> Module.singleConfig(
//    plugin: Plugin,
//    serializer: KSerializer<T> = serializer(),
//    path: Path = plugin.dataFolder.toPath() / "config.yml",
//    format: StringFormat = Yaml(configuration = YamlConfiguration(strictMode = false)),
//    crossinline unload: ReloadScope.(conf: T) -> Unit = {},
//    crossinline load: ReloadScope.(conf: T) -> Unit = {}
//) {
//    val config = object: IdofrontConfig<T>(plugin, serializer, path, format) {
//        override fun ReloadScope.load() {
//            load(data)
//        }
//
//        override fun ReloadScope.unload() {
//            unload(data)
//        }
//    }
//    factory { config.data }
//}
