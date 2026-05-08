package com.dice3d.model

enum class DiceType(val faces: Int, val displayName: String, val modelName: String) {
    D4(4, "D4", "models/d4.obj"),
    D6(6, "D6", "models/d6.obj"),
    D8(8, "D8", "models/d8.obj"),
    D10(10, "D10", "models/d10.obj"),
    D12(12, "D12", "models/d12.obj"),
    D20(20, "D20", "models/d20.obj"),
    D100(100, "D100", "models/d100.obj");

    companion object {
        fun fromFaces(faces: Int): DiceType = entries.find { it.faces == faces } ?: D6
    }
}
