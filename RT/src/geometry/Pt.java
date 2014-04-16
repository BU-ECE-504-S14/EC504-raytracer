package geometry;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/*
 * simple wrapper for Vector3d class. Reduces potential for performing an incorrect tranformation.
 */
public class Pt extends Vector3d
{

	private static final long serialVersionUID = 3L;
	public final double w = 1;

	public Pt(Pt n)
	{
		super((Vector3d) n);
	}

	public Pt()
	{
		super();
	}

	public Pt(double x, double y, double z)
	{
		super(x, y, z);
	}

	public Pt(Tuple3d t1)
	{
		super(t1);
	}

	public Pt(Tuple3f t1)
	{
		super(t1);
	}

	public Pt(Vector3d v1)
	{
		super(v1);
	}

	public Pt(Vector3f v1)
	{
		super(v1);
	}

	public String toString()
	{
		return this.x + ", " + this.y + ", " + this.z;
	}

}
