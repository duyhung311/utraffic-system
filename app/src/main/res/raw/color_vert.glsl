uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ModelViewMatrix;

uniform float u_RotationAngle;
uniform float u_Scale;
uniform vec2 u_Translation;

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

mat4 genScaleMatrix(float x, float y, float z) {
    return mat4(
        vec4(x, 0, 0, 0),
        vec4(0, y, 0, 0),
        vec4(0, 0, z, 0),
        vec4(0, 0, 0, 1)
    );
}

mat4 genRotationMatrix(float angle) {
    float angleInRadians = radians(angle);
    float c = cos(angleInRadians);
    float s = sin(angleInRadians);
    return mat4(
        vec4(c, s, 0, 0),
        vec4(-s, c, 0, 0),
        vec4(0, 0, 1, 0),
        vec4(0, 0, 0, 1)
    );
}

mat4 genTranslationMatrix(float x, float y, float z) {
    return mat4(
        vec4(1, 0, 0, 0),
        vec4(0, 1, 0, 0),
        vec4(0, 0, 1, 0),
        vec4(x, y, z, 1)
    );
}

mat4 genTransformMatrix() {
    mat4 scaleMatrix = genScaleMatrix(u_Scale, u_Scale, u_Scale);
    mat4 rotationMatrix = genRotationMatrix(u_RotationAngle);
    mat4 translationMatrix = genTranslationMatrix(u_Translation.x, u_Translation.y, 0.0);

    return scaleMatrix * rotationMatrix * translationMatrix;
}

void main() {
    mat4 transformMatrix = genTransformMatrix();
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * transformMatrix * a_Position;
    gl_PointSize = 50.0;
    v_Color = a_Color;
}