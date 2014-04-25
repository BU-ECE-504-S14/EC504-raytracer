/**
 *
 */

package util;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist */
public class MaterialFormatException extends Exception
{
	/**
	 * Constructs a ColorFormatException object with the specified message.
	 * 
	 * @param message
	 *            the exception's detail message.
	 */
	public MaterialFormatException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a ColorFormatException object from the specified exception.
	 * 
	 * @param exception
	 *            the exception to wrap.
	 */
	public MaterialFormatException(Throwable exception)
	{
		super(exception);
	}
}
