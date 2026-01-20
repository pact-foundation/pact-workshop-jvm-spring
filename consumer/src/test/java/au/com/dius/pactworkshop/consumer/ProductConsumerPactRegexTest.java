package au.com.dius.pactworkshop.consumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductConsumerPactRegexTest {
	@Test
	void testRegex() {
		// Expected boundaries
		assertTrue("Bearer 1900-01-01T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-01-01T10:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-01-01T20:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-01-01T21:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-01-01T22:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-01-01T23:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-01-01T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-09-01T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-10-01T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-11-01T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-01T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-09T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-10T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-19T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-20T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-29T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-30T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1900-12-31T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 1999-12-31T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 2000-01-01T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 2099-01-01T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 2099-01-01T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 2099-12-31T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 2099-12-31T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		// Feb 29 - note validity of leap years not checked
		assertTrue("Bearer 2020-02-29T12:34".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue("Bearer 2021-02-29T12:34".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		// Invalid cases
		assertTrue(!"Bearer 1899-12-31T23:59".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2100-01-01T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2021-13-01T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2021-00-01T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2021-01-32T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2021-01-00T00:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2021-01-01T24:00".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
		assertTrue(!"Bearer 2021-01-01T00:60".matches(ProductConsumerPactTest.AUTH_TOKEN_REGEX));
	}
}
