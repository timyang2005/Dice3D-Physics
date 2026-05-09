precision mediump float;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying vec2 vTextureCoord;
varying vec3 vWorldPos;
uniform vec3 uDiceColor;
uniform vec3 uNumberColor;

float circle(vec2 uv, vec2 center, float radius) {
    float dist = distance(uv, center);
    return 1.0 - smoothstep(radius - 0.015, radius + 0.015, dist);
}

void main() {
    vec3 pos = vWorldPos;
    vec3 absPos = abs(pos);
    float maxAxis = max(max(absPos.x, absPos.y), absPos.z);

    vec2 localUV;
    float faceValue;

    if (absPos.x >= maxAxis - 0.01) {
        localUV = vec2(pos.z * 0.5 + 0.5, pos.y * 0.5 + 0.5);
        faceValue = (pos.x > 0.0) ? 5.0 : 2.0;
    } else if (absPos.y >= maxAxis - 0.01) {
        localUV = vec2(pos.x * 0.5 + 0.5, pos.z * 0.5 + 0.5);
        faceValue = (pos.y > 0.0) ? 3.0 : 4.0;
    } else {
        localUV = vec2(pos.x * 0.5 + 0.5, pos.y * 0.5 + 0.5);
        faceValue = (pos.z > 0.0) ? 1.0 : 6.0;
    }

    float dotMask = 0.0;
    float dotRadius = 0.09;

    if (faceValue == 1.0) {
        dotMask = circle(localUV, vec2(0.5, 0.5), dotRadius);
    } else if (faceValue == 2.0) {
        dotMask = max(circle(localUV, vec2(0.25, 0.25), dotRadius),
                      circle(localUV, vec2(0.75, 0.75), dotRadius));
    } else if (faceValue == 3.0) {
        dotMask = max(max(circle(localUV, vec2(0.25, 0.25), dotRadius),
                          circle(localUV, vec2(0.5, 0.5), dotRadius)),
                      circle(localUV, vec2(0.75, 0.75), dotRadius));
    } else if (faceValue == 4.0) {
        dotMask = max(max(circle(localUV, vec2(0.25, 0.25), dotRadius),
                          circle(localUV, vec2(0.75, 0.25), dotRadius)),
                      max(circle(localUV, vec2(0.25, 0.75), dotRadius),
                          circle(localUV, vec2(0.75, 0.75), dotRadius)));
    } else if (faceValue == 5.0) {
        dotMask = max(max(max(circle(localUV, vec2(0.5, 0.5), dotRadius),
                              circle(localUV, vec2(0.25, 0.25), dotRadius)),
                          circle(localUV, vec2(0.75, 0.25), dotRadius)),
                      max(circle(localUV, vec2(0.25, 0.75), dotRadius),
                          circle(localUV, vec2(0.75, 0.75), dotRadius)));
    } else if (faceValue == 6.0) {
        dotMask = max(max(max(circle(localUV, vec2(0.25, 0.2), dotRadius),
                              circle(localUV, vec2(0.25, 0.5), dotRadius)),
                          circle(localUV, vec2(0.25, 0.8), dotRadius)),
                      max(max(circle(localUV, vec2(0.75, 0.2), dotRadius),
                              circle(localUV, vec2(0.75, 0.5), dotRadius)),
                          circle(localUV, vec2(0.75, 0.8), dotRadius)));
    }

    vec3 faceColor = mix(uDiceColor, uNumberColor, dotMask);
    vec4 baseColor = vec4(faceColor, 1.0);
    vec4 finalColor = baseColor * vAmbient + baseColor * vDiffuse + baseColor * vSpecular;
    gl_FragColor = finalColor;
}
