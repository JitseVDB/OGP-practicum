package filesystem;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.*;
import filesystem.exception.*;

/**
 * A JUnit (4) test class for testing the non-private methods of the DiskItem Class.
 * The class is abstract, so we can use either one of the subclasses to test the members of the superclass.
 *   
 * @author  Tommy Messelis
 * @version 6.0
 */
public class DiskItemTest {
	
	
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
	// TESTING DISKITEM
	private static DiskItem item;
	
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
	 * Constructors for abstract classes must be tested with objects from the subclasses.
	 */
	
	@Test
	public void testConstructorDirectoryString_Legal() {
		
		// double check the current status
		assertEquals(rootDirA.getNbItems(),5);
		
		timeBefore = new Date();
		sleep();
		item = new File(rootDirA,"dirA_X_between",Type.JAVA);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of setName()
		assertEquals(item.getName(), "dirA_X_between");
		// 2. effect of setParentDirectory()
		// 2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), rootDirA);
		// 2.2 effect of addAsItem
		// 2.2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 2.2.1.1 postcondition on nbItems
		assertEquals(rootDirA.getNbItems(),6);
		// 2.2.1.2 postcondition on getItemAt()
		assertSame(rootDirA.getItemAt(2),item);
		// 2.2.1.3 postcondition on index of other items
		assertSame(rootDirA.getItemAt(3),dirA_Y);
		assertSame(rootDirA.getItemAt(4),fileA_X);
		assertSame(rootDirA.getItemAt(5),fileA_Y);
		// 2.2.2 effect of parent.setModificationTime()
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 5. postcondition on the termination status
		assertFalse(item.isTerminated());		
	}
	@Test
	public void testConstructorDirectoryString_Legal_InvalidName() {
		
		// double check the current status
		assertEquals(rootDirA.getNbItems(),5);
		
		timeBefore = new Date();
		sleep();
		item = new File(rootDirA,"?invalid?name",Type.TEXT);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of setName()
		assertEquals(item.getName(), item.getDefaultName());
		// 2. effect of setParentDirectory()
		// 2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), rootDirA);
		// 2.2 effect of addAsItem
		// 2.2.1 effect of addItemAt (based on the default name, it should have been added at index 6
		// 2.2.1.1 postcondition on nbItems
		assertEquals(rootDirA.getNbItems(),6);
		// 2.2.1.2 postcondition on getItemAt()
		assertSame(rootDirA.getItemAt(6),item);
		// 2.2.1.3 postcondition on index of other items (none here!)
		// 2.2.2 effect of parent.setModificationTime()
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 5. postcondition on the termination status
		assertFalse(item.isTerminated());
		
	}
	@Test
	public void testConstructorDirectoryString_Legal_Rootitem() {
		
		timeBefore = new Date();
		sleep();
		item = new Directory(null,"newDir");
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of setName()
		assertEquals(item.getName(), "newDir");
		// 2. effect of setParentDirectory()
		// 2.1 postcondition on the reference of item
		assertSame(item.getParentDirectory(), null);
		// 3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 5. postcondition on the termination status
		assertFalse(item.isTerminated());
		
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_ParentDirectory_Terminated() {	
		item = new File(rootDirD_terminated,"testFile",Type.JAVA);		
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_ValidNameNotUnique() {	
		item = new File(rootDirA,"fileA_X",Type.JAVA);		
	}
	@Test (expected = IllegalArgumentException.class)
	public void testConstructorDirectoryString_Illegal_InvalidNameNotUnique() {	
		item = new File(dirB_Y,"???",Type.JAVA);		
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testConstructorDirectoryString_Illegal_ParentDirectory_NotWritable() {	
		item = new File(rootDirC,"testFile",Type.JAVA);		
	}
	// the other exceptions thrown by effect clauses of the constructor cannot happen.
	// This is because e.g. addAsItem(.) will never be called with a null parameter from this constructor.
	// We will test those cases later when we test the addAsItem in more detail.
	@Test
	public void testConstructorString_Legal() {
		
		// this one is not straightforward, we use it in the constructor of Link, but actually
		// it is never used in any specification, so as far as we're concerned, it is never used.
		// We write a test anyway, using the Link constructor.
		
		timeBefore = new Date();
		sleep();
		item = new Link(rootDirA,"newLink",dirA_X);
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. effect of setName()
		assertEquals(item.getName(), "newLink");
		// 3. postcondition on the creation time
		assertTrue(item.getCreationTime().after(timeBefore));
		assertTrue(item.getCreationTime().before(timeAfter));
		// 4. postcondition on the modification time
		assertNull(item.getModificationTime());
		// 5. postcondition on the termination status
		assertFalse(item.isTerminated());		
	}
	
	
	/**
	 * DESTRUCTORS
	 */
	
	@Test
	public void testCanBeTerminated_allCases() {
		// In this class, we only have partial specification and thus,
		// we can only test those cases!
		
		// 1. already terminated disk item
		assertFalse(fileA_Z_terminated.canBeTerminated());
		// 2. not root and parent not writable
		assertFalse(linkB_X.canBeTerminated());
	}
	@Test
	public void testCanBeRecursivelyDeleted_allCases() {
		// In this class, we only have partial specification and thus,
		// we can only test those cases!
		
		// 1. already terminated disk item
		assertFalse(fileA_Z_terminated.canBeRecursivelyDeleted());
		// 2. not root and parent not writable
		assertFalse(linkB_X.canBeRecursivelyDeleted());
	}
	@Test
	public void testTerminate_LegalCase() {
		assertTrue(fileA_X.canBeTerminated());
		assertEquals(rootDirA.getNbItems(),5);
		
		timeBefore = new Date();
		sleep();
		fileA_X.terminate();
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. isTerminated
		assertTrue(fileA_X.isTerminated());
		// 2. isRoot
		assertTrue(fileA_X.isRoot());
		// 3. effect of parent.remove
		// 3.1 effect of removeItemAt(index)
		// 3.1.1 nbItems 
		assertEquals(rootDirA.getNbItems(),4);
		// 3.1.2 !hasAsItem
		assertFalse(rootDirA.hasAsItem(fileA_X));
		// 3.1.3 items shifted left
		assertSame(rootDirA.getItemAt(3),fileA_Y);
		assertSame(rootDirA.getItemAt(4),linkA_X);
		// 3.1.4 exceptions impossible
		// 3.2 effect of setModification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3.3 exceptions impossible
		
	}
	@Test (expected = IllegalStateException.class)
	public void testTerminate_IllegalCase_NotCanBeTerminated() {
		assertFalse(fileA_Z_terminated.canBeTerminated());
		fileA_Z_terminated.terminate();
		// Other cases where canBeTerminated returns false do not need to be considered.
		// We'll test the behaviour of that checker later.
		// Here, we must only test that the right exception comes in case of a !canBeTerminated!
	}
	@Test
	public void testDeleteREC_LegalCase() {
		assertTrue(fileA_X.canBeRecursivelyDeleted());
		assertEquals(rootDirA.getNbItems(),5);
		
		timeBefore = new Date();
		sleep();
		fileA_X.deleteRecursive();
		sleep();
		timeAfter = new Date();
		
		// check postconditions
		// 1. isTerminated
		assertTrue(fileA_X.isTerminated());
		// 2. isRoot
		assertTrue(fileA_X.isRoot());
		// 3. effect of parent.remove
		// 3.1 effect of removeItemAt(index)
		// 3.1.1 nbItems 
		assertEquals(rootDirA.getNbItems(),4);
		// 3.1.2 !hasAsItem
		assertFalse(rootDirA.hasAsItem(fileA_X));
		// 3.1.3 items shifted left
		assertSame(rootDirA.getItemAt(3),fileA_Y);
		assertSame(rootDirA.getItemAt(4),linkA_X);
		// 3.1.4 exceptions impossible
		// 3.2 effect of setModification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3.3 exceptions impossible
		
	}
	@Test (expected = IllegalStateException.class)
	public void testDeleteREC_IllegalCase_NotCanBeTerminated() {
		assertFalse(fileA_Z_terminated.canBeRecursivelyDeleted());
		fileA_Z_terminated.deleteRecursive();
	}
	
	
	/**
	 * NAME METHODS
	 * 
	 * The private setName does not need to be tested, no-one can call this method.
	 * (So we can't either in this testcase...)
	 */
	
	@Test
	public void testCanHaveAsName_allCases() {
		// In this class, we only have partial specification and thus,
		// we can only test those cases!
		
		// 1. null
		assertFalse(fileA_X.canHaveAsName(null));
		// 2. forbidden characters
		assertFalse(fileA_X.canHaveAsName("someting&with#specials?"));
	}
	@Test
	public void testGetDefaultName() {
		assertEquals(fileA_X.getDefaultName(),"new_disk_item");
		assertTrue(fileA_X.canHaveAsName(fileA_X.getDefaultName()));
	}
	@Test
	public void testChangeName_LegalCase_rootItem_validName() {
		assertTrue(rootDirA.canHaveAsName("new_valid_name"));
		
		timeBefore = new Date();
		sleep();
		rootDirA.changeName("new_valid_name");
		sleep();
		timeAfter = new Date();
		
		// check effects
		// 1. effect setName
		assertEquals("new_valid_name",rootDirA.getName());
		// 2. effect modification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3. effect order in parent: impossible (root item)
	}
	@Test
	public void testChangeName_LegalCase_rootItem_invalidName() {
		assertFalse(rootDirA.canHaveAsName("bad???NAME"));
		
		timeBefore = new Date();
		sleep();
		rootDirA.changeName("bad???NAME");
		sleep();
		timeAfter = new Date();
		
		// check effects
		// 1. effect setName
		assertEquals(rootDirA.getDefaultName(),rootDirA.getName());
		// 2. effect modification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3. effect order in parent: impossible (root item)
	}
	@Test
	public void testChangeName_LegalCase_NonRootItem_validName() {
		assertTrue(fileA_X.canHaveAsName("new_valid_name"));
		// Keep track of original items
		ArrayList<DiskItem> items = new ArrayList<DiskItem>();
		for (int i=1; i<=rootDirA.getNbItems(); i++) {
			items.add(rootDirA.getItemAt(i));
		}
				
		timeBefore = new Date();
		sleep();
		fileA_X.changeName("new_valid_name");
		sleep();
		timeAfter = new Date();
		
		// check effects
		// 1. effect setName
		assertEquals("new_valid_name",fileA_X.getName());
		// 2. effect modification time
		assertNotNull(fileA_X.getModificationTime());
		assertTrue(fileA_X.getModificationTime().after(timeBefore));
		assertTrue(fileA_X.getModificationTime().before(timeAfter));
		// 3. effect order in parent
		// 3.1 canHaveAsItemAt
		for (int i=1; i<=rootDirA.getNbItems(); i++) {
			assertTrue(rootDirA.canHaveAsItemAt(rootDirA.getItemAt(i),i));
		}
		// 3.2 items are still in the directory, possibly on other indices
		for (int i=0; i<items.size(); i++) {
			DiskItem item = items.get(i);
			boolean found = false;
			for (int j=1; j<=rootDirA.getNbItems(); j++) {
				if (rootDirA.getItemAt(j) == item) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}
	@Test
	public void testChangeName_LegalCase_NonRootItem_invalidName() {
		assertFalse(fileA_X.canHaveAsName("bad???NAME"));
		// Keep track of original items
		ArrayList<DiskItem> items = new ArrayList<DiskItem>();
		for (int i=1; i<=rootDirA.getNbItems(); i++) {
			items.add(rootDirA.getItemAt(i));
		}
		
		timeBefore = new Date();
		sleep();
		fileA_X.changeName("bad???NAME");
		sleep();
		timeAfter = new Date();
		
		// check effects
		// 1. effect setName
		assertEquals(fileA_X.getDefaultName(),fileA_X.getName());
		// 2. effect modification time
		assertNotNull(fileA_X.getModificationTime());
		assertTrue(fileA_X.getModificationTime().after(timeBefore));
		assertTrue(fileA_X.getModificationTime().before(timeAfter));
		// 3. effect order in parent
		// 3.1 canHaveAsItemAt
		for (int i=1; i<=rootDirA.getNbItems(); i++) {
			assertTrue(rootDirA.canHaveAsItemAt(rootDirA.getItemAt(i),i));
		}
		// 3.2 items are still in the directory, possibly on other indices
		for (int i=0; i<items.size(); i++) {
			DiskItem item = items.get(i);
			boolean found = false;
			for (int j=1; j<=rootDirA.getNbItems(); j++) {
				if (rootDirA.getItemAt(j) == item) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}
	@Test (expected = IllegalStateException.class)
	public void testChangeName_IllegalCase_Terminated() {
		rootDirD_terminated.changeName("valid");
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testChangeName_IllegalCase_ParentNotWritable() {
		dirB_Y.changeName("valid");
	}
	@Test (expected = IllegalArgumentException.class)
	public void testChangeName_IllegalCase_ParentAlreadyContainsItemWithSameName() {
		dirA_X.changeName("dirA_Y");
	}
	@Test (expected = IllegalArgumentException.class)
	public void testChangeName_IllegalCase_ParentAlreadyContainsItemWithDefaultName() {
		linkB_Y_1.changeName("invalid?Name");
	}
	/*
	 * unwritable items will be tested in the ActualDiskItem testclass!
	 */
	@Test
	public void testIsOrderedAfterString_allCases() {
		assertTrue(fileA_X.isOrderedAfter("a"));
		assertFalse(fileA_X.isOrderedAfter("z"));
		assertFalse(fileA_X.isOrderedAfter(fileA_X.getName()));
		assertFalse(fileA_X.isOrderedAfter((String)null));
	}
	@Test
	public void testIsOrderedBeforeString_allCases() {
		assertFalse(fileA_X.isOrderedBefore("a"));
		assertTrue(fileA_X.isOrderedBefore("z"));
		assertFalse(fileA_X.isOrderedBefore(fileA_X.getName()));
		assertFalse(fileA_X.isOrderedBefore((String)null));
	}
	@Test
	public void testIsOrderedAfterDiskItem_allCases() {
		assertTrue(fileA_Y.isOrderedAfter(fileA_X));
		assertFalse(fileA_X.isOrderedAfter(fileA_Y));
		assertFalse(fileA_X.isOrderedAfter(fileA_X));
		assertFalse(fileA_X.isOrderedAfter((DiskItem)null));
	}
	@Test
	public void testIsOrderedBeforeDiskItem_allCases() {
		assertFalse(fileA_Y.isOrderedBefore(fileA_X));
		assertTrue(fileA_X.isOrderedBefore(fileA_Y));
		assertFalse(fileA_X.isOrderedBefore(fileA_X));
		assertFalse(fileA_X.isOrderedBefore((DiskItem)null));
	}
	@Test
	public void testgetAbsolutePath_abstract() {
		// We can test abstract methods if they have specification. 
		// We simply use one of the subclasses.
		assertNotNull(fileA_X.getAbsolutePath());
		assertNotNull(linkA_X.getAbsolutePath());
		assertNotNull(rootDirA.getAbsolutePath());
	}
	
	
	
	/**
	 * CREATION TIME METHODS
	 */
	
	@Test
	public void testIsValidCreationTime_allCases() {
		assertFalse(DiskItem.isValidCreationTime(null));
		Date now = new Date();
		// This is probably not the right test result, because 'now' will already be
		// in the past when testing... 
		// We can't do any better than this.
		assertTrue(DiskItem.isValidCreationTime(now));
		Date inFuture = new Date(System.currentTimeMillis() + 1000*60*60);
		assertFalse(DiskItem.isValidCreationTime(inFuture));	
	}
	
	/**
	 * MODIFICATION TIME METHODS
	 */
	
	@Test
	public void testCanHaveAsModificationTime_allCases() {
		timeBefore = new Date();
		sleep();
		DiskItem item = new Directory("newDir");
		sleep();
		timeAfter = new Date();
		sleep();
		Date inFuture = new Date(System.currentTimeMillis() + 1000*60*60);
		
		// null
		assertTrue(item.canHaveAsModificationTime(null));
		// time before creation
		assertFalse(item.canHaveAsModificationTime(timeBefore));
		// time after creation and before now
		assertTrue(item.canHaveAsModificationTime(timeAfter));
		// time after creation and after now
		assertFalse(item.canHaveAsModificationTime(inFuture));
	}
	@Test
	public void testSetModificationTime_allCases() {
		timeBefore = new Date();
		sleep();
		rootDirC.setModificationTime();
		sleep();
		timeAfter = new Date();
		
		assertNotNull(rootDirC.getModificationTime());
		assertTrue(rootDirC.getModificationTime().after(timeBefore));
		assertTrue(rootDirC.getModificationTime().before(timeAfter));	
	}
	@Test
	public void testHasOverlappingUsePeriod_NullParameter() {
		assertFalse(fileB_X.hasOverlappingUsePeriod(null));
	}
	@Test
	public void testHasOverlappingUsePeriod_UnmodifiedItems() {
		// 1. prime object not modified:
		// 1.1 other object also not modified
		assertFalse(fileB_X.hasOverlappingUsePeriod(fileB_Y));
		// 1.2 other object is mofified
		assertFalse(fileB_X.hasOverlappingUsePeriod(rootDirA));
		// 2. other object not modifief:
		// 2.1 prime object not modified (same as 1.1)
		// 2.2 prime object is modifief
		assertFalse(rootDirA.hasOverlappingUsePeriod(fileB_Y));
	}
	@Test
	public void testHasOverlappingUsePeriod_ModifiedItems() {
		DiskItem prime, parameter;
		
		// 1. no overlap
		// 1.1 prime modified before parameter created
		prime = new Directory("prime");
		sleep();
		prime.setModificationTime();
		sleep();
		parameter = new Directory("parameter");
		sleep();
		parameter.setModificationTime();
		assertFalse(prime.hasOverlappingUsePeriod(parameter));
		// 1.2 prime created after parameter modified
		parameter = new Directory("parameter");
		sleep();
		parameter.setModificationTime();
		sleep();
		prime = new Directory("prime");
		sleep();
		prime.setModificationTime();
		assertFalse(prime.hasOverlappingUsePeriod(parameter));
		
		// 2. overlap: all other cases
		// 2.1 prime created, parameter created, parameter modified, prime modified
		prime = new Directory("prime");
		sleep();
		parameter = new Directory("parameter");
		sleep();
		parameter.setModificationTime();
		sleep();
		prime.setModificationTime();
		assertTrue(prime.hasOverlappingUsePeriod(parameter));
		// 2.2 prime created, parameter created, prime modified, parameter modified
		prime = new Directory("prime");
		sleep();
		parameter = new Directory("parameter");
		sleep();
		prime.setModificationTime();
		sleep();
		parameter.setModificationTime();
		assertTrue(prime.hasOverlappingUsePeriod(parameter));
		// 2.3 parameter created, prime created, prime modified, parameter modified
		parameter = new Directory("prime");
		sleep();
		prime = new Directory("parameter");
		sleep();
		prime.setModificationTime();
		sleep();
		parameter.setModificationTime();
		assertTrue(prime.hasOverlappingUsePeriod(parameter));
		// 2.4 parameter created, prime created, parameter modified, prime modified
		parameter = new Directory("prime");
		sleep();
		prime = new Directory("parameter");
		sleep();
		parameter.setModificationTime();
		sleep();
		prime.setModificationTime();
		assertTrue(prime.hasOverlappingUsePeriod(parameter));		
	}
	
	
	/**
	 * PARENT DIRECTORY METHODS
	 */
	
	@Test
	public void testCanHaveAsParentDirectory_allCases() {
		// In this class, we only have partial specification and thus,
		// we can only test those cases!
		
		// 1. terminated item
		assertTrue(rootDirD_terminated.canHaveAsParentDirectory(null));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirA));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirB));
		assertFalse(rootDirD_terminated.canHaveAsParentDirectory(rootDirC));
		// 2. terminated parent directory
		assertFalse(fileA_X.canHaveAsParentDirectory(rootDirD_terminated));
		assertFalse(fileA_Z_terminated.canHaveAsParentDirectory(rootDirD_terminated));
	}
	@Test
	public void testHasProperParentDirectory_allCases() {
		// 1. Root items
		// We cannot construct invalid root items, 
		// the setParent will never result in a raw item after execution.
		// So check only the valid root items
		assertTrue(rootDirA.hasProperParentDirectory());
		assertTrue(rootDirA.canHaveAsParentDirectory(null));
		assertTrue(rootDirA.isRoot());
		// 2. Non-root items
		// 2.1 We can check valid root items
		assertTrue(dirA_X.hasProperParentDirectory());
		assertTrue(dirA_X.canHaveAsParentDirectory(dirA_X.getParentDirectory()));
		assertTrue(dirA_X.getParentDirectory().hasAsItem(dirA_X));	
		// 2.2 There is no way of constructing items with an invalid parent directory.
		// 		We would have to be able to call private methods of the DiskItem class,
		//		which is impossible from this Testclass.
		// 	This is actually a good thing. Only the setParentDirectory is able to 
		//  manipulate the bidirectional relationship!
	}
	@Test
	public void testSetParentDirectory_legalCase_RootItem_NullParent() {
		assertTrue(rootDirA.canHaveAsParentDirectory(null));
		rootDirA.setParentDirectory(null);
		// check result
		// 1. parent is set
		assertNull(rootDirA.getParentDirectory());
		// 2. effects are not applicable here
	}
	@Test
	public void testSetParentDirectory_legalCase_NonRootItem_NullParent() {
		assertTrue(dirA_X.canHaveAsParentDirectory(null));
		timeBefore = new Date();
		sleep();
		dirA_X.setParentDirectory(null);
		sleep();
		timeAfter = new Date();
		
		// check result
		// 1. parent is set
		assertNull(dirA_X.getParentDirectory());
		// 3. effect of parent.remove
		// 3.1 effect of removeItemAt(index)
		// 3.1.1 nbItems 
		assertEquals(rootDirA.getNbItems(),4);
		// 3.1.2 !hasAsItem
		assertFalse(rootDirA.hasAsItem(dirA_X));
		// 3.1.3 items shifted left
		assertSame(rootDirA.getItemAt(3),fileA_Y);
		assertSame(rootDirA.getItemAt(4),linkA_X);
		// 3.1.4 exceptions impossible
		// 3.2 effect of setModification time
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3.3 exceptions impossible
	}
	@Test
	public void testSetParentDirectory_legalCase_RootItem_EffectiveParent() {
		timeBefore = new Date();
		sleep();
		rootDirB.setWritable(true);
		assertTrue(rootDirB.canHaveAsParentDirectory(rootDirA));
		rootDirB.setParentDirectory(rootDirA);
		sleep();
		timeAfter = new Date();
		
		// check result
		// 1. parent is set
		assertSame(rootDirB.getParentDirectory(),rootDirA);
		// 2 effect of addAsItem
		// 2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 2.1.1 postcondition on nbItems
		assertEquals(rootDirA.getNbItems(),6);
		// 2.1.2 postcondition on getItemAt()
		assertSame(rootDirA.getItemAt(3),rootDirB);
		// 2.1.3 postcondition on index of other items
		assertSame(rootDirA.getItemAt(4),fileA_X);
		assertSame(rootDirA.getItemAt(5),fileA_Y);
		// 2.2 effect of parent.setModificationTime()
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		// 3. effect of parent.remove not applicable here
	}
	@Test
	public void testSetParentDirectory_legalCase_NonRootItem_EffectiveParent() {
		assertTrue(dirA_X.canHaveAsParentDirectory(dirB_Y));
		timeBefore = new Date();
		sleep();
		dirA_X.setParentDirectory(dirB_Y);
		sleep();
		timeAfter = new Date();
		
		// check result
		// 1. parent is set
		assertSame(dirA_X.getParentDirectory(),dirB_Y);
		// 2 effect of addAsItem
		// 2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 2.1.1 postcondition on nbItems
		assertEquals(dirB_Y.getNbItems(),4);
		// 2.1.2 postcondition on getItemAt()
		assertSame(dirB_Y.getItemAt(1),dirA_X);
		// 2.1.3 postcondition on index of other items
		assertSame(dirB_Y.getItemAt(2),fileB_Y_1);
		assertSame(dirB_Y.getItemAt(3),linkB_Y_1);
		assertSame(dirB_Y.getItemAt(4),fileB_Y_2_default);
		// 2.2 effect of parent.setModificationTime()
		assertNotNull(dirB_Y.getModificationTime());
		assertTrue(dirB_Y.getModificationTime().after(timeBefore));
		assertTrue(dirB_Y.getModificationTime().before(timeAfter));
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
	}
	@Test (expected = IllegalStateException.class)
	public void testSetParentDirectory_IllegalCase_Terminated_ParentNotNull() {
		rootDirD_terminated.setParentDirectory(dirA_X);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testSetParentDirectory_IllegalCase_InvalidParent() {
		fileA_X.setParentDirectory(null);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testSetParentDirectory_IllegalCase_InvalidTerminatedParent() {
		fileA_X.setParentDirectory(rootDirD_terminated);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testSetParentDirectory_IllegalCase_ValidParentCantHaveItem() {
		// set parent to directory that already has this name in its contents
		fileA_X_1.changeName(fileA_X_1.getDefaultName());
		fileB_Y_2_default.setParentDirectory(dirA_X);
	}
	
	@Test
	public void testGetRoot() {
		assertSame(rootDirB.getRoot(), rootDirB);
		assertSame(dirB_X.getRoot(), rootDirB);
		assertSame(dirB_X_1.getRoot(), rootDirB);
		assertSame(dirB_X_1_alfa.getRoot(), rootDirB);
		// It may also be Files in case of a terminated file:
		assertSame(fileA_Z_terminated.getRoot(),fileA_Z_terminated);
	}
	@Test
	public void testIsRoot() {
		assertTrue(rootDirA.isRoot());
		assertTrue(fileA_Z_terminated.isRoot());
		assertFalse(dirB_X.isRoot());
		assertFalse(fileB_X_1_beta.isRoot());
	}
	
	
	@Test
	public void testMove_legalCase_RootItem() {
		timeBefore = new Date();
		sleep();
		assertTrue(rootDirA.canHaveAsParentDirectory(dirB_Y));
		rootDirA.move(dirB_Y);
		sleep();
		timeAfter = new Date();
		
		// check result
		// 1. effect of setParentDirectory
		// 1.1 parent is set
		assertSame(rootDirA.getParentDirectory(),dirB_Y);
		// 1.2 effect of addAsItem
		// 1.2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 1.2.1.1 postcondition on nbItems
		assertEquals(dirB_Y.getNbItems(),4);
		// 1.2.1.2 postcondition on getItemAt()
		assertSame(dirB_Y.getItemAt(1),rootDirA);
		// 1.2.1.3 postcondition on index of other items (none in this case)
		assertSame(dirB_Y.getItemAt(2),fileB_Y_1);
		assertSame(dirB_Y.getItemAt(3),linkB_Y_1);
		assertSame(dirB_Y.getItemAt(4),fileB_Y_2_default);
		// 1.2.2 effect of parent.setModificationTime()
		assertNotNull(dirB_Y.getModificationTime());
		assertTrue(dirB_Y.getModificationTime().after(timeBefore));
		assertTrue(dirB_Y.getModificationTime().before(timeAfter));
		// 1.3. effect of parent.remove not applicable here
		// 2. effect of setModificationTime
		assertNotNull(rootDirA.getModificationTime());
		assertTrue(rootDirA.getModificationTime().after(timeBefore));
		assertTrue(rootDirA.getModificationTime().before(timeAfter));
		
	}
	@Test
	public void testMove_legalCase_NonRootItem() {
		assertTrue(dirA_X.canHaveAsParentDirectory(dirB_Y));
		timeBefore = new Date();
		sleep();
		dirA_X.move(dirB_Y);
		sleep();
		timeAfter = new Date();
		
		// check result
		// 1. effect of setParentDirectory
		// 1.1. parent is set
		assertSame(dirA_X.getParentDirectory(),dirB_Y);
		// 1.2 effect of addAsItem
		// 1.2.1 effect of addItemAt (based on the chosen name, it should have been added at index 2
		// 1.2.1.1 postcondition on nbItems
		assertEquals(dirB_Y.getNbItems(),4);
		// 1.2.1.2 postcondition on getItemAt()
		assertSame(dirB_Y.getItemAt(1),dirA_X);
		// 1.2.1.3 postcondition on index of other items
		assertSame(dirB_Y.getItemAt(2),fileB_Y_1);
		assertSame(dirB_Y.getItemAt(3),linkB_Y_1);
		assertSame(dirB_Y.getItemAt(4),fileB_Y_2_default);
		// 1.2.2 effect of parent.setModificationTime()
		assertNotNull(dirB_Y.getModificationTime());
		assertTrue(dirB_Y.getModificationTime().after(timeBefore));
		assertTrue(dirB_Y.getModificationTime().before(timeAfter));
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
		// 2. effect of setModificationTime
		assertNotNull(dirA_X.getModificationTime());
		assertTrue(dirA_X.getModificationTime().after(timeBefore));
		assertTrue(dirA_X.getModificationTime().before(timeAfter));
	}
	@Test (expected = IllegalStateException.class)
	public void testMove_IllegalCase_Terminated() {
		rootDirD_terminated.move(dirA_X);
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testMove_IllegalCase_ParentNotWritable() {
		dirB_Y.move(rootDirA);
	}
	@Test (expected = DiskItemNotWritableException.class)
	public void testMove_IllegalCase_TargetNotWritable() {
		dirA_X.move(rootDirB);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testMove_IllegalCase_TargetNull() {
		fileA_X.move(null);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testMove_IllegalCase_TargetSame() {
		fileA_X.move(rootDirA);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testMove_IllegalCase_InvalidParent() {
		fileA_X.move(rootDirD_terminated);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testMove_IllegalCase_ValidParentCantHaveItem() {
		// set parent to directory that already has this name in its contents
		fileA_X_1.changeName(fileA_X_1.getDefaultName());
		fileB_Y_2_default.move(dirA_X);
	}
	
	@Test
	public void testIsDirectOrIndirectChildOf_allCases() {
		assertFalse(rootDirB.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(dirB_X.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(dirB_Y.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(dirB_Z.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_X.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_Y.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(linkB_X.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(linkB_Y.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(dirB_X_1.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_X_1.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_X_2.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_Y_1.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(linkB_Y_1.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_Y_2_default.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(dirB_X_1_alfa.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_X_1_alfa.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(fileB_X_1_beta.isDirectOrIndirectChildOf(rootDirB));
		assertTrue(linkB_X_1_alfa.isDirectOrIndirectChildOf(rootDirB));
		
		assertFalse(dirA_X.isDirectOrIndirectChildOf(rootDirB));
		assertFalse(dirA_Y.isDirectOrIndirectChildOf(rootDirB));
		assertFalse(fileA_X.isDirectOrIndirectChildOf(rootDirB));
		assertFalse(fileA_Y.isDirectOrIndirectChildOf(rootDirB));
		assertFalse(linkA_X.isDirectOrIndirectChildOf(rootDirB));
		assertFalse(linkA_Y_1.isDirectOrIndirectChildOf(rootDirB));		
	}
	
	/**
	 * DISK USAGE METHODS
	 * 
	 * We can test abstract methods if they have specification. 
	 * We simply use one of the subclasses.
	 */
	
	
	@Test
	public void testGetTotalDiskUsage_allCases() {
		assertTrue(rootDirA.getTotalDiskUsage() >= 0);
		assertTrue(dirA_X.getTotalDiskUsage() >= 0);
		assertTrue(fileA_X.getTotalDiskUsage() >= 0);
		assertTrue(linkA_X.getTotalDiskUsage() >= 0);
		assertTrue(rootDirC.getTotalDiskUsage() >= 0);
	}
	
}
