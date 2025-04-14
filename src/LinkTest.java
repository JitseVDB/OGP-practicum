package filesystem;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;
import filesystem.exception.*;

/**
 * A JUnit (4) test class for testing the non-private methods of the Link Class.
 *   
 * @author  Tommy Messelis
 * @version 6.0
 */
public class LinkTest {
	
	
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
	@SuppressWarnings("unused")
	private static Directory dirA_X, dirA_Y, dirB_X, dirB_Y, dirB_Z;
	// second level directories:
	@SuppressWarnings("unused")
	private static Directory dirA_X_1, dirB_X_1;
	// third level directories:
	private static Directory dirB_X_1_alfa;
	
	// FILES:
	// first level files:
	@SuppressWarnings("unused")
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
	private static Link linkA_Y_1, linkB_Y_1;
	// third level files:
	private static Link linkB_X_1_alfa;
	
	// VARIABLES TO KEEP TRACK OF TIME
	private static Date timeBefore, timeAfter;
	// TESTING LINK
	private static Link item;					// <- the type of item is now LINK!
	
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
	 * We cannot simply assume that the superconstructor works flawlessly.
	 * In fact, we state an effect of super(parent, name) but the implementation 
	 * actually calls super(name). We must explicitly check all postconditions 
	 * coming in through the effect of the stated superconstructor!
	 * 
	 * In the other subclasses, we will rely on the tests of the superclass and will
	 * only check the added specification.													//TODO: is dat zo?
	 */
	
	@Test
	public void testConstructorDirectoryStringActualItem_Legal() {
		
		
		timeBefore = new Date();
		sleep();
		item = new Link(rootDirA,"dirA_X_between",dirB_X);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name)
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
		// 2. linkedItem is set
		assertSame(item.getLinkedItem(),dirB_X);
		// 3. the link is valid
		assertTrue(item.isValidLink());
	}
	@Test
	public void testConstructorDirectoryString_Legal_InvalidName() {
		
		// double check the current status
		assertEquals(rootDirA.getNbItems(),5);
		
		timeBefore = new Date();
		sleep();
		item = new Link(rootDirA,"invali??dname",dirB_X);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1.1. effect of setName()
		assertEquals(item.getName(), item.getDefaultName());
		// 1.2. effect of setParentDirectory()
		// 1.2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), rootDirA);
		// 1.2.2 effect of addAsItem
		// 1.2.2.1 effect of addItemAt (based on the default name, it should have been added at index 6
		// 1.2.2.1.1 postcondition on nbItems
		assertEquals(rootDirA.getNbItems(),6);
		// 1.2.2.1.2 postcondition on getItemAt()
		assertSame(rootDirA.getItemAt(6),item);
		// 1.2.2.1.3 postcondition on index of other items (none here!)
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
		// 2. linkedItem is set
		assertSame(item.getLinkedItem(),dirB_X);
		// 3. the link is valid
		assertTrue(item.isValidLink());
		
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_ParentDirectory_Terminated() {	
		item = new Link(rootDirD_terminated,"dirA_X_between",dirB_X);	
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_ValidNameNotUnique() {	
		item = new Link(rootDirA,"fileA_X",dirB_X);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_InvalidNameNotUnique() {	
		item = new Link(dirB_Y,"???",dirB_X);
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testConstructorDirectoryString_Illegal_ParentDirectory_NotWritable() {	
		item = new Link(rootDirC,"dirA_X_between",dirB_X);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_ParentDirectory_Null() {	
		item = new Link(null,"dirA_X_between",dirB_X);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_linkedItem_Null() {	
		item = new Link(null,"dirA_X_between",null);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_linkedItem_Terminated() {	
		item = new Link(null,"dirA_X_between",rootDirD_terminated);
	}
	

	
	/**
	 * DESTRUCTORS
	 */
	
	@Test
	public void testCanBeTerminated_allCases() {
		// We now have a fully closed specification and can test all cases!
		
		// 1. already terminated link
		assertFalse(linkB_Z_terminated.canBeTerminated());
		// 2. not root and parent not writable
		assertFalse(linkB_X.canBeTerminated());
		// 3. root link is not possible
		// 4. non root link in writable directory
		assertTrue(linkA_X.canBeTerminated());
	}
	@Test
	public void testCanBeRecursivelyDeleted_allCases() {
		// We now have a fully closed specification and can test all cases!

		// 1. already terminated link
		assertFalse(linkB_Z_terminated.canBeRecursivelyDeleted());
		// 2. not root and parent not writable
		assertFalse(linkB_X.canBeRecursivelyDeleted());
		// 3. root link is not possible
		// 4. non root link in writable directory
		assertTrue(linkA_X.canBeRecursivelyDeleted());
	}

	
	/**
	 * NAME METHODS
	 */
	
	@Test
	public void testCanHaveAsName_allCases() {
		// We now have a fully closed specification and can test all cases!

		// 1. null
		assertFalse(linkA_X.canHaveAsName(null));
		// 2. forbidden characters
		assertFalse(linkA_X.canHaveAsName("someting&with#specials?"));
		// 3. valid names
		assertTrue(linkA_X.canHaveAsName("aValidName.with.dots_and_underscores0and9numbers4"));
	}
	@Test
	public void testgetAbsolutePath_allCases() {
		assertNotNull(linkA_X.getAbsolutePath());
		assertEquals(linkA_X.getAbsolutePath(),"/dirA/linkA_X");
		assertNotNull(linkA_Y_1.getAbsolutePath());
		assertEquals(linkA_Y_1.getAbsolutePath(),"/dirA/dirA_Y/linkA_Y_1");
		assertNotNull(linkB_Z_terminated.getAbsolutePath());
		assertEquals(linkB_Z_terminated.getAbsolutePath(),"/linkB_Z");	
	}
	
	
	/**
	 * LINKED ITEM METHODS
	 */
	
	@Test
	public void testisValidLinkedItem_allCases() {
		assertFalse(Link.isValidLinkedItem(null));
		assertTrue(Link.isValidLinkedItem(dirA_X));
		assertTrue(Link.isValidLinkedItem(rootDirD_terminated));
		assertTrue(Link.isValidLinkedItem(fileA_Y_1));
	}
	@Test
	public void testisValidLink_allCases() {
		assertFalse(linkA_X.isValidLink());
		assertFalse(linkA_Y_1.isValidLink());
		assertTrue(linkB_X_1_alfa.isValidLink());
		assertTrue(linkB_Y_1.isValidLink());
		assertFalse(linkB_Z_terminated.isValidLink());	
	}
	

	/**
	 * PARENT DIRECTORY METHODS
	 */
	
	@Test
	public void testCanHaveAsParentDirectory_allCases() {
		// We now have a fully closed specification and can test all cases!
		
		// 1. terminated item
		assertTrue(linkB_Z_terminated.canHaveAsParentDirectory(null));
		assertFalse(linkB_Z_terminated.canHaveAsParentDirectory(rootDirA));
		assertFalse(linkB_Z_terminated.canHaveAsParentDirectory(rootDirB));
		assertFalse(linkB_Z_terminated.canHaveAsParentDirectory(rootDirC));
		// 2. terminated parent directory
		assertFalse(linkB_Y.canHaveAsParentDirectory(rootDirD_terminated));
		assertFalse(linkB_Z_terminated.canHaveAsParentDirectory(rootDirD_terminated));
		// 3. null is not allowed for non-terminated links
		assertFalse(linkB_Y.canHaveAsParentDirectory(null));
		// 4. all non-terminated directories are allowed if the link is not terminated
		assertTrue(linkB_Y.canHaveAsParentDirectory(dirA_X));
		assertTrue(linkB_Y.canHaveAsParentDirectory(rootDirA));
		assertTrue(linkB_Y.canHaveAsParentDirectory(rootDirB));
		assertTrue(linkB_Y.canHaveAsParentDirectory(dirB_Y));	
	}
	
	
	/**
	 * DISK USAGE METHODS
	 */
	
	
	@Test
	public void testGetTotalDiskUsage_allCases() {
		assertEquals(linkA_X.getTotalDiskUsage(),0L);
		assertEquals(linkA_Y_1.getTotalDiskUsage(),0L);
		assertEquals(linkB_X_1_alfa.getTotalDiskUsage(),0L);
		assertEquals(linkB_Y_1.getTotalDiskUsage(),0L);
		assertEquals(linkB_X.getTotalDiskUsage(),0L);
		assertEquals(linkB_Y.getTotalDiskUsage(),0L);
		assertEquals(linkB_Z_terminated.getTotalDiskUsage(),0L);
	}
		
}
