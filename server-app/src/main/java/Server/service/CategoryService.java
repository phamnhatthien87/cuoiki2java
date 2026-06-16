package Server.service;

import DAO.CategoryDAO;

import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<String[]> getAllCategories() {
        return categoryDAO.findAll();
    }

    public boolean addCategory(String name) {
        if (isBlank(name)) {
            return false;
        }

        return categoryDAO.insert(name.trim());
    }

    public boolean updateCategory(int id, String name) {
        if (id <= 0 || isBlank(name)) {
            return false;
        }

        return categoryDAO.update(id, name.trim());
    }

    public boolean deleteCategory(int id) {
        if (id <= 0) {
            return false;
        }

        return categoryDAO.delete(id);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
