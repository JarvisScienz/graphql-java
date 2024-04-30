package com.sample.graphql;

/**
 * Manages link persistence
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

public class LinkRepository {

    private final Connection connection;

    public LinkRepository(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    public Link findById(String id) throws SQLException {
        String sql = "SELECT * FROM links WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Link(
                        resultSet.getString("id"),
                        resultSet.getString("url"),
                        resultSet.getString("description"));
            } else {
                return null;
            }
        }
    }

    public List<Link> getAllLinks() throws SQLException {
        List<Link> allLinks = new ArrayList<>();
        String sql = "SELECT * FROM links";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allLinks.add(new Link(
                        resultSet.getString("id"),
                        resultSet.getString("url"),
                        resultSet.getString("description")));
            }
        }
        return allLinks;
    }
    
    /*
     * 
     * Versione 2 - getAllLinks con semplice filtro
     */
    /*public List<Link> getAllLinks(LinkFilter filter) {

        StringBuilder whereClause = new StringBuilder();
        List<String> queryParams = new ArrayList<>(); // For storing dynamic parameters

        if (filter != null) {
            String descriptionContains = filter.getDescriptionContains();
            String urlContains = filter.getUrlContains();

            if (descriptionContains != null && !descriptionContains.isEmpty()) {
                whereClause.append(" description LIKE ? ");
                queryParams.add("%" + descriptionContains + "%");
            }

            if (urlContains != null && !urlContains.isEmpty()) {
                if (whereClause.length() > 0) {
                    whereClause.append(" AND ");
                }
                whereClause.append(" url LIKE ? ");
                queryParams.add("%" + urlContains + "%");
            }
        }

        String query = "SELECT * FROM links " + (whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "");

        List<Link> allLinks = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {

            for (int i = 0; i < queryParams.size(); i++) {
                ps.setString(i + 1, queryParams.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String url = rs.getString("url");
                String description = rs.getString("description");
                allLinks.add(new Link(url, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        return allLinks;
    }*/
    
    /*
     * 
     * Versione 3 - getAllLinks con filtro e paginazione
     */
    public List<Link> getAllLinks(LinkFilter filter, int pageNumber, int pageSize) {

        StringBuilder whereClause = new StringBuilder();
        List<String> queryParams = new ArrayList<>(); // For storing dynamic parameters

        if (filter != null) {
            String descriptionContains = filter.getDescriptionContains();
            String urlContains = filter.getUrlContains();

            if (descriptionContains != null && !descriptionContains.isEmpty()) {
                whereClause.append(" description LIKE ? ");
                queryParams.add("%" + descriptionContains + "%");
            }

            if (urlContains != null && !urlContains.isEmpty()) {
                if (whereClause.length() > 0) {
                    whereClause.append(" AND ");
                }
                whereClause.append(" url LIKE ? ");
                queryParams.add("%" + urlContains + "%");
            }
        }

        int offset = (pageNumber - 1) * pageSize; // Calculate offset for pagination

        String query = "SELECT * FROM links " +
                (whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "") +
                " OFFSET ? LIMIT ?"; // Apply offset and limit for pagination

        List<Link> allLinks = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {

            for (int i = 0; i < queryParams.size(); i++) {
                ps.setString(i + 1, queryParams.get(i));
            }
            ps.setInt(queryParams.size() + 1, offset); // Set offset parameter
            ps.setInt(queryParams.size() + 2, pageSize); // Set limit parameter
            System.out.println("Query getAllLink: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String url = rs.getString("url");
                String description = rs.getString("description");
                allLinks.add(new Link(url, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        return allLinks;
    }


    public void saveLink(Link link) throws SQLException {
    	System.out.println("Save link function");
    	String sql = "INSERT INTO links (id, url, description) VALUES (nextval('links_id_seq'), ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link.getUrl());
            statement.setString(2, link.getDescription());
            statement.executeUpdate();
        }
    }
}
