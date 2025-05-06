package com.group20.dailyreadingtracker.role;

import java.util.Set;

import com.group20.dailyreadingtracker.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Entity representing a user role in the system.
 * 
 * Maps to the "roles" table and maintains:
 * - Unique role names (e.g., "ROLE_USER", "ROLE_ADMIN")
 * - Bidirectional many-to-many relationship with User entities
 * 
 * Used for role-based authorization throughout the application.
 * 
 * @author Sofiia Mamonova
 */

@Entity
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy="roles")
    private Set<User> users;

    public Role(){};

    public Role(String name){
        this.name = name;
    }

    public Role(long id, String name, Set<User> users) {
        super();
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
