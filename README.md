# Example Spring Boot project for the Pact workshop

This workshop should take about 2 hours, depending on how deep you want to go into each topic.

This workshop is setup with a number of steps that can be run through. Each step is in a branch, so to run through a
step of the workshop just check out the branch for that step (i.e. `git checkout step1`).

## Requirements

* JDK 8+
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
