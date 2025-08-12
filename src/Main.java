import archer.model.Block;
import archer.shader.ShaderHelper;
import archer.textures.Colour;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.joml.Matrix4f;

public class Main {
    private long window;
    public int shaderProgram;

    private Block Block = new Block();

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

        window = glfwCreateWindow(1600, 1200, "Hello LWJGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // vsync
        glfwShowWindow(window);

        GL.createCapabilities(); // Must be done before any OpenGL calls

        // Compile and link shaders here
        ShaderHelper ShaderHelper = new ShaderHelper();

        shaderProgram = ShaderHelper.createShaderProgram("Vertex.glsl", "Fragment.glsl");
        glUseProgram(shaderProgram);

        Block.VAOID = glGenVertexArrays();
        Block.VBOID = glGenBuffers();
        Block.EBOID = glGenBuffers();

        glBindVertexArray(Block.VAOID);

        // Vertex Buffer
        glBindBuffer(GL_ARRAY_BUFFER, Block.VBOID);
        glBufferData(GL_ARRAY_BUFFER, Block.blockAll(0, 0, 0, Colour.BLUE), GL_STATIC_DRAW);

        // Element Buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, Block.EBOID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Block.INDICES, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glEnable(GL_DEPTH_TEST);
    }

    private void loop() {
        GL.createCapabilities();
        double angle = -1;

        Matrix4f view = new Matrix4f().translate(0f, 0f, -3f);

        while (!glfwWindowShouldClose(window)) {
            angle += 1;

            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) view.translate( 0, 0, 0.1f);
            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) view.translate( 0, 0, -0.1f);

            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) view.translate( 0.1f, 0, 0f);
            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) view.translate( -0.1f, 0, 0f);

            if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) view.translate( 0, -0.1f, 0f);
            if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) view.translate( 0, 0.1f, 0f);

            if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) view.rotate( 0.1f, 1, 0, 0);
            if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) view.rotate( -0.1f, 1, 0, 0);

            if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) view.rotate( 0.1f, 0, 1, 0);
            if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) view.rotate( -0.1f, 0, 1, 0);

            Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45f), 800f/600f, 0.1f, 100f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glUseProgram(shaderProgram);
            glBindVertexArray(Block.VAOID);

            // Send matrices to shaders
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(16);

                glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "view"), false, view.get(fb));
                glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "projection"), false, projection.get(fb));

                Matrix4f model1 = new Matrix4f().translate(0f, -1f, 0f);
                glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "model"), false, model1.get(fb));
                glDrawElements(GL_TRIANGLES, Block.INDICES.length, GL_UNSIGNED_INT, 0);

                Matrix4f model2 = new Matrix4f().translate(0f, 1f, 0f);
                glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "model"), false, model2.get(fb));
                glDrawElements(GL_TRIANGLES, Block.INDICES.length, GL_UNSIGNED_INT, 0);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
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
