import archer.world.PerlinNoise;
import archer.world.World;
import archer.world.Camera;
import archer.world.Chunk;
import archer.shader.ShaderHelper;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
    private long window;
    private int shaderProgram;

    private World world;
    private Camera camera;
    private Chunk chunk = new Chunk(new PerlinNoise(6752478));

    private int width = 1600;
    private int height = 1200;

    private double lastMouseX, lastMouseY;
    private boolean firstMouse = true;

    public void run() {
        init();
        loop();
        cleanup();
    }

    public void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Hello LWJGL", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        // Capture and hide the cursor for mouse look
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        ShaderHelper shaderHelper = new ShaderHelper();
        shaderProgram = shaderHelper.createShaderProgram("Vertex.glsl", "Fragment.glsl");

        glUseProgram(shaderProgram);

        world = new World();

        camera = new Camera(new Vector3f(0, chunk.getHeight(0, 0) + 2, 0)); // Default constructor - position set inside Camera

        world.generateChunks(128, 128);

        glEnable(GL_DEPTH_TEST);
    }

    private void loop() {
        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            processInput(deltaTime);

            Matrix4f view = camera.getViewMatrix();
            Matrix4f projection = new Matrix4f().perspective(
                    (float) Math.toRadians(70),
                    width / (float) height,
                    0.1f,
                    1000f
            );

            glUseProgram(shaderProgram);

// Get uniform locations once (you can optimize this by caching outside loop if you want)
            int lightDirLoc = glGetUniformLocation(shaderProgram, "lightDir");
            int lightColorLoc = glGetUniformLocation(shaderProgram, "lightColor");
            int viewPosLoc = glGetUniformLocation(shaderProgram, "viewPos");
            int modelLoc = glGetUniformLocation(shaderProgram, "u_Model");
            int normalMatrixLoc = glGetUniformLocation(shaderProgram, "u_NormalMatrix");

// Set lighting uniforms
            glUniform3f(lightDirLoc, 0.5f, -1.0f, 0.3f);     // light direction
            glUniform3f(lightColorLoc, 1.0f, 1.0f, 1.0f);    // white light
            Vector3f camPos = camera.position;
            glUniform3f(viewPosLoc, camPos.x, camPos.y, camPos.z);  // camera position

// Set model matrix (identity if no per-block transform)
            Matrix4f model = new Matrix4f().identity();
            FloatBuffer fb = MemoryUtil.memAllocFloat(16);
            model.get(fb);
            glUniformMatrix4fv(modelLoc, false, fb);

// Set normal matrix (inverse transpose of model)
            Matrix4f normalMatrix = new Matrix4f(model).invert().transpose();
            normalMatrix.get(fb);
            glUniformMatrix4fv(normalMatrixLoc, false, fb);

            MemoryUtil.memFree(fb);

// Then render your world with the MVP uniform
            int mvpLoc = glGetUniformLocation(shaderProgram, "u_MVP");
            Matrix4f mvp = new Matrix4f(projection).mul(view).mul(model);
            FloatBuffer mvpBuffer = MemoryUtil.memAllocFloat(16);
            mvp.get(mvpBuffer);
            glUniformMatrix4fv(mvpLoc, false, mvpBuffer);
            MemoryUtil.memFree(mvpBuffer);

            world.render(modelLoc, view, projection, camera.position, 4);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void processInput(float deltaTime) {
        // Keyboard
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) camera.processKeyboard(GLFW_KEY_W, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) camera.processKeyboard(GLFW_KEY_S, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) camera.processKeyboard(GLFW_KEY_A, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) camera.processKeyboard(GLFW_KEY_D, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) camera.processKeyboard(GLFW_KEY_SPACE, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) camera.processKeyboard(GLFW_KEY_LEFT_SHIFT, deltaTime);

        // Mouse
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(window, mouseX, mouseY);

        if (firstMouse) {
            lastMouseX = mouseX[0];
            lastMouseY = mouseY[0];
            firstMouse = false;
        }

        float xoffset = (float) (mouseX[0] - lastMouseX);
        float yoffset = (float) (lastMouseY - mouseY[0]); // reversed: y-coords go from bottom to top

        lastMouseX = mouseX[0];
        lastMouseY = mouseY[0];

        camera.processMouseMovement(xoffset, yoffset);
    }

    private void cleanup() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
