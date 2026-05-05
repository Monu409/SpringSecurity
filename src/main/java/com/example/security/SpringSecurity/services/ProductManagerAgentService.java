package com.example.security.SpringSecurity.services;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.helpers.BetaToolRunner;
import com.anthropic.models.beta.messages.BetaMessage;
import com.anthropic.models.beta.messages.MessageCreateParams;
import com.example.security.SpringSecurity.models.ProductModel;
import com.example.security.SpringSecurity.repos.ProductRepo;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ProductManagerAgentService {

    private static ProductRepo productRepo;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final AnthropicClient client;

    @Autowired
    public ProductManagerAgentService(
            ProductRepo productRepo,
            @Value("${anthropic.api-key}") String apiKey) {
        ProductManagerAgentService.productRepo = productRepo;
        this.client = AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    private static final String SYSTEM_PROMPT = """
            You are a Product Manager agent responsible for managing our product catalog.
            You have access to tools to create, read, update, delete, and search products.

            Guidelines:
            - When listing products, present them in a clear, readable format.
            - Always confirm successful operations with relevant details.
            - For ambiguous requests, ask clarifying questions before taking action.
            - Format prices as currency (e.g., $29.99).
            - If a product is not found, clearly state so and suggest alternatives if relevant.
            """;

    public String chat(String userMessage) {
        MessageCreateParams params = MessageCreateParams.builder()
                .model("claude-opus-4-6")
                .maxTokens(4096L)
                .system(SYSTEM_PROMPT)
                .addTool(ListProducts.class)
                .addTool(GetProduct.class)
                .addTool(CreateProduct.class)
                .addTool(UpdateProduct.class)
                .addTool(DeleteProduct.class)
                .addTool(SearchProducts.class)
                .addUserMessage(userMessage)
                .build();

        BetaToolRunner toolRunner = client.beta().messages().toolRunner(params);

        BetaMessage finalMessage = null;
        for (BetaMessage message : toolRunner) {
            finalMessage = message;
        }

        if (finalMessage == null) {
            return "No response generated.";
        }

        StringBuilder response = new StringBuilder();
        finalMessage.content().forEach(block ->
                block.text().ifPresent(t -> response.append(t.text()))
        );

        return response.isEmpty() ? "No response generated." : response.toString();
    }

    // ─── Tool: List all products ──────────────────────────────────────────────

    @JsonClassDescription("List all products in the catalog")
    public static class ListProducts implements Supplier<String> {
        @Override
        public String get() {
            try {
                List<ProductModel> products = productRepo.findAll();
                if (products.isEmpty()) {
                    return "No products found in the catalog.";
                }
                return objectMapper.writeValueAsString(products);
            } catch (Exception e) {
                return "Error listing products: " + e.getMessage();
            }
        }
    }

    // ─── Tool: Get a product by ID ────────────────────────────────────────────

    @JsonClassDescription("Get a single product by its ID")
    public static class GetProduct implements Supplier<String> {
        @JsonPropertyDescription("The unique ID of the product to retrieve")
        public String id;

        @Override
        public String get() {
            try {
                Optional<ProductModel> product = productRepo.findById(id);
                if (product.isEmpty()) {
                    return "Product not found with id: " + id;
                }
                return objectMapper.writeValueAsString(product.get());
            } catch (Exception e) {
                return "Error getting product: " + e.getMessage();
            }
        }
    }

    // ─── Tool: Create a product ───────────────────────────────────────────────

    @JsonClassDescription("Create a new product in the catalog")
    public static class CreateProduct implements Supplier<String> {
        @JsonPropertyDescription("Name of the product")
        public String name;

        @JsonPropertyDescription("Detailed description of the product")
        public String description;

        @JsonPropertyDescription("Price of the product in USD")
        public double price;

        @JsonPropertyDescription("Available quantity in stock")
        public int quantity;

        @JsonPropertyDescription("Product category (e.g., Electronics, Clothing, Food)")
        public String category;

        @Override
        public String get() {
            try {
                ProductModel product = ProductModel.builder()
                        .name(name)
                        .description(description)
                        .price(price)
                        .quantity(quantity)
                        .category(category)
                        .build();
                ProductModel saved = productRepo.save(product);
                return objectMapper.writeValueAsString(saved);
            } catch (Exception e) {
                return "Error creating product: " + e.getMessage();
            }
        }
    }

    // ─── Tool: Update a product ───────────────────────────────────────────────

    @JsonClassDescription("Update an existing product in the catalog. Only provide the fields you want to change.")
    public static class UpdateProduct implements Supplier<String> {
        @JsonPropertyDescription("The unique ID of the product to update")
        public String id;

        @JsonPropertyDescription("New name for the product (optional)")
        public String name;

        @JsonPropertyDescription("New description for the product (optional)")
        public String description;

        @JsonPropertyDescription("New price in USD (optional, use null to leave unchanged)")
        public Double price;

        @JsonPropertyDescription("New quantity in stock (optional, use null to leave unchanged)")
        public Integer quantity;

        @JsonPropertyDescription("New category (optional)")
        public String category;

        @Override
        public String get() {
            try {
                Optional<ProductModel> optional = productRepo.findById(id);
                if (optional.isEmpty()) {
                    return "Product not found with id: " + id;
                }
                ProductModel product = optional.get();
                if (name != null)        product.setName(name);
                if (description != null) product.setDescription(description);
                if (price != null)       product.setPrice(price);
                if (quantity != null)    product.setQuantity(quantity);
                if (category != null)    product.setCategory(category);
                ProductModel updated = productRepo.save(product);
                return objectMapper.writeValueAsString(updated);
            } catch (Exception e) {
                return "Error updating product: " + e.getMessage();
            }
        }
    }

    // ─── Tool: Delete a product ───────────────────────────────────────────────

    @JsonClassDescription("Delete a product from the catalog by its ID")
    public static class DeleteProduct implements Supplier<String> {
        @JsonPropertyDescription("The unique ID of the product to delete")
        public String id;

        @Override
        public String get() {
            try {
                if (!productRepo.existsById(id)) {
                    return "Product not found with id: " + id;
                }
                productRepo.deleteById(id);
                return "Product with id " + id + " has been successfully deleted.";
            } catch (Exception e) {
                return "Error deleting product: " + e.getMessage();
            }
        }
    }

    // ─── Tool: Search products ────────────────────────────────────────────────

    @JsonClassDescription("Search for products by name or category")
    public static class SearchProducts implements Supplier<String> {
        @JsonPropertyDescription("Search term to match against product names (optional)")
        public String name;

        @JsonPropertyDescription("Category to filter products by (optional)")
        public String category;

        @Override
        public String get() {
            try {
                List<ProductModel> results;
                if (category != null && !category.isBlank()) {
                    results = productRepo.findByCategory(category);
                } else if (name != null && !name.isBlank()) {
                    results = productRepo.findByNameContainingIgnoreCase(name);
                } else {
                    return "Please provide either a name or category to search.";
                }
                if (results.isEmpty()) {
                    return "No products found matching the search criteria.";
                }
                return objectMapper.writeValueAsString(results);
            } catch (Exception e) {
                return "Error searching products: " + e.getMessage();
            }
        }
    }
}