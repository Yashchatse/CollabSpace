package com.collabspace.loadtest.scenarios;

import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class WorkspaceScenario {

    public static ScenarioBuilder fullWorkspaceFlow() {
        return scenario("Full Workspace Flow")
            // Step 1 - Login
            .exec(
                http("Login")
                    .post("/api/auth/login")
                    .body(StringBody(
                        "{\"email\":\"testuser@collabspace.com\"," +
                        "\"password\":\"Test@1234\"}"
                    ))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.accessToken").saveAs("token"))
            )
            .pause(1)

            // Step 2 - Create Workspace
            .exec(
                http("Create Workspace")
                    .post("/api/workspaces")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .body(StringBody(session -> {
                        String unique = String.valueOf(System.currentTimeMillis());
                        return "{\"name\":\"Workspace " + unique + "\"," +
                               "\"description\":\"Created by Gatling load test\"}";
                    }))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.id").saveAs("workspaceId"))
                    .check(jsonPath("$.name").exists())
            )
            .pause(1)

            // Step 3 - Get All My Workspaces
            .exec(
                http("Get My Workspaces")
                    .get("/api/workspaces")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            )
            .pause(1)

            // Step 4 - Get Single Workspace
            .exec(
                http("Get Workspace by ID")
                    .get(session -> "/api/workspaces/" + session.getString("workspaceId"))
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
                    .check(jsonPath("$.id").exists())
            )
            .pause(1)

            // Step 5 - Create Project inside Workspace
            .exec(
                http("Create Project")
                    .post(session -> "/api/workspaces/" + session.getString("workspaceId") + "/projects")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .body(StringBody("{\"name\":\"Load Test Project\"," +
                                    "\"description\":\"Gatling generated project\"}"))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.id").saveAs("projectId"))
            )
            .pause(1)

            // Step 6 - Get Projects in Workspace
            .exec(
                http("Get Projects in Workspace")
                    .get(session -> "/api/projects/workspace/" + session.getString("workspaceId"))
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            );
    }

    public static ScenarioBuilder readOnlyWorkspaceFlow() {
        return scenario("Read Only Workspace Flow")
            .exec(
                http("Login")
                    .post("/api/auth/login")
                    .body(StringBody(
                        "{\"email\":\"testuser@collabspace.com\"," +
                        "\"password\":\"Test@1234\"}"
                    ))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.accessToken").saveAs("token"))
            )
            .pause(1)
            .exec(
                http("Get My Workspaces")
                    .get("/api/workspaces")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            )
            .pause(1)
            .exec(
                http("Get My Profile")
                    .get("/api/users/me")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            );
    }
}