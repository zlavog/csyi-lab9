package amazon.cloudshape.lab9.model;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class CatInfo {

    private String name;
    private Integer age;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatInfo withName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public CatInfo withAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CatInfo withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CatInfo that = (CatInfo) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.age, that.age)
                && Objects.equals(this.description, that.description);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("age", age)
                .add("description", description)
                .toString();
    }
}
