/**
 *
 * A class representing the anchor points in the game.
 *
 * @author Jitse Vandenberghe
 *
 * @version 1.0
 */
public class AnchorPoint {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new anchor point with the given name.
     *
     * @param name
     *        The name of the new entity.
     *
     * @post The name is set to the given name.
     *       | new.getName() == name
     */
    public AnchorPoint(String name) {
        this.name = name;
    }

    /**********************************************************
     * Name
     **********************************************************/

    /**
     * Variable referencing the name of an anchor point.
     */
    private final String name; // for Heroes this has meaning, for monsters is null

    /**
     * Returns the name of the anchor point.
     */
    public String getName() {
        return name;
    }

    /**********************************************************
     * Item
     **********************************************************/

    /**
     * Variable referencing an item connected to this anchor point.
     */
    private Equipment item;

    /**
     * Returns the item connected to the anchor point.
     */
    public Equipment getItem() {
        return item;
    }

    /**
     * Set the item connected to this anchor point.
     *
     * @param   item
     *          The new item to connect to the anchor point.
     */
    public void setItem(Equipment item) {
        this.item = item;
    }

    /**
     * Check wether the given anchor point is empty.
     *
     * @return  True if there is no item that is connected to the anchor point,
     *          false otherwise.
     *          | result == (item == null)
     */
    public boolean isEmpty() {
        return item == null;
    }
}
