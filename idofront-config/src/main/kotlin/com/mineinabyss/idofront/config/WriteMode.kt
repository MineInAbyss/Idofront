package com.mineinabyss.idofront.config

enum class WriteMode {
    /** Never touch filesystem */
    NEVER,

    /** Create a config file if it did not exist but never update it. Requires a default config to be set. */
    WHEN_EMPTY,

    /** Always re-encode a parsed config file (ex. if structure has changed). May be annoying in some use-cases. */
    ALWAYS,
}