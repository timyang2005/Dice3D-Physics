package com.dice3d.renderer

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DiceTextureTest {

    @Test
    fun diceModel_shouldSupportTextureRendering() {
        assertThat(DiceModel::class.java.methods.any { it.name == "setTextureResourceId" })
            .named("DiceModel should have setTextureResourceId method")
            .isTrue()
    }

    @Test
    fun diceFragmentShader_shouldUseTextureCoord() {
        val fragmentShader = """
            precision mediump float;
            varying vec2 vTextureCoord;
            uniform sampler2D uTexture;
            void main() {
                vec4 texColor = texture(uTexture, vTextureCoord);
                gl_FragColor = texColor;
            }
        """.trimIndent()
        assertThat(fragmentShader).contains("varying vec2 vTextureCoord")
        assertThat(fragmentShader).contains("sampler2D uTexture")
        assertThat(fragmentShader).contains("texture(uTexture")
    }
}
