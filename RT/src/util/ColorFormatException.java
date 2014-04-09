/**
 *
 */

package util;

/**
 * @author BU CS673 - Clone Productions
 */
public class ColorFormatException extends Exception
{
	/**
	 * Constructs a ColorFormatException object with the specified message.
	 * 
	 * @param message
	 *            the exception's detail message.
	 */
	public ColorFormatException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a ColorFormatException object from the specified exception.
	 * 
	 * @param exception
	 *            the exception to wrap.
	 */
	public ColorFormatException(Throwable exception)
	{
		super(exception);
	}
}
