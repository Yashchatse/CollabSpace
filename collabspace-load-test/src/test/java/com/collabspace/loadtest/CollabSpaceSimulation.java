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
            // Auth flow — 5 users at once (smoke test)
            AuthScenario.registerAndLogin()
                .injectOpen(atOnceUsers(5)),

            // Workspace flow — ramp up to 20 users
            WorkspaceScenario.fullWorkspaceFlow()
                .injectOpen(
                    nothingFor(10),
                    rampUsers(20).during(30)
                ),

            // Read-only workspace — simulate browsing users
            WorkspaceScenario.readOnlyWorkspaceFlow()
                .injectOpen(
                    nothingFor(5),
                    constantUsersPerSec(5).during(30)
                ),

            // Task flow — moderate load
            TaskScenario.fullTaskFlow()
                .injectOpen(
                    nothingFor(15),
                    rampUsers(10).during(20)
                ),

            // Read-only task polling
            TaskScenario.readOnlyTaskFlow()
                .injectOpen(
                    nothingFor(10),
                    constantUsersPerSec(3).during(40)
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