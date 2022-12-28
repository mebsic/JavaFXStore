package com.javafxstore.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTest {

    private static Category category;

    @BeforeEach
    void setUp() {
        category = new Category(4267980152L, "Test Category 1", "Test Type 1");
    }

    @Test
    public void testGetCategoryID() {
        long id = 4267980152L;
        assertEquals(id, category.getId());
    }

    @Test
    public void testGetName() {
        String name = "Test Category 2";
        assertEquals(name, category.getName());
    }

    @Test
    public void testGetType() {
        String cat = "Test Type 1";
        assertEquals(cat, category.getType());
    }

    @Test
    public void testSetCategoryID() {
        long id = 4267980152L;
        category.setId(1234567890L);
        assertEquals(id, category.getId());
    }

    @Test
    public void testSetName() {
        String name = "Test Category 3";
        category.setName("Test Category 2");
        assertEquals(name, category.getName());
    }

    @Test
    public void testSetType() {
        String cat = "Test Type 3";
        category.setType("Test Type 3");
        assertEquals(cat, category.getType());
    }
}
