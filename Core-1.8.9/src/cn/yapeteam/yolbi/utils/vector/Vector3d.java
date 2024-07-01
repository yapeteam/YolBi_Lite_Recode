package cn.yapeteam.yolbi.utils.vector;

import lombok.Getter;

/**
 * @author Patrick
 * @since 10/17/2021
 */
@Getter
public class Vector3d {

    private final double x;
    private final double y;
    private final double z;

    public Vector3d(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d add(final double x, final double y, final double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d add(final Vector3d vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public Vector3d subtract(final double x, final double y, final double z) {
        return add(-x, -y, -z);
    }

    public Vector3d subtract(final Vector3d vector) {
        return add(-vector.x, -vector.y, -vector.z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3d multiply(final double v) {
        return new Vector3d(x * v, y * v, z * v);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3d)) return false;

        Vector3d vector = (Vector3d) obj;
        return ((Math.floor(x) == Math.floor(vector.x)) && Math.floor(y) == Math.floor(vector.y) && Math.floor(z) == Math.floor(vector.z));
    }

    public double distance(Vector2f nextPos) {
        return Math.sqrt(Math.pow(nextPos.getX() - x, 2) + Math.pow(nextPos.getY() - y, 2));
    }

    public double angle(Vector2d nextPos) {
        double dx = nextPos.getX() - x;
        double dy = nextPos.getY() - y;
        return Math.atan2(dy, dx);
    }
}
