package archer.world;

public class PerlinNoise {
    private final int[] permutation;

    public PerlinNoise(int seed) {
        permutation = new int[512];
        int[] p = new int[256];
        java.util.Random random = new java.util.Random(seed);
        for (int i = 0; i < 256; i++) p[i] = i;
        for (int i = 255; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[index];
            p[index] = temp;
        }
        System.arraycopy(p, 0, permutation, 0, 256);
        System.arraycopy(p, 0, permutation, 256, 256);
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 7;      // Convert low 3 bits of hash code
        double u = h < 4 ? x : y;
        double v = h < 4 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // 2D Perlin Noise
    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = permutation[permutation[X] + Y];
        int ab = permutation[permutation[X] + Y + 1];
        int ba = permutation[permutation[X + 1] + Y];
        int bb = permutation[permutation[X + 1] + Y + 1];

        double x1 = lerp(u, grad(aa, xf, yf), grad(ba, xf - 1, yf));
        double x2 = lerp(u, grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1));
        return lerp(v, x1, x2);
    }

    private double grad3D(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public double noise3D(double x, double y, double z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);
        double zf = z - Math.floor(z);

        double u = fade(xf);
        double v = fade(yf);
        double w = fade(zf);

        int aaa = permutation[permutation[permutation[X] + Y] + Z];
        int aba = permutation[permutation[permutation[X] + (Y + 1)] + Z];
        int aab = permutation[permutation[permutation[X] + Y] + (Z + 1)];
        int abb = permutation[permutation[permutation[X] + (Y + 1)] + (Z + 1)];
        int baa = permutation[permutation[permutation[(X + 1)] + Y] + Z];
        int bba = permutation[permutation[permutation[(X + 1)] + (Y + 1)] + Z];
        int bab = permutation[permutation[permutation[(X + 1)] + Y] + (Z + 1)];
        int bbb = permutation[permutation[permutation[(X + 1)] + (Y + 1)] + (Z + 1)];

        double x1, x2, y1, y2;

        x1 = lerp(u, grad3D(aaa, xf, yf, zf), grad3D(baa, xf - 1, yf, zf));
        x2 = lerp(u, grad3D(aba, xf, yf - 1, zf), grad3D(bba, xf - 1, yf - 1, zf));
        y1 = lerp(v, x1, x2);

        x1 = lerp(u, grad3D(aab, xf, yf, zf - 1), grad3D(bab, xf - 1, yf, zf - 1));
        x2 = lerp(u, grad3D(abb, xf, yf - 1, zf - 1), grad3D(bbb, xf - 1, yf - 1, zf - 1));
        y2 = lerp(v, x1, x2);

        return lerp(w, y1, y2);
    }
}
