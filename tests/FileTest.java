package filesystem;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;
import filesystem.exception.*;

/**
 * A JUnit (4) test class for testing the non-private methods of the File Class.
 *   
 * @author  Tommy Messelis
 * @version 6.0
 */
public class FileTest {
	
	
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
	private static File fileA_X, fileA_Y, fileA_Z_terminated, fileB_X, fileB_Y;
	// second level files:
	@SuppressWarnings("unused")
	private static File fileA_X_1, fileA_X_2, fileA_Y_1, fileA_Y_2, fileB_X_1, fileB_X_2, fileB_Y_1, fileB_Y_2_default;
	// third level files:
	@SuppressWarnings("unused")
	private static File fileB_X_1_alfa, fileB_X_1_beta;
		
	// LINKS:
	// first level files:
	@SuppressWarnings("unused")
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
	private static File item;					// <- the type of item is now FILE!
	
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
	public void testConstructorDirectoryStringTypeIntBoolean_Legal() {	
		
		timeBefore = new Date();
		sleep();
		item = new File(rootDirA,"dirA_X_between",Type.TEXT,500,false);
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
		
		// 2. effect setSize (only when precondition is satisfied)
		assertTrue(File.isValidSize(500));
		assertEquals(item.getSize(),500);
		// 3. postcondition on type (only when precondition is satisfied)
		assertTrue(File.isValidType(Type.TEXT));
		assertSame(item.getType(),Type.TEXT);
		
	}
	@Test
	public void testConstructorDirectoryStringType_Legal() {	
		
		timeBefore = new Date();
		sleep();
		item = new File(rootDirA,"dirA_X_between",Type.PDF);
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
		
		// 2. effect setSize (only when precondition is satisfied)
		assertTrue(File.isValidSize(0));
		assertEquals(item.getSize(),0);
		// 3. postcondition on type (only when precondition is satisfied)
		assertTrue(File.isValidType(Type.PDF));
		assertSame(item.getType(),Type.PDF);
		
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
		
		// 1. already terminated file
		assertFalse(fileA_Z_terminated.canBeTerminated());
		// 2. file not writable
		assertFalse(fileB_Y.canBeTerminated());
		// 3. not root and parent not writable
		assertFalse(fileB_X.canBeTerminated());
		// 4. root file is not possible
		// 5. non root file in writable directory
		assertTrue(fileA_X.canBeTerminated());
	}
	@Test
	public void testCanBeRecursivelyDeleted_allCases() {
		// We now have a fully closed specification and can test all cases!

		// 1. already terminated file
		assertFalse(fileA_Z_terminated.canBeRecursivelyDeleted());
		// 2. file not writable
		assertFalse(fileB_Y.canBeRecursivelyDeleted());
		// 3. not root and parent not writable
		assertFalse(fileB_X.canBeRecursivelyDeleted());
		// 4. root file is not possible
		// 5. non root file in writable directory
		assertTrue(fileA_X.canBeRecursivelyDeleted());
	}

	
	/**
	 * NAME METHODS
	 */
	
	@Test
	public void testCanHaveAsName_allCases() {
		// We now have a fully closed specification and can test all cases!

		// 1. null
		assertFalse(fileA_X.canHaveAsName(null));
		// 2. forbidden characters
		assertFalse(fileA_X.canHaveAsName("someting&with#specials?"));
		// 3. valid names
		assertTrue(fileA_X.canHaveAsName("aValidName.with.dots_and_underscores0and9numbers4"));
	}
	@Test
	public void testgetAbsolutePath_allCases() {
		assertNotNull(fileA_X.getAbsolutePath());
		assertEquals(fileA_X.getAbsolutePath(),"/dirA/fileA_X.txt");
		assertNotNull(fileA_Y_1.getAbsolutePath());
		assertEquals(fileA_Y_1.getAbsolutePath(),"/dirA/dirA_Y/fileA_Y_1.java");
		assertNotNull(fileA_Z_terminated.getAbsolutePath());
		assertEquals(fileA_Z_terminated.getAbsolutePath(),"/fileA_Z.pdf");	
	}
	@Test
	public void testToString_allCases() {
		assertEquals(fileA_X.toString(),fileA_X.getName()+"."+fileA_X.getType().getExtension());
		assertEquals(fileA_Y.toString(),fileA_Y.getName()+"."+fileA_Y.getType().getExtension());
		assertEquals(fileA_Z_terminated.toString(),fileA_Z_terminated.getName()+"."+fileA_Z_terminated.getType().getExtension());
	}
	
	
	/**
	 * TYPE METHODS
	 */
	
	@Test
	public void testisValidType_allCases() {
		assertFalse(File.isValidType(null));
		// check all types automatically:
		for (Type t: Type.values()) {
			assertTrue(File.isValidType(t));
		}
	}
	
	/**
	 * SIZE METHODS
	 * 
	 * The private methods setSize and changeSize do not need to be tested.
	 * No-one can call these methods.
	 * We must however verify their effects through the enlarge/shorten methods.
	 * 
	 * Do an extended test with special (valid) cases.
	 */
	
	@Test
	public void testIsValidSize_allCases() {
		assertTrue(File.isValidSize(0));
		assertTrue(File.isValidSize(1));
		assertTrue(File.isValidSize(File.getMaximumSize()/2));
		assertTrue(File.isValidSize(File.getMaximumSize()-1));
		assertTrue(File.isValidSize(File.getMaximumSize()));
		assertFalse(File.isValidSize(-1));
		if (File.getMaximumSize() < Integer.MAX_VALUE) {
			assertFalse(File.isValidSize(File.getMaximumSize()+1));
		}
	}
	
	@Test
	public void testEnlarge_LegalCase() {
		//ensure preconditions
		assertTrue(File.isValidSize(fileA_X.getSize()+300));
		
		timeBefore = new Date();
		sleep();
		fileA_X.enlarge(300);
		sleep();
		timeAfter = new Date();		
		
		// check postconditions
		// 1. effect of setSize(getSize()+delta)
		assertEquals(fileA_X.getSize(),400);
		// 2. effect of setModTime
		assertNotNull(fileA_X.getModificationTime());
		assertTrue(fileA_X.getModificationTime().after(timeBefore));
		assertTrue(fileA_X.getModificationTime().before(timeAfter));	
	}
	public void testEnlarge_LegalCaseToMax() {
		//ensure preconditions
		assertTrue(File.isValidSize(File.getMaximumSize()));
		
		timeBefore = new Date();
		sleep();
		fileA_X.enlarge(File.getMaximumSize()-100);
		sleep();
		timeAfter = new Date();		
		
		// check postconditions
		// 1. effect of setSize(getSize()+delta)
		assertEquals(fileA_X.getSize(),File.getMaximumSize());
		// 2. effect of setModTime
		assertNotNull(fileA_X.getModificationTime());
		assertTrue(fileA_X.getModificationTime().after(timeBefore));
		assertTrue(fileA_X.getModificationTime().before(timeAfter));	
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testEnlarge_IllegalCase_NotWritable() {
		fileA_Y.enlarge(300);
	}
	/*
	 * Other special cases are blocked by the preconditions. 
	 * We don't need to test these.
	 */
	@Test
	public void testShorten_LegalCase() {
		//ensure preconditions
		assertTrue(File.isValidSize(fileA_X.getSize()-50));
		
		timeBefore = new Date();
		sleep();
		fileA_X.shorten(50);
		sleep();
		timeAfter = new Date();		
		
		// check postconditions
		// 1. effect of setSize(getSize()+delta)
		assertEquals(fileA_X.getSize(),50);
		// 2. effect of setModTime
		assertNotNull(fileA_X.getModificationTime());
		assertTrue(fileA_X.getModificationTime().after(timeBefore));
		assertTrue(fileA_X.getModificationTime().before(timeAfter));	
	}
	public void testShorten_LegalCaseTo0() {
		//ensure preconditions
		assertTrue(File.isValidSize(0));
		
		timeBefore = new Date();
		sleep();
		fileA_X.shorten(100);
		sleep();
		timeAfter = new Date();		
		
		// check postconditions
		// 1. effect of setSize(getSize()+delta)
		assertEquals(fileA_X.getSize(),0);
		// 2. effect of setModTime
		assertNotNull(fileA_X.getModificationTime());
		assertTrue(fileA_X.getModificationTime().after(timeBefore));
		assertTrue(fileA_X.getModificationTime().before(timeAfter));	
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testShorten_IllegalCase_NotWritable() {
		fileB_Y.shorten(300);
	}
	
	
	/**
	 * PARENT DIRECTORY METHODS
	 */
	
	@Test
	public void testCanHaveAsParentDirectory_allCases() {
		// We now have a fully closed specification and can test all cases!
		
		// 1. terminated item
		assertTrue(fileA_Z_terminated.canHaveAsParentDirectory(null));
		assertFalse(fileA_Z_terminated.canHaveAsParentDirectory(rootDirA));
		assertFalse(fileA_Z_terminated.canHaveAsParentDirectory(rootDirB));
		assertFalse(fileA_Z_terminated.canHaveAsParentDirectory(rootDirC));
		// 2. terminated parent directory
		assertFalse(fileB_X.canHaveAsParentDirectory(rootDirD_terminated));
		assertFalse(fileA_Z_terminated.canHaveAsParentDirectory(rootDirD_terminated));
		// 3. null is not allowed for non-terminated files
		assertFalse(fileB_X.canHaveAsParentDirectory(null));
		// 4. all non-terminated directories are allowed if the link is not terminated
		assertTrue(fileB_X.canHaveAsParentDirectory(dirA_X));
		assertTrue(fileB_X.canHaveAsParentDirectory(rootDirA));
		assertTrue(fileB_X.canHaveAsParentDirectory(rootDirB));
		assertTrue(fileB_X.canHaveAsParentDirectory(dirB_Y));	
	}
	
	
	/**
	 * DISK USAGE METHODS
	 */
	
	@Test
	public void testGetTotalDiskUsage_allCases() {
		assertEquals(fileA_X_1.getTotalDiskUsage(),fileA_X_1.getSize());
		assertEquals(fileA_X_2.getTotalDiskUsage(),fileA_X_2.getSize());
		assertEquals(fileA_Y_1.getTotalDiskUsage(),fileA_Y_1.getSize());
		assertEquals(fileA_X.getTotalDiskUsage(),fileA_X.getSize());
		assertEquals(fileA_Y.getTotalDiskUsage(),fileA_Y.getSize());
		assertEquals(fileB_X.getTotalDiskUsage(),fileB_X.getSize());
		assertEquals(fileA_Z_terminated.getTotalDiskUsage(),fileA_Z_terminated.getSize());
	}
		
}
