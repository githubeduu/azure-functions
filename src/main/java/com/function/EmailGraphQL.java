package com.function;

import com.function.GraphQL.GraphQLProvider;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import graphql.ExecutionResult;
import graphql.GraphQL;

import java.util.Map;

public class EmailGraphQL {

    @FunctionName("EmailGraphQL")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Map<String, Object>> request,
        final ExecutionContext context) {

        try {
            GraphQL graphQL = GraphQLProvider.getGraphQL();
            if (graphQL == null) {
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("GraphQL no inicializado.")
                        .build();
            }

            Map<String, Object> body = request.getBody();
            if (body == null || !body.containsKey("query")) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("No se proporcionó una consulta GraphQL.")
                        .build();
            }

            String query = (String) body.get("query");

            ExecutionResult executionResult = graphQL.execute(query);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(executionResult.toSpecification())
                    .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en GraphQL: " + e.getMessage())
                    .build();
        }
    }
}
