package filesystem;

import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;


/**
 * A class of actual disk items, involving writability rights.
 * These items can either be files or directories, not links.
 * 
 * @note 	no additional invariants, only writability is added at this level.
 * 			there are only 2 possible values for this field, and both are always allowed.
 * 			(so no extra invariant needed to express this)
 * 
 * @author 	Tommy Messelis
 * @version	6.1
 */
public abstract class ActualItem extends DiskItem {

	/**********************************************************
     * Constructors
     **********************************************************/ 
	
	/**
	 * Initialize a new disk item with given parent directory, name and 
	 * writability status.
	 *   
	 * @param  	parent
	 *         	The parent directory of the new actual disk item.
	 * @param  	name
	 *         	The name of the new actual disk item.  
	 * @param  	writable
	 *         	The writability of the new actual disk item.
	 *         
	 * @effect 	The actual disk item is initialized as a disk item 
	 * 			(parent, name, creation time, modification time and termination status are set)
	 * 			| super(parent, name)
	 * 
	 * @effect	The writability is set to the given flag
	 * 			| setWritable(writable)
	 *
	 */
	@Model @Raw
	protected ActualItem(Directory parent, String name, boolean writable) 
			throws IllegalArgumentException, DiskItemNotWritableException {
		// the call of a superconstructor is ALWAYS the first operation in a constructor.
		super(parent, name);		
		setWritable(writable);
	}

	
	/**********************************************************
	 * Destructors: delete/termination
	 **********************************************************/

	/**
	 * Check whether this disk item can be terminated.
	 * 
	 * @return	False if the disk item is not writable
	 * 			| if(!isWritable())
	 * 			| then result == false
	 * 
	 * @note	We add a new condition under which the result is false. The clause is still
	 * 			not conclusive in other cases, it will become so in the subclasses.
	 * 			
	 */
	@Override
    public boolean canBeTerminated(){
		// we call the implementation of the superclass and add a clause
		return super.canBeTerminated() && isWritable();
	}
	
	/**
     * Check whether this disk item can be recursively deleted.
     *
     * @return	False if the disk item is not writable
	 * 			| if(!isWritable())
	 * 			| then result == false
	 * 
     * @note	We add a new condition under which the result is false. The clause is still
	 * 			not conclusive in other cases, it will become so in the subclasses.
     */
	@Override
    public boolean canBeRecursivelyDeleted() {
		// we call the implementation of the superclass and add a clause
		return super.canBeRecursivelyDeleted() && isWritable();	
	}

	
	/**********************************************************
	 * writable
	 **********************************************************/

	/**
	 * Variable registering whether or not this disk item is writable. (Default = true)
	 */
	private boolean isWritable = true;

	/**
	 * Check whether this disk item is writable.
	 */
	@Basic
	public boolean isWritable() {
		return isWritable;
	}

	/**
	 * Set the writability of this disk item to the given writability.
	 *
	 * @param isWritable
	 *        The new writability
	 * @post  The given writability is registered as the new writability
	 *        for this disk item.
	 *        | new.isWritable() == isWritable
	 */
	@Raw 
	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}

		
	/**********************************************************
	 * name
	 **********************************************************/
	
	/**
	 * Change the name of this disk item to the given name.
	 * 
	 * @throws 	DiskItemNotWritableException(this) [must]
	 * 			The actual disk item is not writable
	 * 			| !isWritable()
	 *
	 * @note	The conditions under which this method can throw a DiskItemNotWritableExcpetion
	 * 			are now specifiable. The exception turned into a [must] throw exception!
	 * 			Please note that the [can] and the '?' have been removed from the clause.
	 */
	@Override
	public void changeName(String name) throws DiskItemNotWritableException, IllegalStateException {
		if(!isWritable())
			throw new DiskItemNotWritableException(this);
		super.changeName(name);
	}
	
	
	/**********************************************************
	 * parent directory
	 **********************************************************/
	
	/**
	 * Move this disk item to a given directory.
	 * 
	 * @param   target
	 *          The target directory.
	 * 
	 * @throws  DiskItemNotWritableException(this) [must]
	 *          | !isWritable()
	 * 
	 * @note	Here we add the condition on the exception. (According to Liskov's rules.)			
	 */
	@Override
	public void move(Directory target) 
			throws IllegalArgumentException, DiskItemNotWritableException, IllegalStateException {
		
		if (!isWritable()) 
			throw new DiskItemNotWritableException(this);
		// move using the superclass method
		super.move(target);
	}
	
	
}
