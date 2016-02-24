package g12.assignment1;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class BFATest extends TestCase {

	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BFATest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BFATest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testBFA()
    {
        assertTrue( true );
    }
}
