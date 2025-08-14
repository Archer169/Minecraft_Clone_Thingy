#version 330 core

in vec3 vertexColor;
in vec3 Normal;
in vec3 FragPos;

out vec4 FragColor;

uniform vec3 lightDir;  // Directional light direction (should be normalized)
uniform vec3 lightColor;
uniform vec3 viewPos;   // camera position (for specular, optional)

void main() {
    // Ambient lighting
    float ambientStrength = 0.3;
    vec3 ambient = ambientStrength * lightColor;

    // Diffuse lighting
    vec3 norm = normalize(Normal);
    vec3 lightDirection = normalize(-lightDir); // Light coming from lightDir
    float diff = max(dot(norm, lightDirection), 0.0);
    vec3 diffuse = diff * lightColor;

    // Combine results
    vec3 result = (ambient + diffuse) * vertexColor;

    FragColor = vec4(result, 1.0);
}
