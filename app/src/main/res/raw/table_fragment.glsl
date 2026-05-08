precision mediump float;
varying vec3 vWorldPos;
uniform float uBrightness;
uniform float uIsDarkMode;

void main() {
    float gridSize = 1.0;
    vec2 grid = abs(fract(vWorldPos.xz / gridSize - 0.5) - 0.5) / fwidth(vWorldPos.xz / gridSize);
    float line = min(grid.x, grid.y);
    float gridAlpha = 1.0 - min(line, 1.0);

    vec3 baseColor = mix(vec3(0.6, 0.6, 0.6), vec3(0.25, 0.25, 0.25), uIsDarkMode);
    vec3 lineColor = mix(vec3(0.4, 0.4, 0.4), vec3(0.15, 0.15, 0.15), uIsDarkMode);
    vec3 color = mix(baseColor, lineColor, gridAlpha);
    gl_FragColor = vec4(color * uBrightness, 1.0);
}
