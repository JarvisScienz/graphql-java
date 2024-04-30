package com.sample.graphql;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coxautodev.graphql.tools.SchemaParser;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import graphql.validation.ValidationError;
import io.leangen.graphql.GraphQLSchemaGenerator;

/**
 * The servlet acting as the GraphQL endpoint
 * @param <GraphQLError>
 */
@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

	private static UserRepository userRepository = null;
	private static final long serialVersionUID = 3936594434298527600L;

	static {
    
        try {
			userRepository =  new UserRepository("jdbc:postgresql://localhost:5432/graphql?user=postgres&password=postgres", "postgres", "postgres");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public GraphQLEndpoint() {
        super(buildSchema());
    }

	/*
	 * 
	 * By schema
	 */
    private static GraphQLSchema buildSchema() {
    	System.out.println("buildSchema by schema");
        LinkRepository linkRepository = null;
		try {
			linkRepository = new LinkRepository("jdbc:postgresql://localhost:5432/graphql?user=postgres&password=postgres", "postgres", "postgres");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(
                		new Query(linkRepository), 
                		new Mutation(linkRepository, userRepository),
                		new SigninResolver())
                .build()
                .makeExecutableSchema();
    }
	
	/*
	 * 
	 * By code
	 */
	/*private static GraphQLSchema buildSchema() {
		System.out.println("buildSchema by code");
		LinkRepository linkRepository = null;
		try {
			linkRepository = new LinkRepository("jdbc:postgresql://localhost:5432/graphql?user=postgres&password=postgres", "postgres", "postgres");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Query query = new Query(linkRepository); //create or inject the service beans
	    LinkResolver linkResolver = new LinkResolver(userRepository);
	    Mutation mutation = new Mutation(linkRepository, userRepository);
	    
	    return new GraphQLSchemaGenerator()
	            .withOperationsFromSingletons(query, linkResolver, mutation) //register the beans
	            .generate(); //done :)
	}*/
    
//    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
//    	User user = request
//		        .map(req -> req.getHeader("Authorization"))
//		        .filter(id -> !id.isEmpty())
//		        .map(id -> id.replace("Bearer ", ""))
//		        .map(t -> {
//					try {
//						return userRepository.findById(t);
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return null;
//				})
//		        .orElse(null);
//		return new AuthContext(user, request, response);
//    }
    
    @Override
    protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
        return errors.stream()
                .filter(e -> e instanceof ExceptionWhileDataFetching || e instanceof ValidationError || super.isClientError(e))
                .map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
                .collect(Collectors.toList());
    }
}
