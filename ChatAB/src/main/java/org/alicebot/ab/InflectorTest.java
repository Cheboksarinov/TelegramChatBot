package org.alicebot.ab;

import junit.framework.TestCase;
import org.junit.Test;
public class InflectorTest extends TestCase {
    @Test
    public void testPluralize() throws Exception {
        Inflector inflector = new Inflector();
        String pairs[][] = {{"dog","dogs"},{"person","people"},{"cats","cats"}};
        for (int i = 0; i < pairs.length; i++) {
        String singular = pairs[i][0];
        String expected = pairs[i][1];
        String actual = inflector.pluralize(singular);
        assertEquals("Pluralize "+pairs[0][0],expected, actual);
        }


    }

    @Test
    public void testSingularize() throws Exception {

    }
}
