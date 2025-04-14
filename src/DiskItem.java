package filesystem;

import java.util.Date;
import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;

/**
 * An abstract class of disk items.
 *
 * @invar	Each disk item must have a properly spelled name.
 * 			| canHaveAsName(getName())
 * @invar   Each disk item must have a valid creation time.
 *          | isValidCreationTime(getCreationTime())
 * @invar   Each disk item must have a valid modification time.
 *          | canHaveAsModificationTime(getModificationTime())
 * @invar   Each disk item must have a valid parent directory.
 *          | hasProperParentDirectory()
 * 
 * @author 	Tommy Messelis      
 * @version 6.0
 * 
 * @note 	The name invariant has become object-dependent, so the name of 
 * 			the checker changed to canHaveAsX (instead of isValidX).
 * 			Although we do not rely on properties/attributes of the object itself 
 * 			to determine the validity of the name, it still is important 
 * 			which subclass the object belongs to.
 * 			Hence the object-dependency of the checker.
 * @note	The boolean property isTerminated accepts all possible values 
 * 			and does not require a checker. The other class invariants express 
 * 			their rules for both termination statuses, if applicable.
 *  
 */
public abstract class DiskItem {

	/**********************************************************
	 * Constructors
	 **********************************************************/
	
	/**
	 * Initialize a new disk item with given parent directory and name.
	 *   
	 * @param  	parent
	 *         	The parent directory of the new disk item.
	 * @param  	name
	 *         	The name of the new disk item.  
	 *         
	 * @effect  The name of the disk item is set to the given name.
	 * 			If the given name is not valid, a default name is set.
	 *          | setName(name) 
	 * @effect 	The parent directory of this disk item is set.
	 * 			| setParentDirectory(parent)
	 * 
	 * @post    The new creation time of this disk item is initialized to some time during
	 *          constructor execution.
	 *          | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *          | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post    The new disk item has no time of last modification.
	 *          | new.getModificationTime() == null
	 * @post    The new disk item is not terminated.
	 *          | !new.isTerminated()
	 *          
	 * @throws 	IllegalArgumentException
	 *         	The given parent directory is not allowed for this item.
	 *         	| !canHaveAsParentDirectory(parent)
	 * @throws 	DiskItemNotWritableException
	 *         	The given parent directory is effective but not writable.
	 *         	| parent != null && !parent.isWritable()
	 * @throws 	IllegalArgumentException
	 *         	The given name is valid and the given parent directory is effective, 
	 *          but already contains an item with the same (valid) name.
	 *          | parent != null && canHaveAsName(name) && 
	 *          |   parent.containsDiskItemWithName(name)
	 * @throws 	IllegalArgumentException
	 *         	The given name is not valid and the given parent directory is effective, 
	 *          but already contains an item with the default (valid) name.
	 *          | parent != null && !canHaveAsName(name) &&
	 *         	|   parent.containsDiskItemWithName(getDefaultName())
	 *         
	 * @note	The constructor is annotated raw because at the start of the execution, 
	 * 			not all fields have a default value that is accepted by the invariants. 
     * 			E.g. the parentDirectory is defaulted to null, which may not be allowed.
	 *
     * @note	In general, null parents are allowed. Subclasses for which this is not
     * 			the case will take care of this restriction themselves. They will overwrite
     * 			the canHaveAsParentDirectory(.) checker and by the power of dynamic binding, 
     * 			the right implementation is automatically used when executing the constructor from
     * 			within the subclasses.
	 */
	@Model @Raw
	protected DiskItem(Directory parent, String name) 
			throws IllegalArgumentException, DiskItemNotWritableException {									
		if (!canHaveAsParentDirectory(parent))
			throw new IllegalArgumentException("The given parent directory is not allowed.");
		if (parent != null && !parent.isWritable())
			throw new DiskItemNotWritableException(parent);
		if (parent != null && canHaveAsName(name) && parent.containsDiskItemWithName(name))
			throw new IllegalArgumentException("The given parent directory already contains an item with the given (valid) name.");
		if (parent != null && !canHaveAsName(name) && parent.containsDiskItemWithName(getDefaultName()))
			throw new IllegalArgumentException("The given name is invalid and the given parent directory already contains an item with the default (valid) name.");
		
		setName(name);
		// Please note that the name must be set before calling the setParentDirectory()!
		// Otherwise, we may end up in the wrong place within the (sorted collection within the) parent directory.
		try{
			setParentDirectory(parent);
		}catch(IllegalStateException e) {
			//should not occur
			assert false;
		}
	}
	
	/**
	 * Auxiliary constructor that initializes only the local fields of a disk item, not the relationships.
	 * 
	 * @param  	name
	 *         	The name of the new disk item.  
	 * 
	 * @post    The new creation time of this disk item is initialized to some time during
	 *          constructor execution.
	 *          | (new.getCreationTime().getTime() >= System.currentTimeMillis()) &&
	 *          | (new.getCreationTime().getTime() <= (new System).currentTimeMillis())
	 * @post    The new disk item has no time of last modification.
	 *          | new.getModificationTime() == null
	 * @post    The new disk item is not terminated.
	 *          | !new.isTerminated()
	 * @effect  The name of the disk item is set to the given name.
	 * 			If the given name is not valid, a default name is set.
	 *          | setName(name) 
	 *          
	 * @note	This constructor is required by the subclasses when they want to throw 
	 * 			other Exceptions in their constructors. It may only make local changes
	 * 			which will be unobservable in the universe (which may not be changed if
	 * 			that subclass constructor ends in an exception).
	 * 			The most important thing is that it may not change a parent directory.
	 * 			We are unable to call the constructor of this class with null
	 * 			as a parent, because this would result in an error if that null parent is
	 * 			not acceptable for the specific subclass (e.g. links)!
	 * 			This constructor is thus only meant to be used from such subclass constructors.
	 * 			We will leave the parent directory as null, which may violate the class
	 * 			invariants of those subclasses. But we may not throw an exception in that case!
	 * 			The subclass will be responsible for making sure the parent is set well.
	 */
	@Raw
	protected DiskItem(String name) {
		setName(name);
	}
	

	/**********************************************************
	 * Destructors: delete/termination
	 **********************************************************/

	/**
	 * Variable registering whether or not this disk item has been terminated. (Default = false)
	 */
	private boolean isTerminated = false;

	/**
	 * Check whether this disk item is already terminated.
	 */
	@Raw @Basic
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * Check whether this disk item can be terminated.
	 * 
	 * @return	False if the disk item is already terminated, or if it is not a root item 
	 * 			and its parent directory is not writable.
	 * 			| if (isTerminated() || (!isRoot() && !getParentDirectory().isWritable())
	 * 			| then result == false
	 * 
	 * @note	This specification must be left open s.t. the subclasses can change it.
	 * 			It does e.g. not say in which cases the answer is true.
	 * 			A fully closed description here would lead to contradictions in the subclasses.
	 */
	public boolean canBeTerminated(){
		return !isTerminated() && (isRoot() || getParentDirectory().isWritable());
	}

	/**
     * Check whether this disk item can be recursively deleted.
     *
     * @return	False if the disk item is already terminated, or if it is not a root item 
	 * 			and its parent directory is not writable.
	 * 			| if (isTerminated() || (!isRoot() && !getParentDirectory().isWritable())
	 * 			| then result == false
	 * 			
     * @note	This specification must be left open s.t. the subclasses can change it.
     * @note	At this level, there is no difference with the canBeTerminated method, but this will change in the subclasses.
     * 			We can not use the specification of canBeTerminated() here because of that.
     */
    public boolean canBeRecursivelyDeleted() {
    	return !isTerminated() && (isRoot() || getParentDirectory().isWritable());	
	}

    /**
	 * Terminate this disk item. 
	 * 
	 * @post 	This disk item is terminated.
	 *       	| new.isTerminated()
	 * @post    This item becomes a root item.
	 *          | new.isRoot()
	 * @effect	If the item is currently not a root item, 
	 * 			this item is removed from the current parent directory.
	 * 			| if !isRoot()
	 * 			| then getParentDirectory().removeAsItem(this)
	 *          
	 * @throws 	IllegalStateException
	 * 		   	This disk item can not be terminated.
	 * 		   	| !canBeTerminated()
	 * 
	 * @note	Unfortunately, we cannot write effect setParentDirectory(null) because
	 * 			this leads to a throws clause for items which do not allow null references 
	 * 			for the parent directory (unless they are terminated).
	 * 			This is the case for files and links.
	 * 			We can only call effects on the before status of an object, not on the new status.
	 * 			Ideally, we would like to express that we first set the termination status, 
	 * 			and then call the effect of setParentDirectory(null) on the intermediate status, 
	 * 			but we can't.
	 */
	public void terminate() throws IllegalStateException {
		if (!canBeTerminated()) {
			throw new IllegalStateException("This item cannot be terminated");
		}
		this.isTerminated = true;	
		// Please note that the termination status must be set before calling the setParentDirectory()!
		// Otherwise, we may end up with an exception because the null parent may not be allowed for certain subclasses.
		try{
			setParentDirectory(null);
		}catch(IllegalStateException e) {
			// Will not happen!
			assert false;
		}catch(IllegalArgumentException e) {
			// Will not happen!
			assert false;
		}
	}	
	
	/**
     * Delete this disk item recursively.  
     *
	 * @post 	This disk item is terminated.
	 *       	| new.isTerminated()
	 * @post    This item becomes a root item.
	 *          | new.isRoot()
	 * @effect	If the item is currently not a root item, 
	 * 			this item is removed from the current parent directory.
	 * 			| if !isRoot()
	 * 			| then getParentDirectory().removeAsItem(this)
	 *          
	 * @throws 	IllegalStateException
	 * 		   	This disk item can not be recursively deleted.
	 * 		   	| !canBeRecursivelyDeleted()
	 * 
	 * @note	This can not use the terminate method as an effect, 
	 * 			because the conditions are different.
	 * 			(canBeTerminated versus canBeRecursivelyDeleted)
	 * 			(Even though at this level both those methods do the same, 
	 * 			their specification is open and will change in the subclasses!)
	 * 
	 * @note 	Note how the power of object-orientation is exploited here. 
	 * 			This method throws an Exception in case the checker returns false.
	 * 			By the power of dynamic binding, the right version of this checker is used, 
	 * 			so all specific conditions of the subclass are already taken into account here, 
	 * 			even though we do not know here what these may be!
	 * 			If, in the future, we add more subclasses with more specific implementations, 
	 * 			we do not have to change anything in this class!
	 * 
     */    
    public void deleteRecursive() throws IllegalStateException {
    	if (!canBeRecursivelyDeleted()) {
			throw new IllegalStateException("This item cannot be recursively deleted");
		}
    	// take care here of everything that is common to all disk items.
    	this.isTerminated = true;	
    	try{
			setParentDirectory(null);
		}catch(IllegalStateException e) {
			// Will not happen!
			assert false;
		}catch(IllegalArgumentException e) {
			// Will not happen!
			assert false;
		}
    }
    
    
	/**********************************************************
	 * name - total programming
	 **********************************************************/

	/**
	 * Variable referencing the name of this disk item. (Default = default name)
	 * 
	 * @note	This is a different approach w.r.t. Practicum 1. 
	 * 			In P1, this would have allowed to delete the raw tag of the constructor,
	 * 			as all fields would then have a valid value by default. (All invariants
	 * 			would be satisfied when entering the constructor code.)
	 * 			In this version of the practicum, the parent may be null, 
	 * 			and this may not be allowed in certain subclasses, so the raw annotation 
	 * 			of the constructor is still needed here.
	 */
	private String name = getDefaultName();

	/**
	 * Return the name of this disk item.
	 */
	@Raw @Basic 
	public String getName() {
		return name;
	}

	/**
	 * Check whether this disk item can have the given name as its name.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	False if the name is not effective.
	 * 			| if (name == null || !name.matches("[a-zA-Z_0-9.-]+"))
	 * 			| then result == false
	 * 
	 * @note	This checker only checks the common stuff for all disk items. 
	 * 			Its specification is left open and has to be closed in the subclasses.
	 * @note	This checker checks only if the name is compliant with the stated rules, it does 
	 * 			not consider the content of the parent directory and it does not have to. 
	 * 			The rule about the uniqueness of names in a directory is only relevant for directory
	 * 			itself. An item should not be considered raw if it is contained in a directory 
	 * 			with another item that has the same name. Only that directory should be considered raw.
	 * @note	There is no distinction between terminated items and regular items w.r.t. the name
	 * 			conventions. 
	 */
	@Raw
	public boolean canHaveAsName(String name) {
		return (name != null && name.matches("[a-zA-Z_0-9.-]+"));
	}

	/**
	 * Set the name of this disk item to the given name.
	 *
	 * @param   name
	 * 			The new name for disk item file.
	 * @post    If the given name is valid, the name of
	 *          this disk item is set to the given name,
	 *          otherwise, the name of the disk item is set to a valid default name.
	 *          | if (canHaveAsName(name))
	 *          | then new.getName().equals(name)
	 *          | else new.getName().equals(getDefaultName())
	 *          
	 * @note	This is a different approach w.r.t. Practicum 1.
	 */
	@Raw @Model 
	private void setName(String name) {
		if (canHaveAsName(name)) {
			this.name = name;
		} else {
			this.name = getDefaultName();
		}
	}

	/**
	 * Return the name for a new disk item which is to be used when the
	 * given name is not valid.
	 *
	 * @return	A valid disk item name.
	 *         	| canHaveAsName(result) && result.equals("new_disk_item")
	 *         
	 * @note	The inspector is object-dependent and can be overwritten in the subclasses.
	 * 			That will however not be needed because of the specific implementation.
	 * @note	We need to be more specific w.r.t. the default name, because we need
	 * 			to know whether or not this name already exists in the parent directory
	 * 			in order to verify the conditions of the exceptions in the constructor.
	 * 			Also, it determines the order of the items in the parent directory, so
	 * 			it needs to be explicitly defined in order to verify the other methods.
	 */
	@Model
	protected String getDefaultName() {
		return "new_disk_item";
	}
	
	
	/**
	 * Change the name of this disk item to the given name.
	 *
	 * @param	name
	 * 			The new name for this disk item.
	 * 
	 * @effect  The name of this disk item is set to the given name.
	 *          | setName(name)
	 * @effect  The modification time is updated.
	 *          | setModificationTime()
	 * @effect  If this disk item is not a root item, the order of the items in the parent
	 * 			directory is restored, given the updated name of this item.
	 * 			In its turn, this also sets the modification time of the parent directory.
	 * 			| if (!isRoot())  
	 * 			| then getParentDirectory().restoreOrderAfterNameChangeAt(getParentDirectory().getIndexOf(this))
	 *          
	 * @throws 	IllegalStateException
	 * 			This disk item is already terminated
	 * 			| isTerminated()
	 * @throws  DiskItemNotWritableException(this) [can]
	 *          | ? true
	 * @throws  DiskItemNotWritableException(getParentDirectory())
	 *          This disk item is not a root item and the parent directory is not writable.
	 *          | !isRoot() && !getParentDirectory().isWritable()
	 * @throws 	IllegalArgumentException
	 * 			The given name is valid, but this item is not a root item and 
	 * 			its parent directory already contains an item with the given name.
	 * 			| !isRoot() && canHaveAsName(name) && getParentDirectory().containsDiskItemWithName(name)
	 * @throws 	IllegalArgumentException
	 * 			The given name is not valid, and this item is not a root item and 
	 * 			its parent directory already contains an item with the default name.
	 * 			| !isRoot() && !canHaveAsName(name) && getParentDirectory().containsDiskItemWithName(getDefaultName())
	 * 
	 * @note	We cannot (yet) nicely specify the conditions under which this method can
	 * 			throw a DiskItemNotWritableException, we must however already mention this
	 * 			Exception here, because Liskov prohibits us to add exceptions in subclasses!
	 * 			Please note the [can] and the '?' in the clause above!
	 */
	public void changeName(String name) throws DiskItemNotWritableException, IllegalStateException, IllegalArgumentException {
		if (isTerminated()) 
			throw new IllegalStateException("Disk item terminated!");
		if (!isRoot() && !getParentDirectory().isWritable()) 
			throw new DiskItemNotWritableException(getParentDirectory());
		if (!isRoot() && canHaveAsName(name) && getParentDirectory().containsDiskItemWithName(name))
			throw new IllegalArgumentException("Parent directory already contains this name.");
		if (!isRoot() && !canHaveAsName(name) && getParentDirectory().containsDiskItemWithName(getDefaultName()))
			throw new IllegalArgumentException("Invalid name and parent directory already contains the default name.");
		
		// setName will take any name and still change the name of this item to a valid name. (TOTAL PROGRAMMING)
		setName(name);
		if(!isRoot()){
			int currentIndexInParent = getParentDirectory().getIndexOf(this);
			try {
				getParentDirectory().restoreOrderAfterNameChangeAt(currentIndexInParent);
			}catch(IndexOutOfBoundsException e) {
				//impossible
				assert false;
			}
			
		}
		setModificationTime();
	}

	
	/**
	 * Check whether the name of this item is lexicographically 
	 * ordered after the given name, ignoring case.
	 * 
	 * @param 	name
	 *       	The name to compare with
	 * @return 	True if the given name is effective and the name of this item 
	 * 			comes strictly after the given name (ignoring case), 
	 *         	false otherwise.
	 *       	| result == (name != null) && (getName().compareToIgnoreCase(name) > 0)
	 */
	public boolean isOrderedAfter(String name) {
		return (name != null) && (getName().compareToIgnoreCase(name) > 0);
	}

	/**
	 * Check whether the name of this item is lexicographically 
	 * ordered before the given name, ignoring case.
	 * 
	 * @param 	name
	 *       	The name to compare with
	 * @return 	True if the given name is effective and the name of this item 
	 * 			comes strictly before the given name (ignoring case), 
	 *         	false otherwise.
	 *       	| result == (name != null) && (getName().compareToIgnoreCase(name) < 0)
	 */
	public boolean isOrderedBefore(String name) {
		return (name != null) && (getName().compareToIgnoreCase(name) < 0);
	}

	/**
	 * Check whether this item is ordered after the given other item
	 * according to the lexicographic ordering of their names,
	 * ignoring case.
	 * 
	 * @param 	other
	 *        	The item to compare with
	 * @return 	True if the given other item is effective, and the name
	 *         	of this item is lexicographically ordered after the name
	 *         	of the given other item (ignoring case),
	 *         	false otherwise.
	 *       	| result == (other != null) && 
	 *       	|           isOrderedAfter(other.getName())
	 */
	public boolean isOrderedAfter(@Raw DiskItem other) {									
		return (other != null) && isOrderedAfter(other.getName());
	}

	/**
	 * Check whether this item is ordered before the given other item
	 * according to the lexicographic ordering of their names,
	 * ignoring case.
	 * 
	 * @param 	other
	 *        	The item to compare with
	 * @return 	True if the given other item is effective, and the name
	 *         	of this item is lexicographically ordered before the name
	 *         	of the given other item (ignoring case),
	 *         	false otherwise.
	 *       	| result == (other != null) && 
	 *       	|           isOrderedBefore(other.getName())
	 */
	public boolean isOrderedBefore(@Raw DiskItem other) {
		return (other != null) && isOrderedBefore(other.getName());
	}
	
	
	/**
	 * Returns the absolute path of the disk item.
	 * 
	 * @return	The path of the item.
	 * 			| result != null
	 * 
	 * @note	There's not much more to say other than 
	 * 			the result will be an effective String.
	 */
	public abstract String getAbsolutePath();


	/**********************************************************
	 * creationTime
	 **********************************************************/

	/**
	 * Variable referencing the time of creation. 
	 * (Default and final value = time of construction)
	 */
	private final Date creationTime = new Date();

	/**
	 * Return the time at which this disk item was created.
	 */
	@Raw @Basic @Immutable 
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Check whether the given date is a valid creation time.
	 *
	 * @param  	date
	 *         	The date to check.
	 * @return 	True if and only if the given date is effective and not
	 * 			in the future.
	 *         	| result == 
	 *         	| 	(date != null) &&
	 *         	| 	(date.getTime() <= System.currentTimeMillis())
	 *         
	 * @note	This checker is object-independent (and thus static).
	 * 
	 */
	public static boolean isValidCreationTime(Date date) {
		return 	(date!=null) &&
				(date.getTime()<=System.currentTimeMillis());
	}
	

	/**********************************************************
	 * modificationTime
	 **********************************************************/

	/**
	 * Variable referencing the time of the last modification,
	 * possibly null. (Default = null)
	 */
	private Date modificationTime = null;

	/**
	 * Return the time at which this disk item was last modified, that is
	 * at which the name or content was last changed. If this disk item has
	 * not yet been modified after construction, null is returned.
	 */
	@Raw @Basic
	public Date getModificationTime() {
		return modificationTime;
	}

	/**
	 * Check whether this disk item can have the given date as modification time.
	 *
	 * @param	date
	 * 			The date to check.
	 * @return 	True if and only if the given date is either not effective
	 * 			or if the given date lies between the creation time and the
	 * 			current time.
	 *         | result == (date == null) ||
	 *         | ( (date.getTime() >= getCreationTime().getTime()) &&
	 *         |   (date.getTime() <= System.currentTimeMillis())     )
	 *         
	 * @note	This checker does not distinguish between terminated and regular items.
	 */
	@Raw
	public boolean canHaveAsModificationTime(Date date) {
		return (date == null) ||
				( (date.getTime() >= getCreationTime().getTime()) &&
						(date.getTime() <= System.currentTimeMillis()) );
	}

	/**
	 * Set the modification time of this disk item to the current time.
	 *
	 * @post   The new modification time is effective.
	 *         | new.getModificationTime() != null
	 * @post   The new modification time lies between the system
	 *         time at the beginning of this method execution and
	 *         the system time at the end of method execution.
	 *         | (new.getModificationTime().getTime() >=
	 *         |                    System.currentTimeMillis()) &&
	 *         | (new.getModificationTime().getTime() <=
	 *         |                    (new System).currentTimeMillis())
	 */
	@Model @Raw
	protected void setModificationTime() {
		modificationTime = new Date();
	}

	/**
	 * Return whether this disk item and the given other disk item have an
	 * overlapping use period.
	 *
	 * @param 	other
	 *        	The other disk item to compare with.
	 * @return 	False if the other disk item is not effective
	 * 			False if the prime object does not have a modification time
	 * 			False if the other disk item is effective, but does not have a modification time
	 * 			otherwise, true if and only if the open time intervals of this disk item and
	 * 			the other disk item overlap
	 *        	| if (other == null) then result == false else
	 *        	| if ((getModificationTime() == null)||
	 *        	|       other.getModificationTime() == null)
	 *        	|    then result == false
	 *        	|    else 
	 *        	| result ==
	 *        	| ! (getCreationTime().after(other.getModificationTime()) || 
	 *        	|	 getModificationTime().before(other.getCreationTime()) ) 
	 */
	public boolean hasOverlappingUsePeriod(DiskItem other) {
		if (other == null) return false;
		if(getModificationTime() == null || other.getModificationTime() == null) return false;
		return ! (this.getCreationTime().after(other.getModificationTime()) || this.getModificationTime().before(other.getCreationTime()));
	}


	/**********************************************************
	 * parent directory
	 **********************************************************/	

	/**
	 * Variable referencing the directory (if any) to which this 
	 * disk item belongs. (Default = null)
	 * 
	 * @note 	This class is the controlling class for the bidirectional relationship.
	 * @note	The default value may not be allowed for certain subclasses.
	 * 			This demands a raw annotation for the constructors.
	 */
	private Directory parentDirectory = null;

	/** 
	 * Check whether this disk item can have the given directory as
	 * its parent directory.
	 * 
	 * @param  	directory
	 *          The directory to check.
	 * @return  If this disk item is terminated, 
	 * 			then true if and only if the given directory is not effective
	 *          | if (this.isTerminated()) 
	 *          | then result == (directory == null)
	 * @return  If the given directory is effective but terminated, then false
	 * 			| if (directory != null && directory.isTerminated()) then result == false
	 * 
	 * @note	The remaining checks are dependent on the type of disk item we are considering, 
	 * 			so we include them on the level of the subclasses. We must therefore leave the
	 * 			specification of this checker open on this level. 
	 * 			(Here, we do not specify an answer in all other cases, so no contradictions 
	 * 			can occur when combined with the specification in the subclasses.)
	 * @note	This checker considers only the content restrictions of the parent directory.
	 * 			It does not check the consistency of the bidirectional relationship.
	 * 			It can be used to check if the current parent directory is valid, 
	 * 			or if a given directory can be accepted as a (new) parent directory.
	 * @note	This checker expresses that terminated items may not refer to a parent directory.
	 * 			Items which are not terminated abide to other rules. They may e.g. not refer to a 
	 * 			terminated directory as their parent. The subclasses can (and will) further extend these rules.
	 */
	@Raw 
	public boolean canHaveAsParentDirectory(Directory directory) {
		if (this.isTerminated())
			return (directory == null);
		if (directory != null && directory.isTerminated())
			return false;
		// Note that the implementation requires an answer in all cases.
		return true;
	}
	
	/** 
	 * Check whether this disk item has a proper parent directory as
	 * its parent directory.
	 * 
	 * @return  True if:
	 * 			 - the current parent directory is a valid parent directory for this item, and
	 * 			 - this item is either a root item, or its parent has registered this item in its contents,
	 * 			false otherwise.
	 *          | result == canHaveAsParentDirectory(getParentDirectory()) &&
	 *			|            (isRoot() || getParentDirectory().hasAsItem(this))
	 *
	 * @note	This checker is split up in two parts. The consistency of the 
	 *			bidirectional relationship is added to the functionality of 
	 *			the internal state checker (canHaveAsParentDirectory(.)).
	 */
	@Raw 
	public boolean hasProperParentDirectory() {
		return canHaveAsParentDirectory(getParentDirectory()) &&
				(isRoot() || getParentDirectory().hasAsItem(this));
	}
	
	
	/**
	 * Return the parent directory of this disk item (if any).
	 */
	@Raw @Basic
	public Directory getParentDirectory() {
		return parentDirectory;
	}

	/**
	 * Set the parent directory of this item to the given directory.
	 * This setter maintains the bidirectional relationship in both directions
	 * and ensures that invariants on both ends are satisfied.
	 *
	 * @param   directory
	 *          The new parent directory for this item.
	 *          
	 * @post    The parent directory of this item is set to the given 
	 *          directory.
	 *          | new.getParentDirectory() == directory
	 * 
	 * @effect	If the item is currently not a root item and the given directory is different 
	 * 			from the current parent directory, this item is removed from the current parent directory.
	 * 			| if (!isRoot() && getParentDirectory() != directory)
	 * 			| then getParentDirectory().removeItemAt(this.getParentDirectory().getIndexOf(this))
	 * @effect	If the item is currently not a root item and the given directory is different 
	 * 			from the current parent directory, the modification time of the current parent is set.
	 * 			| if (!isRoot() && getParentDirectory() != directory)
	 * 			| then getParentDirectory().setModificationTime()
	 * 
	 * @effect	If the given directory is effective and not yet registered as 
	 * 			the current parent of this item, this item is added to the directory.
	 * 			| if (directory != null && getParentDirectory() != directory)
	 * 			| then directory.addItemAt(this,directory.getInsertionIndexOf(this))
	 * @effect	If the given directory is effective and not yet registered as 
	 * 			the current parent of this item, the modification time of the given directory is set.
	 * 			| if (directory != null && getParentDirectory() != directory)
	 * 			| then directory.setModificationTime()
	 * 
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated and the directory is not null.
	 * 			| isTerminated() && (directory != null)
	 * @throws  IllegalArgumentException
	 *          This item cannot have the given directory as its
	 *          parent directory.
	 *          | !canHaveAsParentDirectory(directory)
	 * @throws  IllegalArgumentException
	 *          The directory is effective, but cannot have this item in its contents.
	 *          | (directory != null) && !directory.canHaveAsItem(this)
	 * 
	 * @note	The setter is only responsible to satisfy the invariants w.r.t. the bidirectional relationship.
	 * 			It ensures both the consistency of the relationship and
	 * 			the restrictions on the actual referenced parent.
	 * 
	 * @note	The exception clauses that come in through the effects are all
	 * 			cancelled out by the throws clauses here.
	 * 
	 * @note	Note that we cannot simply include the effect of the addAsItem and removeAsItem methods here.
	 * 			Those auxiliary methods are highly restricted and throw errors in case the bidirectional 
	 * 			relationship is not yet partly set up/torn down.
	 * 			E.g. if we want to add an item to a parent, the addAsItem method requires this item to already 
	 * 			have set up its reference to the new parent.
	 * 			If we would include an effect of that method here, this would result in stating that we would 
	 * 			throw an IllegalStateException, because at the start of this setParentDirectory, that reference 
	 * 			is not yet set up! We have to copy the postconditions/effects of the addAsItem method, 
	 * 			without this specific throws clause, to correctly state the effect of setParentDirectory!  
	 */
	@Raw @Model
	protected void setParentDirectory(Directory directory)									
			throws IllegalArgumentException, IllegalStateException {		
		if (isTerminated() && directory != null) 
			throw new IllegalStateException("Diskitem is terminated, cannot accept anything else than null!");
		if (!canHaveAsParentDirectory(directory))
			throw new IllegalArgumentException("The given parent directory is not allowed for this item!");
		if (directory != null && !directory.canHaveAsItem(this))
			throw new IllegalArgumentException("This item is not allowed by the given parent directory!");
		
		// Remember the old parent directory
		Directory oldParent = getParentDirectory();
		
		// First, set up / break down the relationship from this side:
		this.parentDirectory = directory;
		
		// Then, break down the old relationship from the other side, if it existed
		if (oldParent != null) {
			try{ 
				oldParent.removeAsItem(this);
				// the prime object is now in a raw state!
			}catch(IllegalArgumentException e) {
				// Should never occur!
				assert false;
			}
		}
		
		// Finally, set up the new relationship from the other side, if needed
		if (directory != null) {
			try{
				directory.addAsItem(this);
			}catch(IllegalArgumentException e) {
				// Should never occur!
				assert false;
			}
		}
	}
	
	/**
	 * Return the root item to which this item directly or indirectly
	 * belongs. In case this item is a root item, the item itself is 
	 * the result.
	 * 
	 * @return 	If this item is a root item, this item is returned,
	 *         	otherwise the root of the parent item is returned.
	 *         	| if (isRoot())
	 *         	| then result == this
	 *         	| else result == getParentDirectory().getRoot()
	 * 
	 * @note	The return type of this inspector is a DiskItem and not a Directory.
	 * 			This is necessary because terminated items are all root items.
	 */
	public DiskItem getRoot() {
		if (isRoot()) {
			return this;
		} else {
			return getParentDirectory().getRoot();
		}
	}
	
	/**
	 * Check whether this item is a root item.
	 * 
	 * @return  True if this item has a non-effective parent directory;
	 *          false otherwise.
	 *        	| result == (getParentDirectory() == null)
	 */
	@Raw
	public boolean isRoot() {
		return getParentDirectory() == null;
	}
	
	/**
	 * Move this disk item to the given directory.
	 * 
	 * @param   target
	 *          The target directory.
	 *          
	 * @effect  The parent directory of this item is set.
	 * 			| setParentDirectory(target)
	 * @effect	The modification time of this item is set.
	 * 			| setModificationTime()
	 * 
	 * @throws 	IllegalStateException
	 * 			This disk item is terminated.
	 * 			| isTerminated()
	 * @throws  DiskItemNotWritableException(this) [can]
	 *          | ? true
	 * @throws	DiskItemNotWritable(getParentDirectory())	
	 * 			This disk item is not a root item and its parent directory is not writable.
	 * 			| !isRoot() && !getParentDirectory().isWritable()
	 * @throws	DiskItemNotWritable(target)	
	 * 			The target directory is not writable.
	 * 			| !target.isWritable()
	 * @throws	IllegalArgumentException
	 * 			The given target directory is not effective or it is already registered as the parent directory.
	 * 			| (target == null) || (target == this.getParentDirectory())
	 *
	 * @note	The conditions for the move method are also dependent on the subclass, some items must be writable,
	 * 			for others, this is irrelevant (writability not defined).
	 * 			We announce a [can] exception here and will specify in the corresponding subclass when this will occur.
	 * 			An alternative approach would be to introduce a checker canBeMoved() and 
	 * 			provide the right implementations in the several subclasses.
	 * @note	Note that a terminated target directory is actually also checked in the canHaveAsParentDirectory() checker,
	 * 			which is included through the effect clause of setParentDirectory(target).
	 * 			(In case several throws conditions are satisfied, one of the possible exceptions must be thrown.) 
	 */
	public void move(Directory target) 
			throws IllegalArgumentException, DiskItemNotWritableException, IllegalStateException {
		
		// is this item ok?
		if (isTerminated()) 
			throw new IllegalStateException("This disk item is terminated!");
		if (!isRoot() && !getParentDirectory().isWritable())
			throw new DiskItemNotWritableException(this.getParentDirectory());
		// is the target ok?
		if (target == null)
			throw new IllegalArgumentException("The target directory is non-effective.");
		if (!target.isWritable())
			throw new DiskItemNotWritableException(target);
		if (target == this.getParentDirectory())
			throw new IllegalArgumentException("The target directory is already the parent directory.");
		
		// then move!
		setParentDirectory(target);
		// setParent may have thrown other exceptions if e.g. !canHaveAsParentDirectory(target) or !target.canHaveAsItem(this).
		setModificationTime();

	}

	/**
	 * Checks whether this item is a direct or indirect child item of the given directory.
	 * 
	 * @param 	directory
	 * 			The directory to check.
	 * 
	 * @return  If this is a root item, then false
	 * 			| if this.isRoot() then result == false
	 * @return	If the given directory is non-effective, then false
	 * 			| if directory == null then result == false
	 * @return	If this is not a root item and the given directory is effective,
	 * 			then true if this item is a direct or indirect child of the given directory,
	 * 				 false otherwise.
	 * 			| if (!this.isRoot() && directory != null)
	 * 			| then result == ( this.getParentDirectory() == directory ||
	 * 			|					this.getParentDirectory().isDirectOrIndirectChildOf(directory) )
	 * 
	 */
	public boolean isDirectOrIndirectChildOf(Directory directory) {
		if (this.isRoot() || directory == null)
			return false;
		else
			return this.getParentDirectory() == directory || this.getParentDirectory().isDirectOrIndirectChildOf(directory);
	}
	
	
	/**********************************************************
	 * disk usage
	 **********************************************************/

	/**
	 * Returns the total disk usage of this disk item.
	 * 
	 * @return	the disk usage in bytes (always positive)
	 * 			| result >= 0
	 * 
	 * @note	We can say here that the result will be positive, 
	 * 			there's not much more known at this level. 
	 * 			(It is not really necessary to say this here.)
	 * @note	Since files can have an arbitrary size (Integer.MAX_VaALUE),
	 * 			We must allow for larger total disk usage numbers.
	 * 			We can use the long Datatype for this.
	 */
	public abstract long getTotalDiskUsage();


}
