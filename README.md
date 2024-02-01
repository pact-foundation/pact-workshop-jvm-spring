# Example Spring Boot project for the Pact workshop

This workshop should take about 2 hours, depending on how deep you want to go into each topic.

This workshop is setup with a number of steps that can be run through. Each step is in a branch, so to run through a
step of the workshop just check out the branch for that step (i.e. `git checkout step1`).

## Requirements

* JDK 17+
* Docker for step 11

## Workshop outline:

* [step 1: **Simple Consumer calling Provider**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step1#step-1---simple-consumer-calling-provider)
* [step 2: **Client Tested but integration fails**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step2#step-2---client-tested-but-integration-fails)
* [step 3: **Pact to the rescue**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step3#step-3---pact-to-the-rescue)
* [step 4: **Verify the provider**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step4#step-4---verify-the-provider)
* [step 5: **Back to the client we go**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step5#step-5---back-to-the-client-we-go)
* [step 6: **Consumer updates contract for missing products**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step6#step-6---consumer-updates-contract-for-missing-products)
* [step 7: **Adding the missing states**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step7#step-7---adding-the-missing-states)
* [step 8: **Authorization**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step8#step-8---authorization)
* [step 9: **Implement authorisation on the provider**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step9#step-9---implement-authorisation-on-the-provider)
* [step 10: **Request Filters on the Provider**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step10#step-10---request-filters-on-the-provider)
* [step 11: **Using a Pact Broker**](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step11#step-11---using-a-pact-broker)

_NOTE: Each step is tied to, and must be run within, a git branch, allowing you to progress through each stage incrementally. For example, to move to step 2 run the following: git checkout step2_

## Scenario

There are two components in scope for our workshop.

1. Product Catalog application (Consumer). It provides a console interface to query the Product service for product information.
1. Product Service (Provider). Provides useful things about products, such as listing all products and getting the details of an individual product.

## Step 1 - Simple Consumer calling Provider

We need to first create an HTTP client to make the calls to our provider service:

![Simple Consumer](diagrams/workshop_step1.svg)

The Consumer has implemented the product service client which has the following:

- `GET /products` - Retrieve all products
- `GET /products/{id}` - Retrieve a single product by ID

The diagram below highlights the interaction for retrieving a product with ID 10:

![Sequence Diagram](diagrams/workshop_step1_class-sequence-diagram.svg)

You can see the client interface we created in `consumer/src/main/au/com/dius/pactworkshop/consumer/ProductService.java`:

```java
@Service
public class ProductService {

    private final RestTemplate restTemplate;

    @Autowired
    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Product> getAllProducts() {
        return restTemplate.exchange("/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>(){}).getBody();
    }

    public Product getProduct(String id) {
        return restTemplate.getForEntity("/products/{id}", Product.class, id).getBody();
    }
}
```

We can run the client with `./gradlew consumer:bootRun` - it should fail with the error below, because the Provider is not running.

```console
Caused by: org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8085/products": Connection refused: connect; nested exception is java.net.ConnectException: Connection refused: connect
```

Move on to [step 2](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step2#step-2---client-tested-but-integration-fails)

## Step 2 - Client Tested but integration fails

Now let's create a basic test for our API client. We're going to check 2 things:

1. That our client code hits the expected endpoint
1. That the response is marshalled into an object that is usable, with the correct ID

You can see the client interface test we created in `consumer/src/test/java/au/com/dius/pactworkshop/consumer/ProductServiceTest.java`:

```java
class ProductServiceTest {

  private WireMockServer wireMockServer;
  private ProductService productService;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(options().dynamicPort());

    wireMockServer.start();

    RestTemplate restTemplate = new RestTemplateBuilder()
      .rootUri(wireMockServer.baseUrl())
      .build();

    productService = new ProductService(restTemplate);
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void getAllProducts() {
    wireMockServer.stubFor(get(urlPathEqualTo("/products"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("[" +
          "{\"id\":\"9\",\"type\":\"CREDIT_CARD\",\"name\":\"GEM Visa\",\"version\":\"v2\"},"+
          "{\"id\":\"10\",\"type\":\"CREDIT_CARD\",\"name\":\"28 Degrees\",\"version\":\"v1\"}"+
          "]")));

    List<Product> expected = Arrays.asList(new Product("9", "CREDIT_CARD", "GEM Visa", "v2"),
      new Product("10", "CREDIT_CARD", "28 Degrees", "v1"));

    List<Product> products = productService.getAllProducts();

    assertEquals(expected, products);
  }

  @Test
  void getProductById() {
    wireMockServer.stubFor(get(urlPathEqualTo("/products/50"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{\"id\":\"50\",\"type\":\"CREDIT_CARD\",\"name\":\"28 Degrees\",\"version\":\"v1\"}")));

    Product expected = new Product("50", "CREDIT_CARD", "28 Degrees", "v1");

    Product product = productService.getProduct("50");

    assertEquals(expected, product);
  }
}
```



![Unit Test With Mocked Response](diagrams/workshop_step2_unit_test.svg)



Let's run this test and see it all pass:

```console
> ./gradlew consumer:test

BUILD SUCCESSFUL in 2s
```

Meanwhile, our provider team has started building out their API in parallel. Let's run our website against our provider (you'll need two terminals to do this):


```console
# Terminal 1
❯ ./gradlew provider:bootRun

...
...
Tomcat started on port(s): 8085 (http) with context path ''
Started ProviderApplication in 1.67 seconds (JVM running for 2.039)
```

```console
# Terminal 2
> ./gradlew consumer:bootRun --console plain

...
...
Started ConsumerApplication in 1.106 seconds (JVM running for 1.62)


Products
--------
1) Gem Visa
2) MyFlexiPay
3) 28 Degrees
Select item to view details: 
```

You should now see 3 different products. Choosing an index number should display detailed product information.

Let's see what happens!

![Failed page](diagrams/workshop_step2_failed_page.png)

Doh! We are getting 404 every time we try to view detailed product information. On closer inspection, the provider only knows about `/product/{id}` and `/products`.

We need to have a conversation about what the endpoint should be, but first...

Move on to [step 3](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step3#step-3---pact-to-the-rescue)

## Step 3 - Pact to the rescue

Unit tests are written and executed in isolation of any other services. When we write tests for code that talk to other services, they are built on trust that the contracts are upheld. There is no way to validate that the consumer and provider can communicate correctly.

> An integration contract test is a test at the boundary of an external service verifying that it meets the contract expected by a consuming service — [Martin Fowler](https://martinfowler.com/bliki/IntegrationContractTest.html)

Adding contract tests via Pact would have highlighted the `/product/{id}` endpoint was incorrect.

Let us add Pact to the project and write a consumer pact test for the `GET /products/{id}` endpoint.

*Provider states* is an important concept of Pact that we need to introduce. These states help define the state that the provider should be in for specific interactions. For the moment, we will initially be testing the following states:

- `product with ID 10 exists`
- `products exist`

The consumer can define the state of an interaction using the `given` property.

Note how similar it looks to our unit test:

In `consumer/src/test/java/au/com/dius/pactworkshop/consumer/ProductConsumerPactTest.java`:

```java
@ExtendWith(PactConsumerTestExt.class)
public class ProductConsumerPactTest {
  
      @Pact(consumer = "FrontendApplication", provider = "ProductService")
      RequestResponsePact getAllProducts(PactDslWithProvider builder) {
        return builder.given("products exist")
          .uponReceiving("get all products")
          .method("GET")
          .path("/products")
          .willRespondWith()
          .status(200)
          .headers(headers())
          .body(newJsonArrayMinLike(2, array ->
            array.object(object -> {
              object.stringType("id", "09");
              object.stringType("type", "CREDIT_CARD");
              object.stringType("name", "Gem Visa");
            })
          ).build())
          .toPact();
      }
    
      @Pact(consumer = "FrontendApplication", provider = "ProductService")
      RequestResponsePact getOneProduct(PactDslWithProvider builder) {
        return builder.given("product with ID 10 exists")
          .uponReceiving("get product with ID 10")
          .method("GET")
          .path("/products/10")
          .willRespondWith()
          .status(200)
          .headers(headers())
          .body(newJsonBody(object -> {
            object.stringType("id", "10");
            object.stringType("type", "CREDIT_CARD");
            object.stringType("name", "28 Degrees");
          }).build())
          .toPact();
      }
    
      @Test
      @PactTestFor(pactMethod = "getAllProducts")
      void getAllProducts_whenProductsExist(MockServer mockServer) {
        Product product = new Product();
        product.setId("09");
        product.setType("CREDIT_CARD");
        product.setName("Gem Visa");
        List<Product> expected = Arrays.asList(product, product);
    
        RestTemplate restTemplate = new RestTemplateBuilder()
          .rootUri(mockServer.getUrl())
          .build();
        List<Product> products = new ProductService(restTemplate).getAllProducts();
    
        assertEquals(expected, products);
      }
    
      @Test
      @PactTestFor(pactMethod = "getOneProduct")
      void getProductById_whenProductWithId10Exists(MockServer mockServer) {
        Product expected = new Product();
        expected.setId("10");
        expected.setType("CREDIT_CARD");
        expected.setName("28 Degrees");
    
        RestTemplate restTemplate = new RestTemplateBuilder()
          .rootUri(mockServer.getUrl())
          .build();
        Product product = new ProductService(restTemplate).getProduct("10");
    
        assertEquals(expected, product);
      }
    
      private Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
      }
}
```


![Test using Pact](diagrams/workshop_step3_pact.svg)

This test starts a mock server on a random port that acts as our provider service. To get this to work we update the URL in the `Client` that we create, after initialising Pact.

To run only the Pact tests:

```console
> ./gradlew consumer:test --tests '*PactTest'

```

Running this test still passes, but it creates a pact file which we can use to validate our assumptions on the provider side, and have conversation around.

```console
❯ ./gradlew consumer:test --tests '*PactTest'
  
  BUILD SUCCESSFUL in 6s
```

A pact file should have been generated in *consumer/build/pacts/FrontendApplication-ProductService.json*

*NOTE*: even if the API client had been graciously provided for us by our Provider Team, it doesn't mean that we shouldn't write contract tests - because the version of the client we have may not always be in sync with the deployed API - and also because we will write tests on the output appropriate to our specific needs.

Move on to [step 4](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step4#step-4---verify-the-provider)

## Step 4 - Verify the provider

We will need to copy the Pact contract file that was produced from the consumer test into the Provider module. This will help us verify that the provider can meet the requirements as set out in the contract.

Copy the contract located in `consumer/build/pacts/FrontendApplication-Productservice.json` to `provider/src/test/resources/pacts/FrontendApplication-Productservice.json`. Or run the Gradle task
```console
> ./gradlew consumer:copyPacts

BUILD SUCCESSFUL in 1s
```

Now let's make a start on writing Pact tests to validate the consumer contract:

In `provider/src/test/java/au/com/dius/pactworkshop/provider/ProductPactProviderTest.java`:

```java
@Provider("ProductService")
@PactFolder("pacts")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductPactProviderTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("products exist")
    void toProductsExistState() {

    }

    @State("product with ID 10 exists")
    void toProductWithIdTenExistsState() {

    }
}
```

To run only the verification tests:

```console
> ./gradlew provider:test --tests '*Pact*Test'
```

We now need to validate the pact generated by the consumer is valid, by executing it against the running service provider, which should fail:

```console
❯ ./gradlew provider:test --tests '*Pact*Test'

...
...
au.com.dius.pactworkshop.provider.ProductPactProviderTest > FrontendApplication - get product with ID 10 FAILED
    java.lang.AssertionError at ProductPactProviderTest.java:33
2020-10-09 06:21:52.555  INFO 6404 --- [extShutdownHook] o.s.s.concurrent.Thread
2 tests completed, 1 failed

> Task :provider:test FAILED
```

![Pact Verification](diagrams/workshop_step4_pact.svg)

The test has failed, as the expected path `/products/{id}` is returning 404. We incorrectly believed our provider was following a RESTful design, but the authors were too lazy to implement a better routing solution 🤷🏻‍♂️.

The correct endpoint which the consumer should call is `/product/{id}`.

Move on to [step 5](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step5#step-5---back-to-the-client-we-go)

## Step 5 - Back to the client we go

We now need to update the consumer client and tests to hit the correct product path.

First, we need to update the GET route for the client:

In `consumer/src/main/au/com/dius/pactworkshop/consumer/ProductService.java`:

```java
...

public Product getProduct(String id) {
    return restTemplate.getForEntity("/product/{id}", Product.class, id).getBody();
}
```

Then we need to update the Pact test `ID 10 exists` to use the correct endpoint in `path`.

In `consumer/src/test/java/au/com/dius/pactworkshop/consumer/ProductConsumerPactTest.java`:

```java
@Pact(consumer = "FrontendApplication", provider = "ProductService")
RequestResponsePact getOneProduct(PactDslWithProvider builder) {
    return builder.given("product with ID 10 exists")
            .uponReceiving("get product with ID 10")
            .method("GET")
            .path("/product/10")
            .willRespondWith()
            .status(200)
            .headers(headers())
            .body(newJsonBody(object -> {
                object.stringType("id", "10");
                object.stringType("type", "CREDIT_CARD");
                object.stringType("name", "28 Degrees");
            }).build())
            .toPact();
}
...
```

![Pact Verification](diagrams/workshop_step5_pact.svg)

Let's run and generate an updated pact file on the client:

```console
❯ ./gradlew consumer:test --tests *PactTest
  
  BUILD SUCCESSFUL in 7s
```



Now we run the provider tests again with the updated contract

Copy the updated contract located in `consumer/build/pacts/FrontendApplication-ProductService.json` to `provider/src/test/resources/pacts/FrontendApplication-Productservice.json` by running the command:
```console
> ./gradlew consumer:copyPacts
  
  BUILD SUCCESSFUL in 1s
```

Run the command:

```console
❯ ./gradlew provider:test --tests '*Pact*Test'

...
...

BUILD SUCCESSFUL in 10s
```

Yay - green ✅!

Move on to [step 6](https://github.com/pact-foundation/pact-workshop-jvm-spring/tree/step6#step-6---consumer-updates-contract-for-missing-products)
