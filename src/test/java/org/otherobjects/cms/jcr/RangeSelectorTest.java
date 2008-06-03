package org.otherobjects.cms.jcr;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class RangeSelectorTest extends TestCase
{
    private List<Integer> items = null;
    private List<Integer> twoItems = null;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        items = new ArrayList<Integer>();
        for (int i = 1; i <= 50; i++)
            items.add(i);
        
        twoItems = new ArrayList<Integer>();
        twoItems.add(1);
        twoItems.add(2);
    }

    public void testRangeSelector()
    {
        RangeSelector<Integer> r1 = new RangeSelector<Integer>("{4}", items);
        assertEquals(false, r1.isRandom());
        assertEquals(1, r1.getStart());
        assertEquals(4, r1.getEnd());
        List<Integer> r1r = r1.getSelected();
        assertEquals(4, r1r.size());
        assertEquals(new Integer(1), r1r.get(0));
        assertEquals(new Integer(4), r1r.get(3));

        RangeSelector<Integer> r2 = new RangeSelector<Integer>("{3..7}", items);
        assertEquals(false, r2.isRandom());
        assertEquals(3, r2.getStart());
        assertEquals(7, r2.getEnd());
        List<Integer> r2r = r2.getSelected();
        assertEquals(5, r2r.size());
        assertEquals(new Integer(3), r2r.get(0));
        assertEquals(new Integer(7), r2r.get(4));

        RangeSelector<Integer> r3 = new RangeSelector<Integer>("{%5}", items);
        assertEquals(true, r3.isRandom());
        assertEquals(5, r3.getEnd());
        List<Integer> r3r = r3.getSelected();
        List<Integer> r3r2 = r3.getSelected();
        assertEquals(5, r3r.size());
        assertNotSame(r3r,(r3r2));

        RangeSelector<Integer> r4 = new RangeSelector<Integer>("{%2}", twoItems);
        List<Integer> r4r = r4.getSelected();
        assertTrue(r4r.contains(new Integer(1)));
        assertTrue(r4r.contains(new Integer(2)));
        
        // Test bad input
        try
        {
            new RangeSelector<Integer>("{%6..9}", items);
            fail();
        }
        catch (RuntimeException e)
        {
            // Got exception. Pass.
        }
        try
        {
            new RangeSelector<Integer>("{9..2}", items);
            fail();
        }
        catch (RuntimeException e)
        {
            // Got exception. Pass.
        }
        try
        {
            new RangeSelector<Integer>("asd,9", items);
            fail();
        }
        catch (RuntimeException e)
        {
            // Got exception. Pass.
        }
    }

}
