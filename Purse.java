import be.kuleuven.cs.som.annotate.*;

/**
 * A class of purses, involving contents.
 *
 * @invar	The contents of a purse is a positive number, less than or equal to the capacity of the purse.
 * 			| canHaveAsContents(getContents())
 *
 * @invar   If this purse is destroyed, its contents must be 0.
 *          | if (getCondition() == Condition.DESTROYED)
 *          |       then this.getContents == 0
 *
 *
 * @author  Jitse Vandenberghe
 * @version 1.1
 */
public class Purse extends StorageItem {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new purse with the given weight, base value and capacity.
     *
     * @param   weight
     *          The weight of the purse.
     *
     * @param   capacity
     *          The maximal amount of dukaten the purse can carry.
     *
     * @effect  The purse is initialized as a storage item.
     *          (weight, base value are set and an identification number is generated and assigned)
     *          | super(weight, 0, capacity)
     *
     * @note    The owner of the purse is initialized to null.
     */
    public Purse(int weight, int capacity) {
        super(weight, 0, capacity);
    }

    /**********************************************************
     * Content
     **********************************************************/

    /**
     * The amount of dukaten currently stored in this purse, also an integer number.
     */
    private int contents = 0;

    /**
     * Return the contents of this purse.
     *
     */
    @Basic @Raw
    public int getContents() {
        return this.contents;
    }

    /**
     * Set the contents of this purse to the given amount of dukaten.
     *
     * @param	amount
     * 			The amount of dukaten to be set as the contents of purse.
     *
     * @post	The contents is set to the given amount, if the amount is a
     * 			positive value less than or equal to the capacity of this purse.
     * 			Otherwise, if the amount is negative, the content is set to 0,
     * 			Otherwise, (thus if the amount is larger than the capacity)
     * 			the purse rips and becomes destroyed.
     * 			| if (amount >= 0 && amount < getCapacity())
     * 			|		then new.getContents() == amount
     * 			|		else if (amount < 0)
     * 			|			 	then new.getContents() == 0
     * 			|				else new.getContents() == 0 and new.getCondition = Condtion.DESTROYED
     *
     * @note	The setters should always ensure that the class invariants are not violated.
     * 			Their job is to only set the value if it is allowed.
     *
     * @note 	The setter is made private. Instead, we want the user to use the other methods to change
     * 			the contents of this purse (add/extract amount).
     *
     */
    @Model
    private void setContents(int amount) {
        if(amount >= 0 && amount <= getCapacity())
            this.contents = amount;
        else {
            if(amount < 0)
                this.contents = 0;
            else // amount > capacity
                this.destroy();
        }
    }

    /**
     * Checks whether the given contents is a valid value for this purse.
     *
     * @param	contents
     * 			The contents to check.
     * @return	True if the given contents is a positive integer less than or equal to the capacity of this purse.
     * 			| result == (contents >= 0 && contents <= this.getCapacity())
     *
     */
    @Raw
    public boolean canHaveAsContents(int contents) {
        return contents >= 0 && contents <= getCapacity();
    }

    /**
     * Add the given amount of dukaten to this purse.
     *
     * @param	amount
     * 			The amount of dukaten to be added to the contents of this purse.
     *
     * @effect	If the given amount is positive and the purse is not destroyed, the contents is set to the current contents + amount,
     * 			or to the capacity of this purse in case this sum exceeds the capacity.
     * 			Otherwise, the contents is not changed.
     * 			| if (amount >= 0)
     * 			|		then setContents(getContents() + amount)
     */
    public void addToContents(int amount) {
        if (!isDestroyed() && amount > 0) {
            setContents(getContents() + amount);
        }
    }

    /**
     * Extract the given amount of dukaten from this purse.
     *
     * @param	amount
     * 			The amount of dukaten to be extracted from the contents of this purse.
     *
     * @effect	If the given amount is positive and the purse is not destroyed, the contents is set to the current contents - amount,
     * 			or to the 0 in case this sum is negative.
     * 			Otherwise, the contents is not changed.
     * 			| if (amount >= 0)
     * 			|		then setContents(getContents() - amount)
     *
     */
    public void removeFromContents(int amount) {
        if (!isDestroyed() && amount > 0) {
            setContents(getContents() - amount);
        }
    }

    /**
     * Empty this purse.
     *
     * @effect	The content of this purse is set to 0.
     * 			| setContents(0)
     */
    public void empty() {
        setContents(0);
    }

    /**
     * Fill this purse to its full capacity.
     *
     * @effect	The contents of the purse is set to its capacity if the purse is not destroyed,
     *          otherwise, the contents is not changed.
     * 			| setContents(getCapacity())
     *
     */
    public void fillToCapacity() {
        if (!isDestroyed()) {
            setContents(getCapacity());
        }
    }

    /**
     * Calculate the amount of free space in this purse.
     *
     * @return	The difference between the capacity and the contents of this purse.
     * 			| result == getCapacity() - getContents()
     *
     */
    public int getFreeSpace() {
        return getCapacity() - getContents();
    }

    /**
     * Transfers the entire contents of the given purse to this purse.
     *
     * @param	other
     * 			The purse from which the contents should be transfered to this purse.
     *
     * @post	If the given purse is not effective or the same as this purse, nothing is transferred.
     * 			Otherwise, as much dukaten as possible is transferred from the given purse to this purse
     * 			s.t. the capacity of this purse is not exceeded. Any residual dukaten will be left in the given purse.
     * 			| if (other != null && other != this)
     * 			|		then (if (getFreeSpace() >= other.getContents())
     * 			|				then (new.getContents() == getContents() + other.getContents()
     * 			|					  and (new other).getContents() == 0 )
     * 			|				else (new.getContents() == 0 and new.getCondition() == Condtion.DESTROYED
     * 			|			 		  and (new other).getContents() == other.getContents() - getFreeSpace() ))
     */
    public void transferFrom(Purse other) {
        if ((other != null) && (other != this)) {
            if (other.getContents() <= this.getFreeSpace()) {
                this.addToContents(other.getContents());
                other.empty();
                // VERDER UITWERKEN LATER : "Lege geldbeurs moet op de grond gegooid worden."
            } else {
                other.removeFromContents(this.getFreeSpace());
                this.destroy();
            }
        }
    }


    /**********************************************************
     * Weight - total programming
     **********************************************************/

    /**
     * Returns the total weight of this purse (the weight of the purse itself and the weight of the dukaten stored inside).
     */
    public int getTotalWeight() {
        return getWeight() + 50 * getContents();
    }

    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Calculate the current value of this purse.
     *
     * @return  The calculated current value of the purse, by adding the value of the purse and its contents.
     *          | result == getBaseValue() + getContents()
     */
    @Override
    protected int calculateCurrentValue() {
        return getContents();
    }

    /**********************************************************
     * Condition
     **********************************************************/

    /**
     * The condition of the purse, either GOOD or DESTROYED.
     */
    private Condition condition = Condition.GOOD;

    /**
     * Return the condition of this purse.
     */
    @Basic
    public Condition getCondition() {
        return condition;
    }

    /**
     * Set the condition of this purse.
     *
     * @param   condition
     *          The new condition to set.
     */
    @Model
    private void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * Set this purse to destroyed and empty its contents.
     *
     * @effect  The contents of the purse is set to zero.
     *          | new.getContents() == 0
     *
     * @effect  The condition is set to DESTROYED
     *          | setCondition(Condition.DESTROYED)
     */
    @Model
    void destroy() {
        empty();
        setCondition(Condition.DESTROYED);
    }

    /**
     * Check if this purse is destroyed.
     *
     * @return  If and only if the purse is destroyed, false otherwise.
     *          | result == (this.condition == Condition.DESTROYED)
     */
    public boolean isDestroyed() {
        return this.condition == Condition.DESTROYED;
    }
}
