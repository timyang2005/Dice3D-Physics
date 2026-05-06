uniform mat4 uMProjCameraMatrix;
uniform mat4 uMMatrix;
uniform vec3 uLightLocation;
attribute vec3 aPosition;
void main() {
    vec3 A = vec3(0.0, 0.1, 0.0);
    vec3 n = vec3(0.0, 1.0, 0.0);
    vec3 S = uLightLocation;
    vec3 V = (uMMatrix * vec4(aPosition, 1.0)).xyz;
    float denom = dot(n, V - S);
    if (abs(denom) < 0.001) {
        gl_Position = vec4(0.0, 0.0, -2.0, 1.0);
    } else {
        vec3 VL = S + (V - S) * (dot(n, A - S) / denom);
        gl_Position = uMProjCameraMatrix * vec4(VL, 1.0);
    }
}
