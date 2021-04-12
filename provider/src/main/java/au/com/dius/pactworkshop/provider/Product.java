package au.com.dius.pactworkshop.provider;

import java.util.Objects;

public class Product {

    private String id;
    private String type;
    private String name;
    private String version;

    public Product() {
    }

    public Product(String id,
                   String type,
                   String name,
                   String version) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(type, product.type) &&
                Objects.equals(name, product.name) &&
                Objects.equals(version, product.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, version);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
