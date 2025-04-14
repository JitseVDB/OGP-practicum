package filesystem;

import be.kuleuven.cs.som.annotate.*;
import filesystem.exception.*;

/**
 * A class representing links to actual items
 * 
 * @invar	the linked item is valid
 * 			| isValidLinkedItem(getLinkedItem())
 * 
 * @author 	Tommy Messelis
 * @version	6.0
 */
public class Link extends DiskItem {

	/**********************************************************
     * Constructors
     **********************************************************/ 
	
	/**
	 * Initialize a new link in a given parent directory, with the given name
	 * and referencing the given item
	 * 
	 * @param 	parent
	 * 			the parent directory of this link
	 * @param 	name
	 * 			the name of this link
	 * @param 	linkedItem
	 * 			the actual item which this link refers to
	 * 
	 * @effect 	The actual disk item is initialized as a disk item 
	 * 			(parent, name, creation time, modification time and termination status are set)
	 * 			| super(parent, name)
	 * 
	 * @post    The given item is registered as the linked item
	 * 			| new.getLinkedItem() == linkedItem
	 * @post	The new link is valid after construction
	 * 			| new.isValidLink()
	 * 
	 * @throws 	IllegalArgumentException
	 * 			The linkedItem is null or terminated
	 * 			| linkedItem == null || linkedItem.isTerminated()
	 * 
	 * @note	Please note that a null value as parent directory is prohibited through
	 * 			the effect of the superconstructor. This effect includes the throws
	 * 			clause in case of an invalid parent (!canHaveAsParentDirectory(.)).
	 * 			Through dynamic binding, the correct version of that checker is applied!    
	 */
	@Raw
	public Link(Directory parent, String name, ActualItem linkedItem)
			throws IllegalArgumentException, DiskItemNotWritableException {
		
		super(name); // Java requires the first statement of a constructor to be a call to a superconstructor!
		/* 
		 * Please note that we would rather call the superconstructor that already sets up the 
		 * bidirectional relationship to the parent. (We do specify the effect of that superconstructor!)
		 * However, that constructor would already add this link to the parent directory and change its 
		 * modification time.
		 * In case we have an invalid linkedItem here, we must throw an exception and ensure that the
		 * universe has not been changed. We could try and break down the parent relationship (this removes the 
		 * link from the parent), but we're unable to reset the modification time of the parent!
		 * The universe would thus be changed beyond repair.
		 * 
		 * As a solution, we call the superconstructor that only takes the name argument. 
		 * Doing so, we do not make any observable changes to the universe before checking the linked item
		 * from within this constructor. 
		 * We can then simply check and throw exceptions when needed, or initialize the object further 'manually'.
		 * 
		 * Unfortunately, this is how it's done in Java. It may be more efficiently solved in other languages
		 * that do not require the superconstructor call to be the first statement.
		 * 
		 * Alternatively, in Java, we could use design patterns to solve this problem.
		 * Instead of calling the constructor for Links, we could provide a static method that will deliver a new Link item.
		 * That static method could perform all checks prior to initializing a new object (using a private Link constructor).
		 * Design patterns will be a topic in later courses. For now, we can solve the problem with a restricted supersconstructor.
		 */
			
		if (linkedItem == null) 
			throw new IllegalArgumentException("null is not allowed as a linked item.");		
		if (linkedItem.isTerminated())			
			throw new IllegalArgumentException("A terminated item may not be linked.");
		if (!canHaveAsParentDirectory(parent))
			throw new IllegalArgumentException("The given parent directory is not allowed.");
		if (parent != null && !parent.isWritable())
			throw new DiskItemNotWritableException(parent);
		if (parent != null && canHaveAsName(name) && parent.containsDiskItemWithName(name))
			throw new IllegalArgumentException("The given parent directory already contains an item with the given (valid) name.");
		if (parent != null && !canHaveAsName(name) && parent.containsDiskItemWithName(getDefaultName()))
			throw new IllegalArgumentException("The given name is invalid and the given parent directory already contains an item with the default (valid) name.");
		
		this.linkedItem = linkedItem; 
		try{
			setParentDirectory(parent);
		}catch(IllegalStateException e) {
			//should not occur
			assert false;
		}
	}
	
	
	/**********************************************************
	 * Destructors: delete/termination
	 **********************************************************/

	/**
	 * Check whether this link can be terminated.
	 * 
	 * @return	True if the link is not yet terminated and its parent directory is writable,
	 * 			false otherwise.
	 * 			| result == !isTerminated() && (isRoot() || getParentDirectory().isWritable()())
	 * 
	 * @note	This specification is now be closed.
	 * @note	We must override this method here, as we must complete the return clause
	 * 			of the superclass.
	 * @note	Note that isRoot() will always be false for non-terminated links.
	 */
    @Override
    public boolean canBeTerminated(){
    	// no additional implementation required
    	// but only because we know the implementation in 
    	// the superclass is sufficient (because we implemented it)
    	return super.canBeTerminated();
	}
    
    
    /*
     * Please note that we might want to provide an overridden 
     * implementation for the terminate method too. It makes sense
     * to break down the uni-directional relationship to the linkeditem.
     * However, the variable is denoted final, so we can't overwrite
     * its value with a null-reference.
     * 
     * In case of a uni-directional relationship, this is not a big problem.
     * There can be no inconsistencies with such relationships.
     */
	
    /**
     * Check whether this link can be recursively deleted. 
     * For links this is equivalent to being terminatable.
     *
     * @return	True if this link can be terminated.
	 * 			| result == canBeTerminated()
	 * 
     * @note	This is a fully conclusive specification of the result.
     */
	@Override
    public boolean canBeRecursivelyDeleted(){
    	return canBeTerminated();
    }
    
	
	/**********************************************************
	 * Name
	 **********************************************************/
	
	/**
	 * Check whether the given name is a legal name for a link.
	 * 
	 * @param  	name
	 *			The name to be checked
	 *
	 * @return	True if the given string is effective, not
     * 			empty and consisting only of letters, digits, dots,
     * 			hyphens and underscores; false otherwise.
     * 			| result ==
     * 			|	(name != null) && name.matches("[a-zA-Z_0-9.-]+")
	 * 	
	 * @note	This is a closed form and it does not contradict the inherited documentation.		
	 */
	@Override
	@Raw
	public boolean canHaveAsName(String name) {
		// the implementation of the superclass suffices!
		return super.canHaveAsName(name);
	}
	
	
	/**
	 * Returns the absolute path of this link.
	 * 
	 * @return	If this is a root item, the name of this item is returned preceded with a '/'
	 * 			otherwise the name of this item, preceded with a '/' and the absolute path 
	 * 			of the parent directory is returned.
	 * 			| if (isRoot())
	 * 			| then result.equals("/" + getName())
	 * 			| else result.equals(getParentDirectory().getAbsolutePath() + "/" + getName())
	 */
	@Override
	public String getAbsolutePath(){
		if(isRoot()) return "/"+getName();
		else return getParentDirectory().getAbsolutePath() + "/" + getName();	
	}

	
	
	/**********************************************************
	 * linkedItem
	 **********************************************************/
	
	/**
	 * variable referencing the linked item. (final, so no default)
	 */
	private final ActualItem linkedItem;
	
	/**
	 * Check whether this linked item is valid.
	 * 
	 * @param	item
	 * 			The item to check
	 * @return	true if this link can have the given item as a linked item
	 * 			that is, when it is not null
	 * 			| result == (item != null) 
	 * @note	We can not expect the item to be not terminated, as deletion does not affect links!
	 * 			That is why a second checker is introduced to check whether a link is still valid.
	 */
	public static boolean isValidLinkedItem(ActualItem item) {
		return item != null;
	}
	
	/**
	 * returns the linkedItem.
	 */
	@Basic @Raw
	public final ActualItem getLinkedItem() {
		return linkedItem;
	}
	
	/**
	 * Check whether this link is still valid.
	 * (Not to be confused with whether the linked item is valid (allowed).)
	 * 
	 * @return	True if this link is not terminated and the 
	 * 			linkedItem is not yet terminated.
	 * 			| result == !isTerminated() && !getLinkedItem().isTerminated()
	 */
	public boolean isValidLink() {
		return !isTerminated() && !getLinkedItem().isTerminated();
	}

	
	/**********************************************************
	 * parent directory
	 **********************************************************/
	
	/** 
	 * Check whether this link can have the given directory as
	 * its parent directory.
	 * 
	 * @param  	directory
	 *          The directory to check.
	 * 
	 * @return  If this link is not terminated and the given directory is not effective, then false.
	 * 			| if (!isTerminated() && directory == null) then result == false
	 * @return 	If this link is not terminated, and the given directory is effective and not terminated,
	 * 			then true.
	 * 			| if (!this.isTerminated() && directory != null && !directory.isTerminated())
	 * 			|	then result == true
	 * 
	 * @note	This checker now closes the specification of the inherited checker by specifying the result in all
	 * 			other cases than described at the level of the superclass.
	 * 			In case the disk item is a link, any effective and non-terminated parent directory is allowed.
	 * 			The other cases are inherited from the superclass.
	 */
	@Raw @Override
	public boolean canHaveAsParentDirectory(Directory directory) {
		if (!isTerminated() && directory == null)
			return false;
		// in the other case, the implementation in the superclass suffices
		return super.canHaveAsParentDirectory(directory);
	}
	
	/**********************************************************
	 * disk usage
	 **********************************************************/

	/**
	 * Returns the total disk usage of this link.
	 * 
	 * @return	the disk usage of a link: 0 bytes
	 * 			| result == 0
	 */
	@Override
    public long getTotalDiskUsage(){
		return 0L;
	}	
	
}
