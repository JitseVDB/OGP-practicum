package filesystem.exception;

import be.kuleuven.cs.som.annotate.*;
import filesystem.*;

/**
 * A class for signaling illegal attempts to change an actual disk item due to writability restrictions.
 * 
 * @invar	The referenced disk item must be effective
 * 			| isValidItem(getItem())
 * @author 	Tommy Messelis
 * @version	6.1
 */
public class DiskItemNotWritableException extends RuntimeException {

	/**
	 * Required because this class inherits from Exception. 
	 * (which in its turn implements the interface Serializable)
	 */
	private static final long serialVersionUID = 5764443045080909433L;

	/**
	 * Variable referencing the disk item to which change was denied.
	 */
	private final ActualItem item;

	/**
	 * Check whether the fiven disk item is a valid ActualItem for this Exception.
	 * @param 	item
	 * 			The actual item to check
	 * @return	result == (item != null)
	 */
	public static boolean isValidItem(ActualItem item) {
		return item != null;
	}
	
	/**
	 * Initialize this new not writable exception involving the
	 * given disk item.
	 * 
	 * @param	item
	 * 			The disk item for the new not writable exception.
	 * @pre		The given item must be a valid actual disk item
	 * 			| isValidItem(item)
	 * @post	The disk item involved in the new not writable exception
	 * 			is set to the given disk item.
	 * 			| new.getItem() == item
	 */
	public DiskItemNotWritableException(ActualItem item) {
		this.item = item;
	}
	
	/**
	 * Return the disk item involved in this not writable exception.
	 */
	@Basic @Immutable
	public ActualItem getItem() {
		return item;
	}
	
}
