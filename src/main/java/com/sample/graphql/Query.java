package com.sample.graphql;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * Query root. Contains top-level queries.
 */
class Query implements GraphQLRootResolver {

    private final LinkRepository linkRepository;

    public Query(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    public List<Link> allLinks(LinkFilter filter, Number skip, Number first) {
        return linkRepository.getAllLinks(filter, skip.intValue(), first.intValue());
    }
    
    /*@GraphQLQuery //2
    public List<Link> allLinks(LinkFilter filter,
                               @GraphQLArgument(name = "skip", defaultValue = "0") Number skip, //3
                               @GraphQLArgument(name = "first", defaultValue = "0") Number first) {
    	System.out.println("Call allLinks. Filter: " + filter.getDescriptionContains() + "/" + filter.getUrlContains() + " - skip: " + skip + " - first: " + first);
        return linkRepository.getAllLinks(filter, skip.intValue(), first.intValue());
    }*/
}
