/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package preibisch_code;


/**
 * Signalizes that there were not enough data points available to estimate the
 * {@link AbstractModel}.
 *
  */
public class NotEnoughDataPointsException extends Exception
{
	private static final long serialVersionUID = 492656623783477968L;

	public NotEnoughDataPointsException()
	{
		super( "Not enough data points to solve the Model." );
	}


	public NotEnoughDataPointsException( final String message )
	{
		super( message );
	}


	public NotEnoughDataPointsException( final Throwable cause )
	{
		super( cause );
	}


	public NotEnoughDataPointsException( final String message, final Throwable cause )
	{
		super( message, cause );
	}
}
