package archer.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;

public class World {
    private static final int CHUNK_SIZE = 16;

    private PerlinNoise perlin = new PerlinNoise(6752478);

    // Map chunk coordinates to Chunk objects
    private HashMap<Long, Chunk> chunks = new HashMap<>();

    // Create or get chunk at (chunkX, chunkZ)
    private Chunk getChunk(int chunkX, int chunkZ) {
        long key = (((long) chunkX) << 32) | (chunkZ & 0xffffffffL);
        if (!chunks.containsKey(key)) {
            Chunk chunk = new Chunk(perlin);
            chunk.setChunkCoords(chunkX, chunkZ);
            chunk.generate(chunkX, chunkZ);
            chunk.buildMesh();
            chunks.put(key, chunk);
        }
        return chunks.get(key);
    }

    public void generateChunks(int worldWidth, int worldDepth) {
        int chunkCountX = worldWidth / CHUNK_SIZE;
        int chunkCountZ = worldDepth / CHUNK_SIZE;
        for (int cx = 0; cx < chunkCountX; cx++) {
            for (int cz = 0; cz < chunkCountZ; cz++) {
                getChunk(cx, cz);
            }
        }
    }

    public void render(int modelLoc, Matrix4f view, Matrix4f projection, Vector3f cameraPosition, int renderDistance) {
        int chunkRenderDistance = renderDistance; // render chunks within this radius

        int cameraChunkX = (int) Math.floor(cameraPosition.x / CHUNK_SIZE);
        int cameraChunkZ = (int) Math.floor(cameraPosition.z / CHUNK_SIZE);

        for (int dx = -chunkRenderDistance; dx <= chunkRenderDistance; dx++) {
            for (int dz = -chunkRenderDistance; dz <= chunkRenderDistance; dz++) {
                int cx = cameraChunkX + dx;
                int cz = cameraChunkZ + dz;
                Chunk chunk = getChunk(cx, cz);
                chunk.render(modelLoc, view, projection);
            }
        }
    }
}
