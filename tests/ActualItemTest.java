package filesystem;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;
import filesystem.exception.*;

/**
 * A JUnit (4) test class for testing the non-private methods of the ActualItem Class.
 * The class is abstract, so we can use either one of the subclasses to test the members of the superclass.
 *   
 * @author  Tommy Messelis
 * @version 6.0
 */
public class ActualItemTest {
	
	
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
	private static ActualItem item;					// <- the type of item is now ACTUALITEM!
	
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
	 * (There are only legal cases w.r.t. the writability.)
	 * 
	 * We can only do so because we read the implementation of the constructor and
	 * we know that it actually calls the superconstructor (which is tested and correct).
	 * This can not be deduced from the specification alone.
	 * If we want to be strict, we would have to test the complete specification of the 
	 * superconstructor effect too (like we do in the Link test).
	 */
	
	@Test
	public void testConstructorDirectoryStringActualItem_LegalWritable() {
		
		
		timeBefore = new Date();
		sleep();
		item = new Directory(rootDirA,"dirA_X_between", true);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name)   // <- can be skipped, but was already copypasted, so test it anyway!
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
		
		// 2. effect setWritable
		assertTrue(item.isWritable());
		
	}
	@Test
	public void testConstructorDirectoryStringActualItem_LegalUnWritable() {
		
		
		timeBefore = new Date();
		sleep();
		item = new Directory(null,"dirA_X_between", false);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of super(parent,name)
		// 1.1. effect of setName()
		assertEquals(item.getName(), "dirA_X_between");
		// 1.2. effect of setParentDirectory()
		// 1.2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), null);
		// 1.3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 1.4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 1.5. postcondition on the termination status
		assertFalse(item.isTerminated());	
		
		// 2. effect setWritable
		assertFalse(item.isWritable());
		
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
		// This is still not fully conclusive, so we just check the extra cases!
		
		// 1. already terminated item
		assertFalse(rootDirD_terminated.canBeTerminated());
		// 2. not root and parent not writable
		assertFalse(fileB_X_1_alfa.canBeTerminated());
		// 3. root item not writable
		assertFalse(rootDirC.canBeTerminated());
		// 4. non-root item not writable
		assertFalse(fileA_Y.canBeTerminated());	
	}
	@Test
	public void testCanBeRecursivelyDeleted_allCases() {
		// This is still not fully conclusive, so we just check the extra cases!
		
		// 1. already terminated item
		assertFalse(rootDirD_terminated.canBeRecursivelyDeleted());
		// 2. not root and parent not writable
		assertFalse(fileB_X_1_alfa.canBeRecursivelyDeleted());
		// 3. root item not writable
		assertFalse(rootDirC.canBeRecursivelyDeleted());
		// 4. non-root item not writable
		assertFalse(fileA_Y.canBeRecursivelyDeleted());
	}

	/**
	 * WRITABILITY
	 */
	
	@Test
	public void testsetWritable_allCases() {
		// 1. writable to unwritable
		dirA_X.setWritable(false);
		assertFalse(dirA_X.isWritable());
		// 1. writable to writable
		dirA_X.setWritable(true);
		assertTrue(dirA_X.isWritable());
		// 1. unwritable to unwritable
		fileA_Y.setWritable(false);
		assertFalse(fileA_Y.isWritable());
		// 1. unwritable to unwritable
		fileA_Y.setWritable(false);
		assertFalse(fileA_Y.isWritable());
		
	}
	
	
	/**
	 * NAME METHODS
	 * 
	 * There is one must exception now in changeName.
	 * The legal cases don't change.
	 * 
	 * Again, we only know so because the implementation 
	 * calls the method from the superclass.
	 * 
	 * Strictly speaking, based on the specification alone,
	 * we would have to test the complete behavior (like in
	 * the superclass).
	 */

	@Test (expected = DiskItemNotWritableException.class)
	public void testChangeName_IllegalCase_ItemNotWritable() {
		fileA_Y.changeName("validName");
	}
	
	
	/**
	 * PARENT DIRECTORY METHODS
	 */

	@Test (expected = DiskItemNotWritableException.class)
	public void testMove_IllegalCase_ItemNotWritable() {
		fileA_Y.move(dirA_X);
	}

}
