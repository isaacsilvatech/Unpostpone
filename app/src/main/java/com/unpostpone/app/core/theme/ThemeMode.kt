package com.unpostpone.app.core.theme

enum class ThemeMode {
    SystemDefault {
        override val nativeName = "System default"
        override val displayName = "System default"
    },
    Light {
        override val nativeName = "Light"
        override val displayName = "Light"
    },
    Dark {
        override val nativeName = "Dark"
        override val displayName = "Dark"
    },
    ;

    abstract val nativeName: String
    abstract val displayName: String

    companion object {
        fun fromName(name: String?): ThemeMode =
            entries.firstOrNull { it.name == name } ?: SystemDefault

        val pickable: List<ThemeMode> = entries.toList()
    }
}
