package com.function.GraphQL;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStreamReader;
import java.io.InputStream;

public class GraphQLProvider {

    private static GraphQL graphQL;

    static {
        try {
            InputStream schemaStream = GraphQLProvider.class.getClassLoader().getResourceAsStream("GraphQL/schema.graphqls");
            if (schemaStream == null) {
                throw new RuntimeException("No se encontró schema.graphqls");
            }

            TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(new InputStreamReader(schemaStream));

            RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                    .type("Mutation", builder -> builder.dataFetcher("sendEmail", new SendEmailDataFetcher()))
                    .build();

            GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
            graphQL = GraphQL.newGraphQL(schema).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GraphQL getGraphQL() {
        return graphQL;
    }
}
