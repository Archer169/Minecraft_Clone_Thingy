package archer.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    public Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private float yaw = -90f;   // facing -Z initially
    private float pitch = 0f;

    private float movementSpeed = 10f;  // units per second
    private float mouseSensitivity = 0.1f;

    public Camera(Vector3f startPos) {

        position = startPos;
        worldUp = new Vector3f(0, 1, 0);
        front = new Vector3f(0, 0, -1);
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        Vector3f center = new Vector3f(position).add(front);
        return new Matrix4f().lookAt(position, center, up);
    }

    public void processKeyboard(int key, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        if (key == GLFW_KEY_W)
            position.add(new Vector3f(front).mul(velocity));
        if (key == GLFW_KEY_S)
            position.sub(new Vector3f(front).mul(velocity));
        if (key == GLFW_KEY_A)
            position.sub(new Vector3f(right).mul(velocity));
        if (key == GLFW_KEY_D)
            position.add(new Vector3f(right).mul(velocity));
        if (key == GLFW_KEY_SPACE)
            position.add(new Vector3f(worldUp).mul(velocity)); // up
        if (key == GLFW_KEY_LEFT_SHIFT)
            position.sub(new Vector3f(worldUp).mul(velocity)); // down
    }

    public void processMouseMovement(float xoffset, float yoffset) {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // Clamp pitch so you don’t flip upside down
        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        Vector3f frontNew = new Vector3f();
        frontNew.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        frontNew.y = (float) Math.sin(Math.toRadians(pitch));
        frontNew.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front = frontNew.normalize();

        right = front.cross(worldUp, new Vector3f()).normalize();
        up = right.cross(front, new Vector3f()).normalize();
    }
}

