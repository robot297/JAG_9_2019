package week_9;

import input.InputUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import test_utils.PrintUtils;

import java.sql.*;
import java.util.ArrayList;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)  // Needed for PowerMock to record method calls
@PrepareForTest(InputUtils.class)   // To enable InputUtils to be mocked
public class ProductInventoryTest {
    
    private static final int TIMEOUT = 10000;
    
    private ProductManager productInventoryProgram;
    
    private String testDatabaseURL = "jdbc:sqlite:products_test.db";
    private String developmentDatabaseURL = "jdbc:sqlite:products.db";
    
    private String[][] example = {
            {"Leopard", "100"},
            {"Aardvark", "200"},
            {"Badger", "300"},
    };
    
    
    private String[][] exampleSorted = {
            {"Aardvark", "200"},
            {"Badger", "300"},
            {"Leopard", "100"}
    };
    
    private String sortedDataString = "" +
            "            { \"Aardvark\", \"200\" },\n" +
            "            { \"Badger\", \"300\" },\n" +
            "            { \"Leopard\", \"100\" }";
    
    
    
    @Before
    public void setUp() throws Exception {
        
        // Replace database with test DB
        DBConfig.db_url = testDatabaseURL;
        
        deleteTestData();   // remove all data from test DB
        addTestData();
        
        productInventoryProgram = new ProductManager();
        productInventoryProgram.database = new ProductDB();
        
    }
    
    
    
    private void deleteTestData() {
        try (Connection con = DriverManager.getConnection(testDatabaseURL)) {
            String sql = "DELETE FROM inventory";
            Statement statement = con.createStatement();
            statement.execute(sql);
            
            statement.close();
            con.close();
            
        } catch (SQLException e) {
            fail("SQLException deleting data from test database." + e.getMessage());
        }
        
    }
    
    
    
    private void addTestData() {
        try (Connection con = DriverManager.getConnection(testDatabaseURL)) {
            String sql = "INSERT INTO inventory (name, quantity) values (?, ?)";
            
            PreparedStatement statement = con.prepareStatement(sql);
            
            for (String[] data : example) {
                
                statement.setString(1, data[0]);
                statement.setInt(2, Integer.parseInt(data[1]));
                statement.execute();
            }
            
            statement.close();
           
            
        } catch (SQLException e) {
            fail("SQLException adding data to test database." + e.getMessage());
        }
    }
   
    
    
    @Test(timeout = TIMEOUT)
    public void testTestDatabaseAndTableExists() throws Exception {
        testTableExists(testDatabaseURL);
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testTestDevelopmentDatabaseAndTableExists() throws Exception {
        testTableExists(developmentDatabaseURL);
    }
    
    
    
    public void testTableExists(String dbURL) throws Exception {
        
        try (Connection conn = DriverManager.getConnection(dbURL);
            Statement statement = conn.createStatement() ) {
    
            String tableInfo = "PRAGMA table_info(inventory)";
            ResultSet rs = statement.executeQuery(tableInfo);
            
            rs.next();
            String productNameCol = rs.getString(2);
            String productNameType = rs.getString(3);
            
            rs.next();
            String quantityCol = rs.getString(2);
            String quantityType = rs.getString(3);
            
            assertFalse("The database should only contain two columns, name and quantity.", rs.next());   // No more columns.
            
            assertEquals("The first column's name should be 'name'", "name", productNameCol.toLowerCase());
            assertEquals("The first column's type should be 'text'", "text", productNameType.toLowerCase());
    
            assertEquals("The second column's name should be 'quantity'", "quantity", quantityCol.toLowerCase());
            assertEquals("The second column's type should be 'integer'", "integer", quantityType.toLowerCase());
    
    
        } catch (SQLException e) {
            fail("Databases are not configured correctly, " + e.getMessage());
        }
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testShowAllProducts() {
        
        productInventoryProgram = new ProductManager();
        productInventoryProgram.database = new ProductDB();
        
        PrintUtils.catchStandardOut();
        productInventoryProgram.showAll();
        String out = PrintUtils.resetStandardOut();
        
        // Should contain example data sorted, in the given order
        
        int startSearch = 0;
        
        for (String[] row : exampleSorted) {
            for (String data : row) {
                
                startSearch = out.indexOf(data, startSearch);
                assertTrue("Displaying all data; with example data: \n" + sortedDataString +
                                "\nthe program printed \n" + out +
                                "\nMake sure you print all the data, name then quantity, sorted by product name, in alphabetical order",
                        startSearch != -1);
            }
        }
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testAddNewDuplicateProductShowErrorMessage() {
    
        PrintUtils.catchStandardOut();
        
        mockNameQuantity("Aardvark", 1400);
        productInventoryProgram.addProduct();
        
        String out = PrintUtils.resetStandardOut();
    
        String expectedErrorMessage = "Product already in database";
        assertTrue("If user tries to add a duplicate product, print this message: " + expectedErrorMessage, out.contains(expectedErrorMessage));
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testAddNewDuplicateProduct() {
        
        mockNameQuantity("Hedgehog", 400);
        
        productInventoryProgram.addProduct();
        
        // Check the database
        
        String[][] examplePlusNew = {
                {"Aardvark", "200"},
                {"Badger", "300"},
                {"Hedgehog", "400"},
                {"Leopard", "100"}
        };
        
        checkDatabase(examplePlusNew);   // Contains assert statements
        
        mockNameQuantity("Hedgehog", 1400);
        
        productInventoryProgram.addProduct();
        
        checkDatabase(examplePlusNew);   // Database should not change, Hedgehog quantity should still be 400
        
    }
    
    
    
   @Test(timeout = TIMEOUT)
    public void testAddNewProduct() {
    
        PrintUtils.catchStandardOut();
    
        mockNameQuantity("Hedgehog", 400);
        
        productInventoryProgram.addProduct();
        
        String out = PrintUtils.resetStandardOut();
    
        String unexpectedErrorMessage = "Product already in database";
        assertTrue("If user adds a new product, don't print an error message: ", out.contains(unexpectedErrorMessage));
        
        // Check the database
        
        String[][] examplePlusNew = {
                {"Aardvark", "200"},
                {"Badger", "300"},
                {"Hedgehog", "400"},
                {"Leopard", "100"}
        };
        
        checkDatabase(examplePlusNew);   // Contains assert statements
        
        // Expect to add 100 Velociraptors
        mockNameQuantity("Velociraptor", 100);
        
        productInventoryProgram.addProduct();
        
        String[][] examplePlusAnotherNew = {
                {"Aardvark", "200"},
                {"Badger", "300"},
                {"Hedgehog", "400"},
                {"Leopard", "100"},
                {"Velociraptor", "100"},
        };
        
        checkDatabase(examplePlusAnotherNew);
        
    }
  
    
    
    @Test(timeout = TIMEOUT)
    public void testEditProduct() {
    
        mockNameQuantity("Aardvark", 400);
        
        productInventoryProgram.editProductQuantity();
        
        // Check the database
        
        String[][] exampleWithEdit = {
                {"Aardvark", "400"},
                {"Badger", "300"},
                {"Leopard", "100"}
        };
        
        checkDatabase(exampleWithEdit);
        
        
        mockNameQuantity("Aardvark", 32);
        productInventoryProgram.editProductQuantity();
        
        // Check the database
        
        String[][] exampleWithEdit2 = {
                {"Aardvark", "32"},
                {"Badger", "300"},
                {"Leopard", "100"}
        };
        
        checkDatabase(exampleWithEdit2);
        
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testEditNonExistentProduct() {
        
        mockNameQuantity("Velociraptor", 64);
        productInventoryProgram.editProductQuantity();
        checkDatabase(exampleSorted);  // no edits made
        
    }
    
    
    @Test(timeout = TIMEOUT)
    public void testEditNonExistentProductShowsErrorMessage() {
        
        PrintUtils.catchStandardOut();
        
        mockNameQuantity("Velociraptor", 64);
        productInventoryProgram.editProductQuantity();
        
        String out = PrintUtils.resetStandardOut();
    
        String expectedErrorMessage = "Product to edit not found";
        assertTrue("If user tries to edit a product that is not in the database, print this message: " + expectedErrorMessage, out.contains(expectedErrorMessage));
    }
  
    
    
    @Test(timeout = TIMEOUT)
    public void testDeleteProduct() {
        
        mockName("Badger");
        
        productInventoryProgram.deleteProduct();
        
        // Check the database
        
        String[][] exampleDeleted = {
                {"Aardvark", "200"},
                {"Leopard", "100"}
        };
        
        checkDatabase(exampleDeleted);
        
        mockName("Aardvark");
        
        productInventoryProgram.deleteProduct();
        
        String[][] exampleDeleteAnother = {
                {"Leopard", "100"},
        };
        
        checkDatabase(exampleDeleteAnother);
        
        // delete final item
        
        mockName("Leopard");
        
        productInventoryProgram.deleteProduct();
        
        String[][] exampleDeleteLast = {
            
        };
        
        checkDatabase(exampleDeleteLast);
        
        // Delete from empty database, should not error
        
        mockName("Squirrel");
        
        productInventoryProgram.deleteProduct();
        
        checkDatabase(exampleDeleteLast);
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testDeleteNonExistentProduct() {
        
        mockName("Owl");
        
        productInventoryProgram.deleteProduct();
        
        // Check the database. Data should be the same.
        checkDatabase(exampleSorted);
        
    }
    
    
    @Test(timeout = TIMEOUT)
    public void testDeleteNonExistentProductShowsErrorMessage() {
    
        PrintUtils.catchStandardOut();
    
        mockName("Owl");
        productInventoryProgram.deleteProduct();
    
        String out = PrintUtils.resetStandardOut();
    
        String expectedErrorMessage = "Product to delete not found";
        assertTrue("If user tries to delete a product that is not in the database, print this message: " + expectedErrorMessage, out.contains(expectedErrorMessage));
    
    
    }
    
    
    
    /* ****** Helper methods for checking the database **** */
    
    // Provide mock return values from stringInput and intInput
    
    private void mockNameQuantity(String name, int quantity) {
        mockStatic(InputUtils.class);
        expect(InputUtils.stringInput(anyString())).andReturn(name);
        expect(InputUtils.intInput(anyString())).andReturn(quantity);
        expect(InputUtils.positiveIntInput(anyString())).andReturn(quantity);
        replay(InputUtils.class);
    }
    
    
    private void mockName(String name) {
        mockStatic(InputUtils.class);
        expect(InputUtils.stringInput(anyString())).andReturn(name);
        replay(InputUtils.class);
    }
    
    
    private void checkDatabase(String[][] expected) {
        
        DBContents dbContents = null;
        
        try (Connection con = DriverManager.getConnection(testDatabaseURL);
             Statement statement = con.createStatement()) {
            
            String sql = "SELECT * FROM inventory ORDER BY name";
            
            ResultSet rs = statement.executeQuery(sql);
            
            dbContents = rsToObject(rs);
            
        } catch (SQLException e) {
            fail("SQLException checking data from test database." + e.getMessage());
        } catch (Exception e) {
            fail("Exception checking data from test database." + e.getMessage());
        }
        
        assertNotNull(dbContents);
        
        assertEquals("Database contains a different number of rows of data than expected", expected.length, dbContents.rows());
        
        for (int rowCounter = 0; rowCounter < dbContents.rows(); rowCounter++) {
            
            String name = dbContents.getName(rowCounter);
            int quantity = dbContents.getQuantity(rowCounter);
            
            assertEquals("Name column data does not match actual data", expected[rowCounter][0], name);
            assertEquals("Quantity column data does not match actual data", Integer.parseInt(expected[rowCounter][1]), quantity);
            
        }
    }
    
    
    
    private DBContents rsToObject(ResultSet rs) throws Exception {
        
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        
        while (rs.next()) {
            names.add(rs.getString(1));
            quantities.add(rs.getInt(2));
        }
        
        rs.close();
        
        return new DBContents(names, quantities);
    }
    
    
    class DBContents {
        
        ArrayList<String> names;
        ArrayList<Integer> quantities;
        
        
        public DBContents(ArrayList<String> names, ArrayList<Integer> quantities) throws Exception {
            if (names.size() != quantities.size()) {
                throw new Exception("Should be same number of names and quantities in the actual database to the expected data.");
            }
            this.names = names;
            this.quantities = quantities;
        }
        
        int rows() {
            return names.size();
        }
        
        String getName(int row) {
            return names.get(row);
        }
        
        int getQuantity(int row){
            return quantities.get(row);
        }
    }
    
    
}