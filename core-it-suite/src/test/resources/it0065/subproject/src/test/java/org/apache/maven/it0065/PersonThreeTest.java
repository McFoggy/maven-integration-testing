package org.apache.maven.it0065;

import junit.framework.TestCase;

public class PersonThreeTest
    extends TestCase
{
    public void testPerson()
    {
        Person person = new Person();
        
        person.setName( "foo" );
        
        assertEquals( "foo", person.getName() );
    }
}