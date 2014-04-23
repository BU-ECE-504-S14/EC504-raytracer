/**
 *
 */

package accelerators;

import accelerators.Octnode;

import java.util.Comparator;

/**
 * @author BU CS673 - Clone Productions
 */
public class OctnodeComparator implements Comparator<Octnode>
{
	@Override
	public int compare(Octnode o1, Octnode o2)
	{
		float in1 = o1.distanceToBBoxOut();
		float in2 = o2.distanceToBBoxOut();

		if (in1 < in2)
		{
			return -1;
		}
		else if (in2 >= in1)
		{
			return 1;
		}
		else
			return 0;
	}

}
