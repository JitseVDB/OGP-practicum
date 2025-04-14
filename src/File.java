package filesystem;

import filesystem.exception.*;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class of files.
 *
 * @invar	Each file must have a valid size.
 * 			| isValidSize(getSize())
 * @invar   Each file must have a valid type.
 *          | isValidType(getType())
 *          
 * @note	Subclasses may only add/strengthen invariants (Liskov principle).
 * 
 * 
 * @author 	Tommy Messelis
 * @version	6.0
 */
public class File extends ActualItem{

    /**********************************************************
     * Constructors
     **********************************************************/
	
   
    /**
     * Initialize a new file with given parent directory, name,
     * size and writability.
     *
     * @param  	parent
     *         	The parent directory of the new file.       
     * @param  	name
     *         	The name of the new file.
     * @param  	type
     *         	The type of the new file. 
     * @param  	size
     *         	The size of the new file.
     * @param  	writable
     *         	The writability of the new file.
     * 
     * @pre		type is a valid type.
     * 			| isValidType(type)
     * @effect 	The new file is a disk item with the given
     *         	parent, name and writability.
     *         	| super(parent,name,writable)
     * @effect 	The new file has the given size
     *         	| setSize(size)
     * @post   	The type of this new file is set to the given type.
     *         	| new.getType() == type        
     */
	@Raw
    public File(Directory parent, String name, Type type, int size, boolean writable)
    		throws IllegalArgumentException, DiskItemNotWritableException {
    	super(parent,name,writable);
    	setSize(size);
    	this.type=type;
    	
    	// Note how we can simply call the superconstructor here.
    	// Invalid types or sizes are impossible through the preconditions.
    	// If we would have implemented types and sizes defensively, 
    	// then this constructor would have to call the restricted superconstructor
    	// (like it is the case with Links).
    	// (We would have to define such a restricted constructor at the level of ActualDiskItem.)
    }

    /**
     * Initialize a new writable, empty file with given parent directory
     * and name.
     *
     * @param  parent
     *         The parent directory of the new file.
     * @param  name
     *         The name of the new file.
     * @param  type
     *         The type of the new file.        
     * 
     * @effect This new file is initialized with the given name
     *         and the given parent directory, type, 
     *         the new file is empty and writable.
     *         | this(parent,name,type,0,true)
     */
	@Raw
    public File(Directory parent, String name, Type type)
    		throws IllegalArgumentException, DiskItemNotWritableException {
    	this(parent,name,type,0,true);
    }    
    
	
	/**********************************************************
	 * Destructors: delete/termination
	 **********************************************************/

	/**
	 * Check whether this file can be terminated.
	 * 
	 * @return	True if the file is not yet terminated, is writable and it is either a root or
	 * 			its parent directory is writable.
	 * 			| result == !isTerminated() && isWritable() && (isRoot() || getParentDirectory().isWritable())
	 * 
	 * @note	This specification can now be closed.
	 */
    @Override
    public boolean canBeTerminated(){
    	// no additional implementation required
    	// but only because we know the implementation in the superclass is sufficient (because we implemented it)
    	// (anyway, the tests will show if this implementation would not be sufficient!)
    	return super.canBeTerminated();
	}
    
    
    /**
     * Check whether this file can be recursively deleted. For files this is equivalent to being destructible.
     *
     * @return	True if this file can be terminated.
	 * 			| result == canBeTerminated()
	 * 
     * @note	This is a fully conclusive specification of the result.
     */
	@Override
    public boolean canBeRecursivelyDeleted(){
    	return canBeTerminated();
    }
	
	// terminate and deleteRecursive do not need to be redefined. Their definitions in the superclass suffice!
	
    
	/**********************************************************
	 * Name
	 **********************************************************/
	
	/**
	 * Check whether the given name is a legal name for a file.
	 * 
	 * @param  	name
	 *			The name to be checked
	 * @return	True if the given string is effective, not
     * 			empty and consisting only of letters, digits, dots,
     * 			hyphens and underscores; false otherwise.
     * 			| result ==
     * 			|	(name != null) && name.matches("[a-zA-Z_0-9.-]+")
	 * 			
	 */
	@Override
	@Raw
	public boolean canHaveAsName(String name) {
		// the implementation of the superclass suffices!
		return super.canHaveAsName(name);
	}
	
    
	/**
	 * Returns the absolute path of this file.
	 * 
	 * @return	If this is a root item, the name of this item is returned preceded with a '/'
	 * 			otherwise the name of this item, preceded with a '/' and the absolute path 
	 * 			of the parent directory is returned.
	 * 			| if (isRoot())
	 * 			| then result.equals("/" + toString())
	 * 			| else result.equals(getParentDirectory().getAbsolutePath() + "/" + toString())
	 */
	@Override
	public String getAbsolutePath(){
		if(isRoot()) return "/"+toString();
		else return getParentDirectory().getAbsolutePath() + "/" + toString();	
	}
	
	
	/**********************************************************
	 * string representation
	 **********************************************************/
    
    /**
     * Return a textual representation of this file.
     * 
     * @return  The name of this file followed by a dot
     *          followed by the extension representing the
     *          type of this file.
     *          | result.equals(getName()+"."+getType().getExtension())
     */    
	@Override
    public String toString(){
    	  return getName()+"."+getType().getExtension();
    }
    
    
    /**********************************************************
     * type
     **********************************************************/
    
    /**
	 * Variable referencing the type of this file. (final, so no default)					
	 */
    private final Type type;
    
    /**
     * Return whether the given type is a valid type for a file.
     *
     * @param  type
     *         The type to check.
     * @return True if and only if the given type is effective.
     *         | result == (type != null)
     */
    public static boolean isValidType(Type type){
    	  return type != null;
    }
    
    /**
     * Return the type of this file.
     */ 
    @Raw @Basic @Immutable
    public Type getType(){
    	  return type;
    }

    
    /**********************************************************
     * size - nominal programming
     **********************************************************/
    
    /**
     * Variable registering the size of this file (in bytes). (Default = 0)
     */
    private int size = 0;
    
    /**
     * Variable registering the maximum size of any file (in bytes).
     */
    private static final int maximumSize = Integer.MAX_VALUE;

    /**
     * Return the size of this file (in bytes).
     */
    @Raw @Basic 
    public int getSize() {
        return size;
    }
    
    /**
     * Set the size of this file to the given size.
     *
     * @param  size
     *         The new size for this file.
     * @pre    The given size must be legal.
     *         | isValidSize(size)
     * @post   The given size is registered as the size of this file.
     *         | new.getSize() == size
     */
    @Raw @Model 
    private void setSize(int size) {
        this.size = size;
    }
   
    /**
     * Return the maximum file size.
     */
    @Basic @Immutable
    public static int getMaximumSize() {
        return maximumSize;
    }

    /**
     * Check whether the given size is a valid size for a file.
     *
     * @param  size
     *         The size to check.
     * @return True if and only if the given size is positive and does not
     *         exceed the maximum size.
     *         | result == ((size >= 0) && (size <= getMaximumSize()))
     */
    public static boolean isValidSize(int size) {
        return ((size >= 0) && (size <= getMaximumSize()));
    }

    /**
	 * Change the size of this file with the given delta.
	 *
	 * @param  delta
	 *         The amount of bytes by which the size of this file
	 *         must be increased or decreased.
	 * @pre    The given delta must not be 0
	 *         | delta != 0
	 * @effect The size of this file is adapted with the given delta.
	 *         | setSize(getSize()+delta)
	 * @effect The modification time is updated.
	 *         | setModificationTime()
	 * @throws DiskItemNotWritableException(this)
	 *         This file is not writable.
	 *         | !isWritable()
	 */
	@Model 
	private void changeSize(int delta) throws DiskItemNotWritableException{
	    if (isWritable()) {
	        setSize(getSize()+delta);
	        setModificationTime();            
	    }else{
	    	throw new DiskItemNotWritableException(this);
	    }
	}

	/**
     * Increases the size of this file with the given delta.
     *
     * @param   delta
     *          The amount of bytes by which the size of this file
     *          must be increased.
     * @pre     The given delta must be strictly positive.
     *          | delta > 0
     * @effect  The size of this file is increased with the given delta.
     *          | changeSize(delta)
     */
    public void enlarge(int delta) throws DiskItemNotWritableException {
        changeSize(delta);
    }

    /**
     * Decreases the size of this file with the given delta.
     *
     * @param   delta
     *          The amount of bytes by which the size of this file
     *          must be decreased.
     * @pre     The given delta must be strictly positive.
     *          | delta > 0
     * @effect  The size of this file is decreased with the given delta.
     *          | changeSize(-delta)
     */
    public void shorten(int delta) throws DiskItemNotWritableException {
        changeSize(-delta);
    }

    
    /**********************************************************
	 * parent directory
	 **********************************************************/

    /** 
	 * Check whether this file can have the given directory as
	 * its parent directory.
	 * 
	 * @param  	directory
	 *          The directory to check.
	 * 
	 * @return  If this file is not terminated and the given directory is not effective, then false.
	 * 			| if (!isTerminated() && directory == null) then result == false
	 * @return 	If this file is not terminated, and the given directory is effective and not terminated,
	 * 			then true.
	 * 			| if (!this.isTerminated() && directory != null && !directory.isTerminated())
	 * 			|	then result == true
	 * 
	 * @note	This checker now closes the specification of the inherited checker by specifying the result in all
	 * 			other cases than described at the level of the superclass.
	 * 			In case the disk item is a file, any effective and non-terminated parent directory is allowed.
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
	 * Returns the total disk usage of this File
	 * 
	 * @return	the size of this file
	 * 			| result == (long) getSize()
	 */
    @Override
	public long getTotalDiskUsage(){
		return (long) getSize();
	}
    
    
	
    
}