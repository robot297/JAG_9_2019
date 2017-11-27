## Lab 10: Command Line Database Programs: Product Inventory Manager   

Write an GUI application to manage a database of inventory items and the quantity of each item.
The user will be able to add a new item, change the quantity of any item, and delete an item.

### Database setup:

You will need to create a database called **products**.

You will also need to create a test database called **products_test**

The products database should have a table called **inventory**.
The products_test database should also have a table called **inventory**.
 
The inventory table (in both databases) should have two columns, 

* **product_name** for the name of a product
* **quantity** an integer number, for the quantity of that product in stock

For example, the inventory table may look like this, with some example data:

```
product_name        quantity

CPU                 250
Memory              100
Video Card          93

```


In SQLite, each database lives in a separate file.

In IntelliJ, click on the terminal icon in the lower-right corner of the IntelliJ window.
Type 

```
sqlite products.db
```

To create a database file called `products.db` and open the SQLite shell to work with this database.

Create an `inventory` table in `products.db` with this command

```
create table inventory (product_name text, quantity number);
```

Type 

```
.exit
```

To close the SQLite shell. 

Next, create a database file called `products_test.db` for the tests to use.

```
sqlite products_test.db
```

This will open the SQLite sell for `products_test.db`. 

Next, create an inventory table 

```
create table inventory (product_name text, quantity number);
```

Type 

```
.exit
```

To verify, run the tests `testTestDatabaseAndTableExists` and `testTestDevelopmentDatabaseAndTableExists` should both pass if your database is set up correctly. 



### Database setup, code


Verify the DB connection URL is correct in DBConfig.

For all parts of this lab, make sure you add appropriate error handling, and close all resources (ResultSets, PreparedStatements, Statements, Connections) when your program is done with them. Use PreparedStatements for updates, add, delete operations. 


### User Interface setup

Create a command-line interface to view, edit, add to, and delete, this data. 

The ProductDB program already shows a menu and calls a different method for each of the user's choices.
 
 All of your database interaction should be in ProductDB. Write methods in this class to read/write/edit/delete data in the database.
 
 For example, in ProductInventory showAll(), you'll have code that looks something like this,
 
 ```
 protected void showAll() {
     Object alldata = database.getAllData();  // write getAllData in ProductDB
     // Replace Object alldata with whatever data type getAllData returns, for example a list of items? 
     System.out.println(alldata); // replace with something more user-friendly
 }
 ```
 
 Your other methods, `addProduct()`    `editProduct()` and `deleteProduct()` will also call methods in ProductDB to do the appropriate tasks.
 
  **Showing all Products, showAll()**
  
   Call your new method in ProductDB to get all of the product data. Sort your data by name, in alphabetical order.  Display product names followed by quantity.
   
   Do not return a ResultSet from ProductDB. Return a list, or something that showAll can easily use to display all products.
   
   `showAll` should not use any user input.
   
 
 **Adding a Product, addProduct()**
 
 Ask the user for the name, and the quantity. Use stringInput and intInput. 
 Don't use any other input statements.
 Call your new method in ProductDB to add a product with this information.
 
 If the user enters a name that already exists in the database, display an error message and show the main menu again. Don't ask the user to try again from addProduct. Don't modify the database.
 
 
 **Editing a Product, editProduct()**
 
 Ask the user for the name, and the new quantity.  Use stringInput and intInput. Don't use any other input statements.
 
  Call your new method in ProductDB to edit the product with this name, and update the quantity.
  
  If the user enters a name that does not exist in the database, display an error message and show the main menu again. Don't ask the user to try again from editProduct. Don't modify the database.
  

  **Deleting a Product, deleteProduct()**
  
  Ask the user for the name. Use stringInput. 
  
  Don't use any other input statements.
  
  Call your new method in ProductDB to delete the product with this name.
   
  If the user enters a name that does not exist in the database, display an error message and show the main menu again. Don't ask the user to try again from deleteProduct. Don't modify the database.

 