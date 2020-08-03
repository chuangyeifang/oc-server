package junit;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.oc.common.utils.PwdEncrypt;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	List<Integer> list = Arrays.asList(132,23,599,1,712);
    	Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 > o2 ? 0 : -1;
			}
    		
		});
    	
    	for (Integer integer : list) {
			System.out.println(integer);
		}
    	
        assertTrue( true );
    }

    public void testCreatePwd() {
        System.out.println(PwdEncrypt.createMD5Password("chuangyeifang", "123456"));
    }
    
}
