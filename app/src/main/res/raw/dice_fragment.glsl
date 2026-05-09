precision mediump float;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying vec2 vTextureCoord;
uniform vec3 uDiceColor;
uniform vec3 uNumberColor;

float circle(vec2 uv, vec2 center, float radius) {
    float dist = distance(uv, center);
    return 1.0 - smoothstep(radius - 0.015, radius + 0.015, dist);
}

void main() {
    vec2 uv = vec2(vTextureCoord.x, -vTextureCoord.y);
    float dotRadius = 0.09;
    float dotMask = 0.0;
    dotMask = max(dotMask, circle(uv, vec2(0.5, 0.5), dotRadius));
    dotMask = max(dotMask, circle(uv, vec2(0.25, 0.75), dotRadius));
    dotMask = max(dotMask, circle(uv, vec2(0.75, 0.25), dotRadius));
    dotMask = max(dotMask, circle(uv, vec2(0.75, 0.75), dotRadius));
    dotMask = max(dotMask, circle(uv, vec2(0.25, 0.25), dotRadius));
    vec3 faceColor = mix(uDiceColor, uNumberColor, dotMask);
    vec4 baseColor = vec4(faceColor, 1.0);
    vec4 finalColor = baseColor * vAmbient + baseColor * vDiffuse + baseColor * vSpecular;
    gl_FragColor = finalColor;
}
