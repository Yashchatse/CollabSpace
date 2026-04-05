package com.collabspace.loadtest;

import com.collabspace.loadtest.scenarios.*;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CollabSpaceSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling-LoadTest/1.0");

    {
    	setUp(
    		    AuthScenario.registerAndLogin()
    		        .injectOpen(atOnceUsers(2)),          // was 5

    		    WorkspaceScenario.fullWorkspaceFlow()
    		        .injectOpen(
    		            nothingFor(10),
    		            rampUsers(5).during(20)           // was rampUsers(20).during(30)
    		        ),

    		    WorkspaceScenario.readOnlyWorkspaceFlow()
    		        .injectOpen(
    		            nothingFor(5),
    		            constantUsersPerSec(2).during(20) // was 5 users/sec for 30s
    		        ),

    		    TaskScenario.fullTaskFlow()
    		        .injectOpen(
    		            nothingFor(15),
    		            rampUsers(3).during(15)           // was rampUsers(10).during(20)
    		        ),

    		    TaskScenario.readOnlyTaskFlow()
    		        .injectOpen(
    		            nothingFor(10),
    		            constantUsersPerSec(2).during(30) // was 3/sec for 40s
    		        )
    		)
    		.protocols(httpProtocol)
    		.assertions(
    		    global().responseTime().max().lt(5000),
    		    global().responseTime().percentile(95).lt(2000),
    		    global().responseTime().percentile(99).lt(3000),
    		    global().successfulRequests().percent().gt(90.0)
    		);
    }
}