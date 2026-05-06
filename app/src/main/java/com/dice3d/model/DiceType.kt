package com.dice3d.model

enum class DiceType(val faces: Int, val displayName: String, val modelName: String) {
    D4(4, "D4", "d4.obj"),
    D6(6, "D6", "d6.obj"),
    D8(8, "D8", "d8.obj"),
    D10(10, "D10", "d10.obj"),
    D12(12, "D12", "d12.obj"),
    D20(20, "D20", "d20.obj"),
    D100(100, "D100", "d100.obj");

    companion object {
        fun fromFaces(faces: Int): DiceType = entries.find { it.faces == faces } ?: D6
    }
}
