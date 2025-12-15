package application.services;

import domain.Food;
import domain.factory.FoodFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zhili
 */
public class FoodServiceTest {

    public FoodServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMenuByType method, of class FoodService.
     */
    @Test
    public void testGetMenuByType_validType() {
        FoodService service = new FoodService();

        List<Food> beverages = service.getMenuByType("Beverage");

        assertNotNull(beverages);
        assertFalse(beverages.isEmpty());
    }

    @Test
    public void testGetMenuByType_invalidType() {
        FoodService service = new FoodService();

        List<Food> result = service.getMenuByType("InvalidType");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMenuByType_returnsDefensiveCopy() {
        FoodService service = new FoodService();

        List<Food> menu1 = service.getMenuByType("Beverage");
        int originalSize = menu1.size();

        menu1.clear(); // modify returned list

        List<Food> menu2 = service.getMenuByType("Beverage");
        assertEquals(originalSize, menu2.size());
    }

    /**
     * Test of addOrMergeOrder method, of class FoodService.
     */
    @Test
    public void testAddOrMergeOrder_addNewItem() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        Food cola = FoodFactory.createMenuItem("Beverage", "Cola", 5.0);

        service.addOrMergeOrder(orders, cola, 5.0);

        assertEquals(1, orders.size());
        assertEquals("Cola", orders.get(0).getName());
        assertEquals(1, orders.get(0).getQty());
        assertEquals(5.0, orders.get(0).getPrice(), 0.001);
    }

    @Test
    public void testAddOrMergeOrder_mergeDuplicateItem() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        Food cola1 = FoodFactory.createMenuItem("Beverage", "Cola", 5.0);

        Food cola2 = FoodFactory.createMenuItem("Beverage", "Cola", 5.0);
        cola2.incrementQty(1);
        cola2.incrementPrice(5.0);

        orders.add(cola1);
        service.addOrMergeOrder(orders, cola2, 5.0);

        assertEquals(1, orders.size());
        assertEquals(3, orders.get(0).getQty());
        assertEquals(15.0, orders.get(0).getPrice(), 0.001);
    }

    @Test
    public void testAddOrMergeOrder_nullNewItem() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        ArrayList<Food> result = service.addOrMergeOrder(orders, null, 0.0);

        assertSame(orders, result);
        assertTrue(orders.isEmpty());
    }

    /**
     * Test of calculateTotal method, of class FoodService.
     */
    @Test
    public void testCalculateTotal_emptyOrders() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        double total = service.calculateTotal(orders);

        assertEquals(0.0, total, 0.001);
    }

    @Test
    public void testCalculateTotal_multipleItems() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        Food item1 = FoodFactory.createMenuItem("Beverage", "Cola", 5.0);

        Food item2 = FoodFactory.createMenuItem("Popcorn", "Caramel", 7.0);
        item2.incrementQty(2);
        item2.incrementPrice(7.0);

        orders.add(item1);
        orders.add(item2);

        double total = service.calculateTotal(orders);
        assertEquals(19.0, total, 0.001);
    }

    /**
     * Test of removeOrderItem method, of class FoodService.
     */
    @Test
    public void testRemoveOrderItem_validIndex() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        Food item = FoodFactory.createMenuItem("Beverage", "Cola", 5.0);
        item.incrementQty(1);
        item.incrementPrice(5.0);

        orders.add(item);

        boolean result = service.removeOrderItem(orders, 0);

        assertTrue(result);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testRemoveOrderItem_invalidIndex() {
        FoodService service = new FoodService();
        ArrayList<Food> orders = new ArrayList<>();

        boolean result = service.removeOrderItem(orders, 5);

        assertFalse(result);
    }

}