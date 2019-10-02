package week_9;

import static input.InputUtils.positiveIntInput;

class ProductManager {
    
    ProductDB database;
    
    private String menu = "" +
            "1. Show all products\n" +
            "2. Add a product\n" +
            "3. Edit a product's quantity\n" +
            "4. Delete a product\n" +
            "9. Quit";
    
    public static void main(String[] args) {
        ProductManager productManager = new ProductManager();
        productManager.start();
    }
    
    
    public void start() {
        database = new ProductDB();
        showMenuDoAction();
    }
    
    
    private void showMenuDoAction() {
        
        int choice;
        
        do {
            System.out.println(menu);
            choice = positiveIntInput("Enter choice");
            
            switch (choice) {
                case 1:
                    showAll();
                    break;
                case 2:
                    addProduct();
                    break;
                case 3:
                    editProductQuantity();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 9:
                    break;
                default:
                    System.out.println("Invalid choice, please try again");
            }
            
        } while (choice != 9);
    }
    
    
    protected void showAll() {
        // TODO write and call method in ProductDB to fetch all products, sorted by product name.
        // TODO display all products
    }
    
    protected void addProduct() {
        // TODO ask user for product name and quantity
        // TODO write and call method in ProductDB to add this product
        // TODO Deal with product already existing in DB. Do not add another product with the same name.
    }
    
    protected void editProductQuantity() {
        // TODO ask user which product to edit
        // TODO write and call method in ProductDB to edit this product's quantity. Display confirmation message if product found and quantity edited.
        // TODO Deal with product not existing in DB - do not make any edits, display helpful message to user
    }
    
    protected void deleteProduct() {
        // TODO ask user for the name of a product to delete
        // TODO write and call method in ProductDB to delete this product's record from the database. Display confirmation message.
        // TODO Deal with product not existing in DB - do not make any edits, display helpful message to user
    }
    
}