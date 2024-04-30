package com.sample.graphql;

import java.sql.SQLException;

import com.coxautodev.graphql.tools.GraphQLResolver;

/**
 * Contains vote sub-queries
 */
public class VoteResolver implements GraphQLResolver<Vote> {
    
    
    private final UserRepository userRepository;

    VoteResolver(LinkRepository linkRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User user(Vote vote) {
        try {
			return userRepository.findById(vote.getUserId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
//    public Link link(Vote vote) {
//        return linkRepository.findById(vote.getLinkId());
//    }
}
