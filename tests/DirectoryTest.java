package filesystem;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;
import filesystem.exception.*;

/**
 * A JUnit (4) test class for testing the non-private methods of the Directory Class.
 *   
 * @author  Tommy Messelis
 * @version 6.0
 */
public class DirectoryTest {

	// FILE SYSTEM STRUCTURE FOR TESTING:

	/*
	 * Note that we suppress the warnings for unused variables. In fact, the variables themselves are not unused.
	 * We do give them values (references to objects) and build up a file system structure with them.
	 * The warning comes because we do not necessarily read out the values of these variables, and as such, they are 'unused'.
	 * 
	 * If you don't suppress the warnings, Eclipse will show yellow exclamation marks 
	 * on the lines where these variables are declared.
	 */
	
	// DIRECTORIES:
	// root level directories:
	private static Directory rootDirA, rootDirB, rootDirC, rootDirD_terminated;
	// first level directories:
	private static Directory dirA_X, dirA_Y, dirB_X, dirB_Y, dirB_Z;
	// second level directories:
	private static Directory dirA_X_1, dirB_X_1;
	// third level directories:
	private static Directory dirB_X_1_alfa;
	
	// FILES:
	// first level files:
	private static File fileA_X, fileA_Y, fileA_Z_terminated, fileB_X, fileB_Y;
	// second level files:
	@SuppressWarnings("unused")
	private static File fileA_X_1, fileA_X_2, fileA_Y_1, fileA_Y_2, fileB_X_1, fileB_X_2, fileB_Y_1, fileB_Y_2_default;
	// third level files:
	@SuppressWarnings("unused")
	private static File fileB_X_1_alfa, fileB_X_1_beta;
		
	// LINKS:
	// first level files:
	private static Link linkA_X, linkB_X, linkB_Y, linkB_Z_terminated;
	// second level files:
	@SuppressWarnings("unused")
	private static Link linkA_Y_1, linkB_Y_1;
	// third level files:
	@SuppressWarnings("unused")
	private static Link linkB_X_1_alfa;
	
	// VARIABLES TO KEEP TRACK OF TIME
	private static Date timeBefore, timeAfter;
	// TESTING LINK
	private static Directory item;					// <- the type of item is now DIRECTORY!
	
	// AUXILIARY METHOD
	private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	
	@Before
	public void setUpFileSystem() {
		// initialize the filesystem structure
		rootDirA = new Directory("dirA");
		rootDirB = new Directory("dirB");
		rootDirC = new Directory("dirC",false);
		rootDirD_terminated = new Directory("dirD");
		dirA_X = new Directory(rootDirA,"dirA_X");
		dirA_Y = new Directory(rootDirA,"dirA_Y");
		dirB_X = new Directory(rootDirB,"dirB_X");
		dirB_Y = new Directory(rootDirB,"dirB_Y");
		dirB_Z = new Directory(rootDirB,"dirB_Z");
		dirA_X_1 = new Directory(dirA_X,"dirA_X_1");
		dirB_X_1 = new Directory(dirB_X,"dirB_X_1");
		dirB_X_1_alfa = new Directory(dirB_X_1,"dirB_X_1_alfa");
		
		fileA_X = new File(rootDirA,"fileA_X",Type.TEXT,100,true);
		fileA_Y = new File(rootDirA,"fileA_Y",Type.JAVA,0,false);
		fileA_Z_terminated = new File(rootDirA,"fileA_Z",Type.PDF,150,true);
		fileB_X = new File(rootDirB,"fileB_X",Type.PDF,File.getMaximumSize(),true);
		fileB_Y = new File(rootDirB,"fileB_Y",Type.TEXT,File.getMaximumSize(),false);
		fileA_X_1 = new File(dirA_X,"fileA_X_1",Type.JAVA,1000,true);
		fileA_X_2 = new File(dirA_X,"fileA_X_2",Type.JAVA,1000,true);
		fileA_Y_1 = new File(dirA_Y,"fileA_Y_1",Type.JAVA,10,true);
		fileA_Y_2 = new File(dirA_Y,"fileA_Y_2",Type.TEXT,10,true);
		fileB_X_1 = new File(dirB_X,"fileB_X_1",Type.PDF,200,false);
		fileB_X_2 = new File(dirB_X,"fileB_X_2",Type.JAVA,200,false);
		fileB_Y_1 = new File(dirB_Y,"fileB_Y_1",Type.TEXT,200,false);
		fileB_Y_2_default = new File(dirB_Y,"?",Type.TEXT,0,true);
		fileB_X_1_alfa = new File(dirB_X_1,"fileB_X_1_alfa",Type.PDF,200,true);
		fileB_X_1_beta = new File(dirB_X_1,"fileB_X_1_beta",Type.PDF,200,true);
		
		linkA_X = new Link(rootDirA,"linkA_X",rootDirD_terminated);
		linkB_X = new Link(rootDirB,"linkB_X",rootDirA);
		linkB_Y = new Link(rootDirB,"linkB_Y",fileA_X_1);
		linkB_Z_terminated = new Link(rootDirB,"linkB_Z",fileA_X_1);
		linkA_Y_1 = new Link(dirA_Y,"linkA_Y_1",fileA_Z_terminated);
		linkB_Y_1 = new Link(dirB_Y,"linkB_Y_1",dirB_X);
		linkB_X_1_alfa = new Link(dirB_X_1,"linkB_X_1_alfa",dirB_X_1_alfa);
		
		dirB_X.setWritable(false);
		dirB_X_1.setWritable(false);
		rootDirD_terminated.terminate();
		fileA_Z_terminated.terminate();
		linkB_Z_terminated.terminate();
		rootDirB.setWritable(false);
		
	}

	
	/**
	 * CONSTRUCTORS
	 * 
	 * In this subclass, we rely on the tests of the superclass.
	 * We assume that the effects of calling the superconstructor will be satisfied and
	 * test only the new/added behavior of this class.
	 * 
	 * We can only do so because we read the implementation of the constructor and
	 * we know that it actually calls the superconstructor (which is tested and correct).
	 * This can not be deduced from the specification alone.
	 * If we want to be strict, we would have to test the complete specification of the 
	 * superconstructor effect too (like we do in the Link test).
	 */
	
	@Test
	public void testConstructorDirectoryStringBoolean_Legal() {	
		
		timeBefore = new Date();
		sleep();
		item = new Directory(rootDirA,"dirA_X_between",false);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name,writable)
		// 1.1. effect of setName()
		assertEquals(item.getName(), "dirA_X_between");
		// 1.2. effect of setParentDirectory()
		// 1.2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), rootDirA);
		// 1.2.2 effect of addAsItem
		// 1.2.2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 1.2.2.1.1 postcondition on nbItems
		assertEquals(rootDirA.getNbItems(),6);
		// 1.2.2.1.2 postcondition on getItemAt()
		assertSame(rootDirA.getItemAt(2),item);
		// 1.2.2.1.3 postcondition on index of other items
		assertSame(rootDirA.getItemAt(3),dirA_Y);
		assertSame(rootDirA.getItemAt(4),fileA_X);
		assertSame(rootDirA.getItemAt(5),fileA_Y);
		// 1.2.2.2 effect of parent.setModificationTime()
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 1.3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 1.4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 1.5. postcondition on the termination status
		assertFalse(item.isTerminated());	
		// 1.6. effect setWritable
		assertFalse(item.isWritable());
		
		// 2. postcondition on nbitems
		assertEquals(item.getNbItems(),0);
		
	}
	@Test
	public void testConstructorDirectoryString_Legal() {	
		
		timeBefore = new Date();
		sleep();
		item = new Directory(rootDirA,"dirA_X_between");
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name,writable)
		// 1.1. effect of setName()
		assertEquals(item.getName(), "dirA_X_between");
		// 1.2. effect of setParentDirectory()
		// 1.2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), rootDirA);
		// 1.2.2 effect of addAsItem
		// 1.2.2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 1.2.2.1.1 postcondition on nbItems
		assertEquals(rootDirA.getNbItems(),6);
		// 1.2.2.1.2 postcondition on getItemAt()
		assertSame(rootDirA.getItemAt(2),item);
		// 1.2.2.1.3 postcondition on index of other items
		assertSame(rootDirA.getItemAt(3),dirA_Y);
		assertSame(rootDirA.getItemAt(4),fileA_X);
		assertSame(rootDirA.getItemAt(5),fileA_Y);
		// 1.2.2.2 effect of parent.setModificationTime()
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 1.3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 1.4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 1.5. postcondition on the termination status
		assertFalse(item.isTerminated());	
		// 1.6. effect setWritable
		assertTrue(item.isWritable());
		
		// 2. postcondition on nbitems
		assertEquals(item.getNbItems(),0);
		
	}
	@Test
	public void testConstructorStringBoolean_Legal() {	
		
		timeBefore = new Date();
		sleep();
		item = new Directory("newRootDir",false);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name,writable)
		// 1.1. effect of setName()
		assertEquals(item.getName(), "newRootDir");
		// 1.2. effect of setParentDirectory()
		// 1.2.1 postcondition on the reference of item
		assertNull(item.getParentDirectory());
		// 1.3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 1.4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 1.5. postcondition on the termination status
		assertFalse(item.isTerminated());	
		// 1.6. effect setWritable
		assertFalse(item.isWritable());
		
		// 2. postcondition on nbitems
		assertEquals(item.getNbItems(),0);
		
	}
	@Test
	public void testConstructorString_Legal() {	
		
		timeBefore = new Date();
		sleep();
		item = new Directory("newRootDir");
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name,writable)
		// 1.1. effect of setName()
		assertEquals(item.getName(), "newRootDir");
		// 1.2. effect of setParentDirectory()
		// 1.2.1 postcondition on the reference of item
		assertNull(item.getParentDirectory());
		// 1.3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 1.4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 1.5. postcondition on the termination status
		assertFalse(item.isTerminated());	
		// 1.6. effect setWritable
		assertTrue(item.isWritable());
		
		// 2. postcondition on nbitems
		assertEquals(item.getNbItems(),0);
		
	}
	/*
	 * As we stated above, we will not test (all) the behavior of the superconstructor.
	 * We did test some things together with the new behavior in the tests above,
	 * but we will not revisit the other illegal cases.
	 */
	
	
	/**
	 * DESTRUCTORS
	 */
	
	@Test
	public void testCanBeTerminated_allCases() {
		// We now have a fully closed specification and can test all cases!
		
		// 1. already terminated dir
		assertFalse(rootDirD_terminated.canBeTerminated());
		// 2. dir not writable
		assertFalse(dirB_X.canBeTerminated());
		// 3. dir not empty
		assertFalse(dirA_X.canBeTerminated());
		// 4. not root and parent not writable
		assertFalse(dirB_Z.canBeTerminated());
		// 5. all other cases are terminatable
		assertTrue(dirA_X_1.canBeTerminated());
		rootDirC.setWritable(true);
		assertTrue(rootDirC.canBeTerminated());
	}
	@Test
	public void testCanBeRecursivelyDeleted_allCases() {
		// We now have a fully closed specification and can test all cases!

		// 1. already terminated dir
		assertFalse(rootDirD_terminated.canBeRecursivelyDeleted());
		// 2. dir not writable
		assertFalse(dirB_X.canBeRecursivelyDeleted());
		// 3. dir not empty, and not all content is recursively deletable
		fileA_X_1.setWritable(false);
		assertFalse(dirA_X.canBeRecursivelyDeleted());
		// 4. not root and parent not writable
		assertFalse(dirB_Z.canBeRecursivelyDeleted());
		// 5. all other cases are terminatable
		assertTrue(dirA_X_1.canBeRecursivelyDeleted());
		fileA_X_1.setWritable(true);
		assertTrue(dirA_X.canBeRecursivelyDeleted());
		rootDirC.setWritable(true);
		assertTrue(rootDirC.canBeRecursivelyDeleted());
	}
	
	@Test
	public void testDeleteREC_LegalCase_emptyDir() {
		assertTrue(dirA_X_1.canBeRecursivelyDeleted());
		assertEquals(dirA_X.getNbItems(),3);
		
		timeBefore = new Date();
		sleep();
		dirA_X_1.deleteRecursive();
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. isTerminated
		assertTrue(dirA_X_1.isTerminated());
		// 2. isRoot
		assertTrue(dirA_X_1.isRoot());
		// 3. effect of parent.remove
		// 3.1 effect of removeItemAt(index)
		// 3.1.1 nbItems 
		assertEquals(dirA_X.getNbItems(),2);
		// 3.1.2 !hasAsItem
		assertFalse(dirA_X.hasAsItem(dirA_X_1));
		// 3.1.3 items shifted left
		assertSame(dirA_X.getItemAt(1),fileA_X_1);
		assertSame(dirA_X.getItemAt(2),fileA_X_2);
		// 3.1.4 exceptions impossible
		// 3.2 effect of setModification time
		assertNotNull(dirA_X.getModificationTime());
		assertTrue(dirA_X.getModificationTime().after(timeBefore));
		assertTrue(dirA_X.getModificationTime().before(timeAfter));
		// 3.3 exceptions impossible
		// 4. is empty
		assertEquals(dirA_X_1.getNbItems(),0);
		// 5. there are no other items which were a child of this (empty) directory		
	}
	@Test
	public void testDeleteREC_LegalCase_NonEmptyDir() {
		assertTrue(dirA_X.canBeRecursivelyDeleted());
		assertEquals(rootDirA.getNbItems(),5);
		
		timeBefore = new Date();
		sleep();
		dirA_X.deleteRecursive();
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. isTerminated
		assertTrue(dirA_X.isTerminated());
		// 2. isRoot
		assertTrue(dirA_X.isRoot());
		// 3. effect of parent.remove
		// 3.1 effect of removeItemAt(index)
		// 3.1.1 nbItems 
		assertEquals(rootDirA.getNbItems(),4);
		// 3.1.2 !hasAsItem
		assertFalse(rootDirA.hasAsItem(dirA_X));
		// 3.1.3 items shifted left
		assertSame(rootDirA.getItemAt(1),dirA_Y);
		assertSame(rootDirA.getItemAt(2),fileA_X);
		assertSame(rootDirA.getItemAt(3),fileA_Y);
		assertSame(rootDirA.getItemAt(4),linkA_X);
		// 3.1.4 exceptions impossible
		// 3.2 effect of setModification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3.3 exceptions impossible
		// 4. is empty
		assertEquals(dirA_X.getNbItems(),0);
		// 5. all items which were children of dirA_X are now root, terminated and emtpy
		// 5.1 root
		assertTrue(dirA_X_1.isRoot());
		assertTrue(fileA_X_1.isRoot());
		assertTrue(fileA_X_2.isRoot());
		// 5.2 terminated
		assertTrue(dirA_X_1.isTerminated());
		assertTrue(fileA_X_1.isTerminated());
		assertTrue(fileA_X_2.isTerminated());
		// 5.3 empty
		assertEquals(dirA_X_1.getNbItems(),0);
	}
	@Test
	public void testDeleteREC_LegalCase_NonEmptyRootMultilevelDir() {
		fileA_Y.setWritable(true);
		assertTrue(rootDirA.canBeRecursivelyDeleted());
		
		timeBefore = new Date();
		sleep();
		rootDirA.deleteRecursive();
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. isTerminated
		assertTrue(rootDirA.isTerminated());
		// 2. isRoot
		assertTrue(rootDirA.isRoot());
		// 3. effect of parent.remove <- not here
		// 4. is empty
		assertEquals(rootDirA.getNbItems(),0);
		// 5. all items which were children of dirA_X are now root, terminated and emtpy
		// 5.1 root
		assertTrue(dirA_X.isRoot());
		assertTrue(dirA_X_1.isRoot());
		assertTrue(fileA_X_1.isRoot());
		assertTrue(fileA_X_2.isRoot());
		assertTrue(dirA_Y.isRoot());
		assertTrue(fileA_Y_1.isRoot());
		assertTrue(fileA_Y_2.isRoot());
		assertTrue(linkA_Y_1.isRoot());
		assertTrue(fileA_X.isRoot());
		assertTrue(fileA_Y.isRoot());
		assertTrue(linkA_X.isRoot());
		// 5.2 terminated
		assertTrue(dirA_X.isTerminated());
		assertTrue(dirA_X_1.isTerminated());
		assertTrue(fileA_X_1.isTerminated());
		assertTrue(fileA_X_2.isTerminated());
		assertTrue(dirA_Y.isTerminated());
		assertTrue(fileA_Y_1.isTerminated());
		assertTrue(fileA_Y_2.isTerminated());
		assertTrue(linkA_Y_1.isTerminated());
		assertTrue(fileA_X.isTerminated());
		assertTrue(fileA_Y.isTerminated());
		assertTrue(linkA_X.isTerminated());
		// 5.3 empty
		assertEquals(dirA_X.getNbItems(),0);
		assertEquals(dirA_X_1.getNbItems(),0);
		assertEquals(dirA_Y.getNbItems(),0);	
	}
	
	
	/**
	 * NAME METHODS
	 */
	
	@Test
	public void testCanHaveAsName_allCases() {
		// We now have a fully closed specification and can test all cases!

		// 1. null
		assertFalse(dirA_X.canHaveAsName(null));
		// 2. forbidden characters
		assertFalse(dirA_X.canHaveAsName("someting&with#specials?"));
		// 3. valid names
		assertTrue(dirA_X.canHaveAsName("aValidNamewithoutdots_and_with_underscores0and9numbers4"));
	}
	@Test
	public void testgetAbsolutePath_allCases() {
		assertNotNull(rootDirA.getAbsolutePath());
		assertEquals(rootDirA.getAbsolutePath(),"/dirA");
		assertNotNull(dirA_X.getAbsolutePath());
		assertEquals(dirA_X.getAbsolutePath(),"/dirA/dirA_X");
		assertNotNull(dirB_X_1_alfa.getAbsolutePath());
		assertEquals(dirB_X_1_alfa.getAbsolutePath(),"/dirB/dirB_X/dirB_X_1/dirB_X_1_alfa");
		assertNotNull(rootDirD_terminated.getAbsolutePath());
		assertEquals(rootDirD_terminated.getAbsolutePath(),"/dirD");	
	}
	
	/**
	 * CONTENTS METHODS
	 */
	
	@Test
	public void testHasProperItems_allCases() {
		// 1. empty directories (always proper!)
		assertTrue(rootDirC.hasProperItems());
		assertTrue(dirA_X_1.hasProperItems());
		// 2. non-empty steadystate directories
		assertTrue(dirA_X.hasProperItems());
		for (int i=1; i<= dirA_X.getNbItems(); i++) {
			assertTrue(dirA_X.canHaveAsItemAt(dirA_X.getItemAt(i), i));
			assertSame(dirA_X.getItemAt(i).getParentDirectory(), dirA_X);
		}
		// 3. There is no way of constructing a directory with improper items.
		//	The addAsItem method is highly restricted and can only be called from
		//	the setParentDirectory method, given that it already set up/broke down
		//	one way of the bidirectional relationship!
		// 	This is actually a good thing. Only the setParentDirectory is able to 
		//  manipulate the bidirectional relationship!
	}
	@Test
	public void testCanHaveAsItemAt_AllCases() {
		// 1. items that are not allowed at any index
		assertFalse(rootDirA.canHaveAsItemAt(null,1));
		assertFalse(rootDirA.canHaveAsItemAt(rootDirD_terminated,1));
		// 2. indices that are not allowed
		assertFalse(rootDirA.canHaveAsItemAt(dirA_X,-1));
		assertFalse(rootDirA.canHaveAsItemAt(dirA_X,0));
		assertFalse(rootDirA.canHaveAsItemAt(rootDirB,10));
		// 3. items that exists in a directory -> right and wrong indices
		assertTrue(rootDirA.canHaveAsItemAt(dirA_Y,2));
		assertFalse(rootDirA.canHaveAsItemAt(dirA_Y,1));
		assertFalse(rootDirA.canHaveAsItemAt(dirA_Y,3));
		assertFalse(rootDirA.canHaveAsItemAt(dirA_Y,4));
		assertFalse(rootDirA.canHaveAsItemAt(dirA_Y,5));
		// 4. items that do not exist in a directory -> right and wrong indices
		assertTrue(rootDirA.canHaveAsItemAt(rootDirB,3));
		assertFalse(rootDirA.canHaveAsItemAt(rootDirB,1));
		assertFalse(rootDirA.canHaveAsItemAt(rootDirB,2));
		assertFalse(rootDirA.canHaveAsItemAt(rootDirB,4));
		assertFalse(rootDirA.canHaveAsItemAt(rootDirB,5));	
	}
	@Test
	public void testCanHaveAsItem_AllCases() {
		// 1. null items
		assertFalse(rootDirA.canHaveAsItem(null));
		// 2. terminated items
		assertFalse(rootDirA.canHaveAsItem(rootDirD_terminated));
		// 3. terminated directory can't have any items
		assertFalse(rootDirD_terminated.canHaveAsItem(null));
		assertFalse(rootDirD_terminated.canHaveAsItem(rootDirA));
		assertFalse(rootDirD_terminated.canHaveAsItem(dirA_X_1));
		// 4. items that are present in the directory: unique name
		assertTrue(rootDirA.canHaveAsItem(dirA_X));
		// 4.1 we cannot test a false answer because we cannot add an item with the same name.
		// 5. items not in the directory: name should not yet exist
		assertTrue(rootDirA.canHaveAsItem(fileB_Y_2_default));
		fileB_Y_2_default.changeName("dirA_X");
		assertFalse(rootDirA.canHaveAsItem(fileB_Y_2_default));
	}
	
	@Test
	public void testHasAsItem_AllCases() {
		assertTrue(rootDirA.hasAsItem(dirA_X));
		assertTrue(rootDirA.hasAsItem(dirA_Y));
		assertTrue(rootDirA.hasAsItem(fileA_X));
		assertTrue(rootDirA.hasAsItem(fileA_Y));
		assertTrue(rootDirA.hasAsItem(linkA_X));
		assertFalse(rootDirA.hasAsItem(dirA_X_1));
		assertFalse(rootDirA.hasAsItem(fileA_X_1));
		assertFalse(rootDirA.hasAsItem(fileA_X_2));
		assertFalse(rootDirA.hasAsItem(dirB_X));
		assertFalse(rootDirA.hasAsItem(rootDirC));
		assertFalse(rootDirA.hasAsItem(rootDirD_terminated));
	}
	
	@Test
	public void testGetInsertionIndexOf_legalCases() {
		// 1. first, middle, last
		fileA_Y_1.changeName("aaa");
		assertEquals(rootDirA.getInsertionIndexOf(fileA_Y_1),1);
		fileA_Y_1.changeName("eee");
		assertEquals(rootDirA.getInsertionIndexOf(fileA_Y_1),3);
		fileA_Y_1.changeName("zzz");
		assertEquals(rootDirA.getInsertionIndexOf(fileA_Y_1),6);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testGetInsertionIndexOf_illegalCaseInvalidItemNull() {
		rootDirA.getInsertionIndexOf(null);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testGetInsertionIndexOf_illegalCaseInvalidItemTerminated() {
		rootDirA.getInsertionIndexOf(rootDirD_terminated);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testGetInsertionIndexOf_illegalCaseInvalidItemAlreadyPresent() {
		rootDirA.getInsertionIndexOf(dirA_Y);
	}
	
	@Test
	public void testAddAsItem_Legal() {
		/*
		 * There is no way of testing the legal cases of this method,
		 * because it is an auxiliary method to manipulate the bidirectional
		 * relationship. It is highly restricted and can only be called
		 * from the setParentDirectory method, since that is the only place
		 * were legal cases can be constructed. (It requires one way of the
		 * bidirectional relationship to be already set up.)
		 */
	}
	@Test (expected = IllegalArgumentException.class)
	public void testAddAsItem_IllegalCase_AlreadyPresent() {
		rootDirA.addAsItem(dirA_Y);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testAddAsItem_IllegalCase_NotAllowed() {
		assertFalse(rootDirA.canHaveAsItem(rootDirD_terminated));
		rootDirA.addAsItem(rootDirD_terminated);
	}
	@Test
	public void testRemoveAsItem_Legal() {
		/*
		 * There is no way of testing the legal cases of this method,
		 * because it is an auxiliary method to manipulate the bidirectional
		 * relationship. It is highly restricted and can only be called
		 * from the setParentDirectory method, since that is the only place
		 * were legal cases can be constructed. (It requires one way of the
		 * bidirectional relationship to be already broken down.)
		 */
	}
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveAsItem_IllegalCase_NotPresent() {
		rootDirA.removeAsItem(dirA_X_1);
	}
	
	@Test
	public void testContainsDiskItemWithName_allCases() {
		assertTrue(rootDirA.containsDiskItemWithName("dirA_X"));
		assertTrue(rootDirA.containsDiskItemWithName("dirA_Y"));
		assertTrue(rootDirA.containsDiskItemWithName("fileA_X"));
		assertTrue(rootDirA.containsDiskItemWithName("fileA_Y"));
		assertTrue(rootDirA.containsDiskItemWithName("linkA_X"));
		assertFalse(rootDirA.containsDiskItemWithName("dirA_X_1"));
		assertFalse(rootDirA.containsDiskItemWithName("dirB_X"));
		assertFalse(rootDirA.containsDiskItemWithName("dirC"));
		assertFalse(rootDirA.containsDiskItemWithName("dirD"));	
	}
	
	
	@Test
	public void testGetItem_allCases() {
		DiskItem i;
		// 1. exact name
		i = rootDirA.getItem(dirA_X.getName());
		assertEquals(i.getName(),dirA_X.getName());
		assertTrue(rootDirA.hasAsItem(i));
		// 2. name ignore case
		i = rootDirA.getItem("dira_y");
		assertTrue(i.getName().equalsIgnoreCase("dira_y"));
		assertTrue(rootDirA.hasAsItem(i));
		// 3. name not present
		assertNull(rootDirA.getItem("noname"));		
	}
	
	
	@Test
	public void testGetIndexOf_legalCases() {
		
		assertEquals(rootDirB.getIndexOf(dirB_X),1);
		assertEquals(rootDirB.getIndexOf(dirB_Y),2);
		assertEquals(rootDirB.getIndexOf(dirB_Z),3);
		assertEquals(rootDirB.getIndexOf(fileB_X),4);
		assertEquals(rootDirB.getIndexOf(fileB_Y),5);
		assertEquals(rootDirB.getIndexOf(linkB_X),6);
		assertEquals(rootDirB.getIndexOf(linkB_Y),7);	
	}
	@Test (expected = IllegalArgumentException.class)
	public void testGetIndexOf_illegalCase_Null() {
		rootDirB.getIndexOf(null);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testGetIndexOf_illegalCase_NotPresent() {
		rootDirB.getIndexOf(dirA_X);
	}
	
	/*
	 * Tests for restore order after namechange are difficult.
	 * This is an auxiliary method called by the changeName mutator.
	 * Here, we cannot perform a namechange without calling that mutator.
	 * The legal cases are thus tested through the changeName tests.
	 */
	@Test (expected = IndexOutOfBoundsException.class)
	public void testRestoreOrderAfterNameChange_LowIndex() {
		rootDirA.restoreOrderAfterNameChangeAt(-1);
	}
	@Test (expected = IndexOutOfBoundsException.class)
	public void testRestoreOrderAfterNameChange_HighIndex() {
		rootDirA.restoreOrderAfterNameChangeAt(25);
	}
	@Test (expected = IndexOutOfBoundsException.class)
	public void testRestoreOrderAfterNameChange_ZeroIndex() {
		rootDirA.restoreOrderAfterNameChangeAt(0);
	}
	
	
	/**
	 * PARENT DIRECTORY METHODS
	 */
	
	@Test
	public void testCanHaveAsParentDirectory_allCases() {
		// We now have a fully closed specification and can test all cases!
		
		// 1. terminated item can only have null as parent
		assertTrue(rootDirD_terminated.canHaveAsParentDirectory(null));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirA));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirB));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirC));
		// 2. terminated parent directory is never ok
		assertFalse(rootDirB.canHaveAsParentDirectory(rootDirD_terminated));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirD_terminated));
		// 3. null is allowed for all directories
		assertTrue(dirA_X.canHaveAsParentDirectory(null));
		assertTrue(rootDirD_terminated.canHaveAsParentDirectory(null));
		// 4. the prime object is never allowed
		assertFalse(rootDirA.canHaveAsParentDirectory(rootDirA));
		assertFalse(dirA_X_1.canHaveAsParentDirectory(dirA_X_1));
		// 5. a direct or indirect child is never allowed
		assertFalse(rootDirB.canHaveAsParentDirectory(dirB_X));
		assertFalse(rootDirB.canHaveAsParentDirectory(dirB_X_1));
		assertFalse(rootDirB.canHaveAsParentDirectory(dirB_X_1_alfa));
		// 6. all other options are allowed
		assertTrue(dirB_X.canHaveAsParentDirectory(dirA_X));
		assertTrue(dirB_X.canHaveAsParentDirectory(rootDirA));
		assertTrue(dirB_X.canHaveAsParentDirectory(rootDirB));
		assertTrue(dirB_X.canHaveAsParentDirectory(dirB_Y));	
	}
	
	@Test 
	public void testMakeRoot_legalCaseAlreadyRoot() {

		Date modTimeBefore = rootDirA.getModificationTime();
		timeBefore = new Date();
		sleep();
		rootDirA.makeRoot();
		sleep();
		timeAfter = new Date();
		
		// 1. effect of setParent(null)
		// 1.1 post on parentdirectory
		assertNull(rootDirA.getParentDirectory());
		// 1.2 addAsItem impossible
		// 1.3 removeAsItem not applicable here
		// 2. effect of setModTime (should not have changed!)
		assertEquals(modTimeBefore,rootDirA.getModificationTime());
	}
	@Test 
	public void testMakeRoot_legalCaseNonRoot() {

		timeBefore = new Date();
		sleep();
		dirA_X.makeRoot();
		sleep();
		timeAfter = new Date();
		
		// 1. effect of setParent(null)
		// 1.1 post on parentdirectory
		assertNull(dirA_X.getParentDirectory());
		// 1.2 addAsItem impossible
		// 1.3. effect of parent.remove
		// 1.3.1 effect of removeItemAt(index)
		// 1.3.1.1 nbItems 
		assertEquals(rootDirA.getNbItems(),4);
		// 1.3.1.2 !hasAsItem
		assertFalse(rootDirA.hasAsItem(dirA_X));
		// 1.3.1.3 items shifted left
		assertSame(rootDirA.getItemAt(1),dirA_Y);
		assertSame(rootDirA.getItemAt(2),fileA_X);
		assertSame(rootDirA.getItemAt(3),fileA_Y);
		assertSame(rootDirA.getItemAt(4),linkA_X);
		// 1.3.1.4 exceptions impossible
		// 1.3.2 effect of setModification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 1.3.3 exceptions impossible
		// 2. effect of setModTime (should not have changed!)
		assertNotNull(dirA_X.getModificationTime());
		assertTrue(dirA_X.getModificationTime().after(timeBefore));
		assertTrue(dirA_X.getModificationTime().before(timeAfter));		
	}
	@Test (expected = IllegalStateException.class) 
	public void testMakeRoot_illegalCaseTerminated() {
		rootDirD_terminated.makeRoot();
	}
	@Test (expected = DiskItemNotWritableException.class) 
	public void testMakeRoot_illegalCaseNotWritable() {
		dirB_X.setWritable(true);
		dirB_X_1.makeRoot();
	}
	@Test (expected = DiskItemNotWritableException.class) 
	public void testMakeRoot_illegalCaseParentNotWritable() {
		dirB_Y.makeRoot();
	}
	/*
	 * The other exceptions that come in through the effect of setParent
	 * are all impossible to occur.
	 */
	
	
	/**
	 * DISK USAGE METHODS
	 */
	
	
	@Test
	public void testGetTotalDiskUsage_allCases() {
		// 1. empty dir
		assertEquals(rootDirC.getTotalDiskUsage(),0L);
		assertEquals(dirA_X_1.getTotalDiskUsage(),0L);
		assertEquals(rootDirD_terminated.getTotalDiskUsage(),0L);
		// 2. non-emtpy single-level dir 
		long sum = 0L;
		for (int i=1; i<=dirA_X.getNbItems(); i++) {
			sum += dirA_X.getItemAt(i).getTotalDiskUsage();
		}
		assertTrue(dirA_X.getTotalDiskUsage() >= 0);
		assertEquals(sum,dirA_X.getTotalDiskUsage());
		// 3. non-emtpy multilevel dir 
		sum = 0L;
		for (int i=1; i<=rootDirB.getNbItems(); i++) {
			sum += rootDirB.getItemAt(i).getTotalDiskUsage();
		}
		assertTrue(rootDirB.getTotalDiskUsage() >= 0);
		assertEquals(sum,rootDirB.getTotalDiskUsage());
		
	}

}
