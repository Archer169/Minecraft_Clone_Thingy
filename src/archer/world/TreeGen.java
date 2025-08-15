package archer.world;

import archer.textures.Colour;

import static archer.world.Chunk.*;

public class TreeGen {
    public Chunk Generate(World world, Chunk chunk, PerlinNoise perlin)
    {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int worldX = chunk.chunkX * CHUNK_SIZE + x;
                int worldZ = chunk.chunkZ * CHUNK_SIZE + z;

                for (int y = 0; y < MAX_HEIGHT; y++) {
                    if (isTree(worldX, worldZ, perlin) && y == (chunk.SURFACEY + 1) && chunk.blocks[x][y-1][z] != null) {
                        Colour trunkColour = Colour.BLUE;
                        Colour leafColour = Colour.GREEN; // color for leaves

                        // Trunk
                        for (int h = 0; h < 4; h++) {
                            if (y + h < MAX_HEIGHT)
                                chunk.blocks[x][y + h][z] = new BlockData(true, trunkColour);
                        }

                        int leafY = y + 3; // top of trunk
                        // Simple cross-shaped leaf layer
                        int[][] offsets = {
                                {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                                {1, 1}, {-1, 1}, {1, -1}, {-1, -1} // optional for bigger leaf layer
                        };

                        for (int[] offset : offsets) {
                            int lx = worldX + offset[0];
                            int lz = worldZ + offset[1];

                            // Ensure we're inside chunk bounds

                            world.placeBlock(lx, leafY, lz, new BlockData(true, leafColour));

                        }

                        // Optional second leaf layer above
                        if (leafY + 1 < MAX_HEIGHT) {
                            world.placeBlock(worldX, leafY + 1, worldZ, new BlockData(true, leafColour));
                        }
                    }
                }
            }
        }

        chunk.buildMesh();

        return chunk;
    }
}
