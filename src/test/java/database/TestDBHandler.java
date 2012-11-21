package database;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests methods of the {@link DBHandler}.
 * 
 * @author ak83
 */
public class TestDBHandler {

    /**
     * Tests if method
     * {@link DBHandler#converHashMaoToSQLArray(java.util.HashMap)} work
     * correctly.
     */
    @Test
    public void testConvertingHashMapToSQLArray() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < 10; i++) {
            map.put(i + 1, i);
        }

        String result = null;
        try {
            Method method = DBHandler.class.getDeclaredMethod("converHashMaoToSQLArray", map.getClass());
            method.setAccessible(true);

            result = (String) method.invoke(null, map);
        }
        catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            Assert.fail();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assertEquals("{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}", result);
    }

}
