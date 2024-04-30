package com.sample.graphql;

import java.sql.SQLException;

import com.coxautodev.graphql.tools.GraphQLResolver;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;

/**
 * Contains Link sub-queries
 */
public class LinkResolver implements GraphQLResolver<Link> {
    
    private final UserRepository userRepository;

    LinkResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GraphQLQuery
    public User postedBy(@GraphQLContext Link link) throws SQLException { //2
        if (link.getId() == null) {
            return null;
        }
        return userRepository.findById(link.getId());
    }
}
