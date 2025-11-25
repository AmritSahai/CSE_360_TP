package entityClasses;

/*******
 * <p> Title: ParameterCategory Class </p>
 * 
 * <p> Description: This ParameterCategory class represents a category within a grading parameter.
 * Each category has a name and a weight (0.0 - 1.0) that contributes to the total parameter score.</p>
 * 
 * <p> Copyright: Joseph © 2025 </p>
 * 
 * @author Joseph
 * 
 * @version 1.00		2025-01-16 Initial version
 */

public class ParameterCategory {
    
    private String categoryName;
    private double weight; // Weight between 0.0 - 1.0
    
    // Constants for validation
    public static final int MAX_CATEGORY_NAME_LENGTH = 100;
    public static final double WEIGHT_MIN = 0.0;
    public static final double WEIGHT_MAX = 1.0;
    
    /*****
     * <p> Method: ParameterCategory() </p>
     * 
     * <p> Description: Default constructor. </p>
     */
    public ParameterCategory() {
        this.categoryName = "";
        this.weight = 0.0;
    }
    
    /*****
     * <p> Method: ParameterCategory(String categoryName, double weight) </p>
     * 
     * <p> Description: Constructor to create a parameter category with name and weight. </p>
     * 
     * @param categoryName the name of the category
     * @param weight the weight of this category (0.0 - 1.0)
     */
    public ParameterCategory(String categoryName, double weight) {
        this.categoryName = categoryName;
        this.weight = weight;
    }
    
    /*****
     * <p> Method: String validateCategory() </p>
     * 
     * <p> Description: Validates the category data. </p>
     * 
     * @return empty string if valid, error message if invalid
     */
    public String validateCategory() {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "Category name cannot be empty.";
        }
        
        if (categoryName.length() > MAX_CATEGORY_NAME_LENGTH) {
            return "Category name cannot exceed " + MAX_CATEGORY_NAME_LENGTH + " characters.";
        }
        
        if (weight < WEIGHT_MIN || weight > WEIGHT_MAX) {
            return "Category weight must be between " + WEIGHT_MIN + " and " + WEIGHT_MAX + ".";
        }
        
        return ""; // Valid
    }
    
    /*****
     * <p> Method: String getCategoryName() </p>
     * 
     * <p> Description: Returns the name of this category. </p>
     * 
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }
    
    /*****
     * <p> Method: void setCategoryName(String categoryName) </p>
     * 
     * <p> Description: Sets the name of this category. </p>
     * 
     * @param categoryName the category name to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    /*****
     * <p> Method: double getWeight() </p>
     * 
     * <p> Description: Returns the weight of this category. </p>
     * 
     * @return the weight (0.0 - 1.0)
     */
    public double getWeight() {
        return weight;
    }
    
    /*****
     * <p> Method: void setWeight(double weight) </p>
     * 
     * <p> Description: Sets the weight of this category. </p>
     * 
     * @param weight the weight to set (must be between WEIGHT_MIN and WEIGHT_MAX)
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}

