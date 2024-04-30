package com.sample.graphql;

import java.sql.SQLException;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import graphql.GraphQLException;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLRootContext;

/**
 * Mutation root
 */
public class Mutation implements GraphQLRootResolver {
    
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    
    public Mutation(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public Link createLink(String url, String description) {
        Link newLink = new Link(null, url, description);
        try {
			linkRepository.saveLink(newLink);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return newLink;
    }
    
    @GraphQLMutation //1
    public Link createLink(String url, String description, @GraphQLRootContext AuthContext context) { //2
        Link newLink = new Link(url, description, context.getUser().getId());
        try {
			linkRepository.saveLink(newLink);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return newLink;
    }
    
    public User createUser(String name, AuthData auth) {
        User newUser = new User(name, auth.getEmail(), auth.getPassword());
        try {
			return userRepository.saveUser(newUser);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newUser;
    }
    

    public SigninPayload signinUser(AuthData auth) {
        User user = null;
		try {
			user = userRepository.findByEmail(auth.getEmail());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (user.getPassword().equals(auth.getPassword())) {
            return new SigninPayload(user.getId(), user);
        }
        throw new GraphQLException("Invalid credentials");
    }
}
