/**
 *
 */

package util;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist */
public class RenderSettingException extends Exception
{
	/**
	 * Constructs a RenderSettingException object with the specified message.
	 * 
	 * @param message
	 *            the exception's detail message.
	 */
	public RenderSettingException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a RenderSettingException object from the specified exception.
	 * 
	 * @param exception
	 *            the exception to wrap.
	 */
	public RenderSettingException(Throwable exception)
	{
		super(exception);
	}
}
