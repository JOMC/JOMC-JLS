/*
 *   Copyright (C) 2012 Christian Schulte <cs@schulte.it>
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions
 *   are met:
 *
 *     o Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     o Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
 *
 */
package org.jomc.jls.test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;
import org.jomc.jls.JavaIdentifier;
import org.junit.Test;
import static org.jomc.jls.JavaIdentifier.NormalizationMode.CAMEL_CASE;
import static org.jomc.jls.JavaIdentifier.NormalizationMode.CONSTANT_NAME_CONVENTION;
import static org.jomc.jls.JavaIdentifier.NormalizationMode.LOWER_CASE;
import static org.jomc.jls.JavaIdentifier.NormalizationMode.METHOD_NAME_CONVENTION;
import static org.jomc.jls.JavaIdentifier.NormalizationMode.UPPER_CASE;
import static org.jomc.jls.JavaIdentifier.NormalizationMode.VARIABLE_NAME_CONVENTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.jls.JavaIdentifier}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JavaIdentifierTest
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/jls/test/";

    /**
     * Creates a new {@code JavaIdentifierTest} instance.
     */
    public JavaIdentifierTest()
    {
        super();
    }

    @Test
    public final void NormalizeThrowsNullPointerExceptionOnNullArgument() throws Exception
    {
        assertNullPointerException( ()  -> JavaIdentifier.normalize( null, CAMEL_CASE ) );
        assertNullPointerException( ()  -> JavaIdentifier.normalize( "", null ) );
    }

    @Test
    public final void ParseAndValueOfCorrectlyDetectInvalidIdentifiers() throws Exception
    {
        assertInvalidJavaIdentifier( "" );
        assertInvalidJavaIdentifier( "@" );
        assertInvalidJavaIdentifier( "   " );
        JavaLanguage.forEachKeyword( ( keyword )  -> assertInvalidJavaIdentifier( keyword ) );
        JavaLanguage.forEachLiteral( ( literal )  -> assertInvalidJavaIdentifier( literal ) );
    }

    @Test
    public final void ParseThrowsNullPointerExceptionOnNullArgument() throws Exception
    {
        assertNullPointerException( ()  -> JavaIdentifier.parse( null ) );
    }

    @Test
    public final void ValueOfThrowsNullPointerExceptionOnNullArgument() throws Exception
    {
        assertNullPointerException( ()  -> JavaIdentifier.valueOf( null ) );
    }

    @Test
    public final void CamelCaseNormalization() throws Exception
    {
        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "test test test  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "test_test_test", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( " test_test_test ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "  test test test  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "  test  test  test  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "  Test test test  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "  Test  test  test  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "Test  test  test  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "tEST  tEST  tEST  ", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "Test" ),
                      JavaIdentifier.normalize( "TEST", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "TestTestTest", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "testTestTest", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TestTestTest" ),
                      JavaIdentifier.normalize( "test TeSt Test", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TesTtEsTtEst" ),
                      JavaIdentifier.normalize( "tEsTtEsTtEsT", CAMEL_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TesttesttestTesttesttest" ),
                      JavaIdentifier.normalize( "tEsTtEsTtEsT tEsTtEsTtEsT", CAMEL_CASE ) );

        JavaLanguage.forEachKeyword( ( keyword )  ->
        {
            assertEquals( JavaIdentifier.valueOf( toUpperCase( keyword, 0, 1 ) ),
                          JavaIdentifier.normalize( "   " + keyword + "   ", CAMEL_CASE ) );

        } );

        JavaLanguage.forEachLiteral( ( literal )  ->
        {
            assertEquals( JavaIdentifier.valueOf( toUpperCase( literal, 0, 1 ) ),
                          JavaIdentifier.normalize( "   " + literal + "   ", CAMEL_CASE ) );

        } );

        assertInvalidJavaIdentifier( "", CAMEL_CASE );
        assertInvalidJavaIdentifier( "@", CAMEL_CASE );
        assertInvalidJavaIdentifier( "   ", CAMEL_CASE );
    }

    @Test
    public final void UpperCaseNormalization() throws Exception
    {
        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "test test test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  test test test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  test  test  test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  Test test test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  Test  test  test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "test_test_test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( " test_test_test  ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "_test _test _test_", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( " _test _test _test_ ", UPPER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "TEST_TEST_TEST", UPPER_CASE ) );

        JavaLanguage.forEachKeyword( ( keyword )  ->
        {
            assertEquals( JavaIdentifier.valueOf( toUpperCase( keyword ) ),
                          JavaIdentifier.normalize( "   " + keyword + "   ", UPPER_CASE ) );

        } );

        JavaLanguage.forEachLiteral( ( literal )  ->
        {
            assertEquals( JavaIdentifier.valueOf( toUpperCase( literal ) ),
                          JavaIdentifier.normalize( "   " + literal + "   ", UPPER_CASE ) );

        } );

        assertInvalidJavaIdentifier( "", UPPER_CASE );
        assertInvalidJavaIdentifier( "@", UPPER_CASE );
        assertInvalidJavaIdentifier( "   ", UPPER_CASE );
    }

    @Test
    public final void LowerCaseNormalization() throws Exception
    {
        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "test test test  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "  test test test  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "  test  test  test  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "  Test test test  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "  Test  test  test  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "TEST_TEST_TEST  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( " TEST_TEST_TEST  ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "_TEST _TEST _TEST_", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( " _TEST _TEST _TEST_ ", LOWER_CASE ) );

        assertEquals( JavaIdentifier.valueOf( "test_test_test" ),
                      JavaIdentifier.normalize( "test_test_test", LOWER_CASE ) );

        JavaLanguage.forEachKeyword( ( keyword )  ->
        {
            assertEquals( JavaIdentifier.valueOf( "_" + toLowerCase( keyword ) ),
                          JavaIdentifier.normalize( "   " + keyword + "   ", LOWER_CASE ) );
        } );

        JavaLanguage.forEachLiteral( ( literal )  ->
        {
            assertEquals( JavaIdentifier.valueOf( "_" + toLowerCase( literal ) ),
                          JavaIdentifier.normalize( "   " + literal + "   ", LOWER_CASE ) );

        } );

        assertInvalidJavaIdentifier( "", LOWER_CASE );
        assertInvalidJavaIdentifier( "@", LOWER_CASE );
        assertInvalidJavaIdentifier( "   ", LOWER_CASE );
    }

    @Test
    public final void ConstantNameNormalization() throws Exception
    {
        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "test test test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  test test test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  test  test  test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  Test test test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "  Test  test  test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "test_test_test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( " test_test_test  ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "_test _test _test_", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( " _test _test _test_ ", CONSTANT_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      JavaIdentifier.normalize( "TEST_TEST_TEST", CONSTANT_NAME_CONVENTION ) );

        JavaLanguage.forEachKeyword( ( keyword )  ->
        {
            assertEquals( JavaIdentifier.valueOf( toUpperCase( keyword ) ),
                          JavaIdentifier.normalize( "   " + keyword + "   ", CONSTANT_NAME_CONVENTION ) );

        } );

        JavaLanguage.forEachLiteral( ( literal )  ->
        {
            assertEquals( JavaIdentifier.valueOf( toUpperCase( literal ) ),
                          JavaIdentifier.normalize( "   " + literal + "   ", CONSTANT_NAME_CONVENTION ) );

        } );

        assertInvalidJavaIdentifier( "", CONSTANT_NAME_CONVENTION );
        assertInvalidJavaIdentifier( "@", CONSTANT_NAME_CONVENTION );
        assertInvalidJavaIdentifier( "   ", CONSTANT_NAME_CONVENTION );
    }

    @Test
    public final void MethodNameNormalization() throws Exception
    {
        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "test test test  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "test_test_test", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( " test_test_test ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  test test test  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  test  test  test  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  Test test test  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  Test  test  test  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "Test  test  test  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "tEST  tEST  tEST  ", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "test" ),
                      JavaIdentifier.normalize( "TEST", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "TestTestTest", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "testTestTest", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "Test TeSt Test", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "teStTeStTeStx" ),
                      JavaIdentifier.normalize( "TeStTeStTeStX", METHOD_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testtesttestTesttesttest" ),
                      JavaIdentifier.normalize( "TeStTeStTeSt TeStTeStTeSt", METHOD_NAME_CONVENTION ) );

        JavaLanguage.forEachKeyword( ( keyword )  ->
        {
            assertEquals( JavaIdentifier.valueOf( "_" + keyword ),
                          JavaIdentifier.normalize( "   " + keyword + "   ", METHOD_NAME_CONVENTION ) );

        } );

        JavaLanguage.forEachLiteral( ( literal )  ->
        {
            assertEquals( JavaIdentifier.valueOf( "_" + literal ),
                          JavaIdentifier.normalize( "   " + literal + "   ", METHOD_NAME_CONVENTION ) );

        } );

        assertInvalidJavaIdentifier( "", METHOD_NAME_CONVENTION );
        assertInvalidJavaIdentifier( "@", METHOD_NAME_CONVENTION );
        assertInvalidJavaIdentifier( "   ", METHOD_NAME_CONVENTION );
    }

    @Test
    public final void VariableNameNormalization() throws Exception
    {
        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "test test test  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "test_test_test", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( " test_test_test ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  test test test  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  test  test  test  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  Test test test  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "  Test  test  test  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "Test  test  test  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "tEST  tEST  tEST  ", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "test" ),
                      JavaIdentifier.normalize( "TEST", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "TestTestTest", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "testTestTest", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      JavaIdentifier.normalize( "test TeSt Test", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "teStTeStTeStx" ),
                      JavaIdentifier.normalize( "TeStTeStTeStX", VARIABLE_NAME_CONVENTION ) );

        assertEquals( JavaIdentifier.valueOf( "testtesttestTesttesttest" ),
                      JavaIdentifier.normalize( "TeStTeStTeSt TeStTeStTeSt", VARIABLE_NAME_CONVENTION ) );

        JavaLanguage.forEachKeyword( ( keyword )  ->
        {
            assertEquals( JavaIdentifier.valueOf( "_" + keyword ),
                          JavaIdentifier.normalize( "   " + keyword + "   ", VARIABLE_NAME_CONVENTION ) );

        } );

        JavaLanguage.forEachLiteral( ( literal )  ->
        {
            assertEquals( JavaIdentifier.valueOf( "_" + literal ),
                          JavaIdentifier.normalize( "   " + literal + "   ", VARIABLE_NAME_CONVENTION ) );

        } );

        assertInvalidJavaIdentifier( "", VARIABLE_NAME_CONVENTION );
        assertInvalidJavaIdentifier( "@", VARIABLE_NAME_CONVENTION );
        assertInvalidJavaIdentifier( "   ", VARIABLE_NAME_CONVENTION );
    }

    @Test
    public final void Serializable() throws Exception
    {
        try ( final ObjectOutputStream out = new ObjectOutputStream( new ByteArrayOutputStream() ) )
        {
            out.writeObject( JavaIdentifier.valueOf( "Java" ) );
        }
    }

    @Test
    public final void Deserializable() throws Exception
    {
        try ( final ObjectInputStream in = new ObjectInputStream( this.getClass().getResourceAsStream(
            ABSOLUTE_RESOURCE_NAME_PREFIX + "JavaIdentifier.ser" ) ) )
        {
            final JavaIdentifier javaIdentifier = (JavaIdentifier) in.readObject();
            assertEquals( "Java", javaIdentifier.toString() );
            System.out.println( javaIdentifier );
        }
    }

    private static void assertInvalidJavaIdentifier( final String identifier )
    {
        try
        {
            JavaIdentifier.parse( identifier );
            fail( "Expected 'ParseException' not thrown parsing Java identifier '" + identifier + "'." );
        }
        catch ( final ParseException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.getMessage() );
        }

        try
        {
            JavaIdentifier.valueOf( identifier );
            fail( "Expected 'IllegalArgumentException' not thrown parsing Java identifier '" + identifier + "'." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.getMessage() );
        }
    }

    private <T> void assertNullPointerException( final Callable<T> callable ) throws Exception
    {
        try
        {
            callable.call();
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    private static void assertInvalidJavaIdentifier( final String identifier,
                                                     final JavaIdentifier.NormalizationMode mode )
    {
        try
        {
            JavaIdentifier.normalize( identifier, mode );
            fail( "Expected 'ParseException' not thrown normalizing Java identifier '" + identifier + "'." );
        }
        catch ( final ParseException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.getMessage() );
        }
    }

    private static String toUpperCase( final String string )
    {
        final char[] c = string.toCharArray();

        try ( final IntStream stream = IntStream.range( 0, c.length ).parallel().unordered() )
        {
            stream.forEach( ( i )  -> c[i] = Character.toUpperCase( c[i] ) );
        }

        return String.valueOf( c );
    }

    private static String toUpperCase( final String string, final int offset, final int count )
    {
        final int limit = offset + count;

        Objects.requireNonNull( string, "string" );

        if ( offset < 0 || offset >= string.length() )
        {
            throw new StringIndexOutOfBoundsException();
        }
        if ( count < 0 || limit >= string.length() )
        {
            throw new StringIndexOutOfBoundsException();
        }

        final char[] c = string.toCharArray();

        try ( final IntStream stream = IntStream.range( offset, limit ).parallel().unordered() )
        {
            stream.forEach( ( i )  ->
            {
                c[i] = Character.toUpperCase( c[i] );
            } );
        }

        return String.valueOf( c );
    }

    private static String toLowerCase( final String string )
    {
        final char[] c = string.toCharArray();

        try ( final IntStream stream = IntStream.range( 0, c.length ).parallel().unordered() )
        {
            stream.forEach( i  -> c[i] = Character.toLowerCase( c[i] ) );
        }

        return String.valueOf( c );
    }

}
