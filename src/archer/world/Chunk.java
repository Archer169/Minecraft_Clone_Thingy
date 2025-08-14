package archer.world;

import archer.model.Block;
import archer.textures.Colour;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int MAX_HEIGHT = 128;
    public int SURFACEY;

    private Block block = new Block();
    private PerlinNoise perlin;

    private BlockData[][][] blocks = new BlockData[CHUNK_SIZE][MAX_HEIGHT][CHUNK_SIZE];

    // OpenGL buffers
    private int vaoId, vboId, eboId;
    private int indexCount;

    // Simple container for block info
    private static class BlockData {
        public boolean solid;
        public Colour colour;

        public BlockData(boolean solid, Colour colour) {
            this.solid = solid;
            this.colour = colour;
        }
    }

    // Constructor needs a PerlinNoise instance (shared or new)
    public Chunk(PerlinNoise perlin) {
        this.perlin = perlin;
    }

    // Generate terrain blocks for this chunk at world coords (chunkX, chunkZ)
    public void generate(int chunkX, int chunkZ) {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int worldX = chunkX * CHUNK_SIZE + x;
                int worldZ = chunkZ * CHUNK_SIZE + z;

                SURFACEY = getHeight(worldX, worldZ);

                for (int y = 0; y < MAX_HEIGHT; y++) {
                    if (y > SURFACEY) {
                        // Air
                        blocks[x][y][z] = null;
                    } else if (isCave(worldX, y, worldZ) && y < (SURFACEY + 1)) {
                        // Cave (air)
                        blocks[x][y][z] = null;
                    } else {
                        // Solid block - assign colour based on depth
                        Colour colour;
                        if (y == SURFACEY) {
                            colour = Colour.GREEN; // grass
                        } else if (y > SURFACEY - 3) {
                            colour = Colour.BROWN; // dirt
                        } else {
                            colour = Colour.GREY; // stone
                        }
                        blocks[x][y][z] = new BlockData(true, colour);
                    }
                }
            }
        }
    }

    // Build mesh with face culling and upload to OpenGL
    public void buildMesh() {
        int verticesPerFace = 4;
        int floatsPerVertex = 9;
        int indicesPerFace = 6; // two triangles

        // Max number of faces: each solid block can have up to 6 faces
        int maxFaces = CHUNK_SIZE * MAX_HEIGHT * CHUNK_SIZE * 6;

        float[] vertices = new float[maxFaces * verticesPerFace * floatsPerVertex];
        int[] indices = new int[maxFaces * indicesPerFace];

        int vertexOffset = 0; // counts vertices (not floats)
        int floatOffset = 0;  // counts floats
        int indexOffset = 0;  // counts indices

        // Directions to check neighbors: dx, dy, dz
        int[][] directions = {
                {0, 0, -1}, // back
                {0, 0, 1},  // front
                {-1, 0, 0}, // left
                {1, 0, 0},  // right
                {0, 1, 0},  // top
                {0, -1, 0}  // bottom
        };

        // For each block in chunk
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < MAX_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    BlockData current = blocks[x][y][z];
                    if (current == null || !current.solid) continue;

                    // For each face direction
                    for (int d = 0; d < directions.length; d++) {
                        int dx = directions[d][0];
                        int dy = directions[d][1];
                        int dz = directions[d][2];

                        int nx = x + dx;
                        int ny = y + dy;
                        int nz = z + dz;

                        boolean faceVisible = false;

                        // Check if neighbor is outside chunk or air
                        if (nx < 0 || nx >= CHUNK_SIZE || ny < 0 || ny >= MAX_HEIGHT || nz < 0 || nz >= CHUNK_SIZE) {
                            faceVisible = true;
                        } else {
                            BlockData neighbor = blocks[nx][ny][nz];
                            if (neighbor == null || !neighbor.solid) {
                                faceVisible = true;
                            }
                        }

                        if (faceVisible) {
                            // Generate vertices for this face only
                            float worldX = x + (chunkX * CHUNK_SIZE);
                            float worldY = y;
                            float worldZ = z + (chunkZ * CHUNK_SIZE);

                            // We need a method in Block that returns face vertices by face index (0..5)
                            // with position offset and color, normals, etc.
                            float[] faceVertices = block.getFaceVertices(d, worldX, worldY, worldZ, current.colour);

                            // Copy vertices
                            System.arraycopy(faceVertices, 0, vertices, floatOffset, faceVertices.length);

                            // Copy indices (offset by current vertex count)
                            for (int i = 0; i < indicesPerFace; i++) {
                                indices[indexOffset + i] = block.FACE_INDICES[i] + vertexOffset;
                            }

                            vertexOffset += verticesPerFace;
                            floatOffset += faceVertices.length;
                            indexOffset += indicesPerFace;
                        }
                    }
                }
            }
        }

        indexCount = indexOffset; // only the used indices count

        // Upload to OpenGL buffers
        if (vaoId == 0) vaoId = glGenVertexArrays();
        if (vboId == 0) vboId = glGenBuffers();
        if (eboId == 0) eboId = glGenBuffers();

        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(floatOffset);
        vertexBuffer.put(vertices, 0, floatOffset).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indexCount);
        indexBuffer.put(indices, 0, indexCount).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int stride = floatsPerVertex * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    // Render this chunk (call from world render loop)
    public void render(int modelLoc, Matrix4f view, Matrix4f projection) {
        Matrix4f model = new Matrix4f().identity();
        Matrix4f mvp = new Matrix4f();

        projection.mul(view, mvp);
        mvp.mul(model);

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mvp.get(fb);

        glUniformMatrix4fv(modelLoc, false, fb);

        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    // Helpers:

    private int chunkX, chunkZ;

    // Call before generate/buildMesh to set chunk position in world coords
    public void setChunkCoords(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    // Height function (use your perlin noise)
    public int getHeight(int x, int z) {
        double baseScale = 0.005;
        double scale = 0.005;

        double baseNoise = perlin.noise(x * baseScale, z * baseScale);  // base noise
        baseNoise = (baseNoise + 1) / 2.0;  // normalize 0..1

        // Modulate the coordinates by baseNoise to get "Perlin noise of Perlin noise"
        double modX = x * scale + baseNoise * 10.0;  // 10.0 is modulation strength, tweak as needed
        double modZ = z * scale + baseNoise * 10.0;

        double finalNoise = perlin.noise(modX, modZ);
        finalNoise = (finalNoise + 1) / 2.0;

        int maxHeight = 128;
        return (int) (finalNoise * ((double) maxHeight/4) + (((double) maxHeight /4) * 3));
    }

    // Cave function (3D noise)
    private boolean isCave(int x, int y, int z) {
        if (y < (MAX_HEIGHT - 30)) {
            double scale = 0.1;

            double noiseValue = perlin.noise3D(x * scale, y * scale, z * scale);

            noiseValue = (noiseValue + 1) / 2.0;

            double threshold = 0.6;
            return noiseValue > threshold;
        } else {
            double baseScale = 0.01;
            double scale = 0.01;

            double baseNoise = perlin.noise3D(x * baseScale, y * baseScale, z * baseScale);
            baseNoise = (baseNoise + 1) / 2.0;

            double modX = x * scale + baseNoise * 10.0;  // 10.0 is modulation strength, tweak as needed
            double modZ = z * scale + baseNoise * 10.0;
            double modY = y * scale + baseNoise * 10.0;

            double noiseValue = perlin.noise3D(modX, modY, modZ);
            noiseValue = (noiseValue + 1) / 2.0;

            double threshold = 0.75;
            return noiseValue > threshold;
        }
    }
}
