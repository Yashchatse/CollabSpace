package com.collabspace.loadtest.scenarios;

import io.gatling.javaapi.core.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class TaskScenario {

    public static ScenarioBuilder fullTaskFlow() {
        return scenario("Full Task Flow")
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
                http("Create Workspace for Tasks")
                    .post("/api/workspaces")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .body(StringBody(session -> {
                        String unique = System.currentTimeMillis() + "_" + session.userId();
                        return "{\"name\":\"Task WS " + unique + "\"," +
                               "\"description\":\"Task load test workspace\"}";
                    }))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.id").saveAs("workspaceId"))
            )
            .pause(1)
            .exec(
            	    http("Create Project for Tasks")
            	        .post(session -> "/api/workspaces/" + session.getString("workspaceId") + "/projects")
            	        .header("Authorization", session -> "Bearer " + session.getString("token"))
            	        .body(StringBody("{\"name\":\"Task Project\",\"description\":\"For task testing\"}"))
            	        .asJson()
            	        .check(status().is(200))
            	        .check(jsonPath("$.id").saveAs("projectId"))
            	)
            	.pause(1)
            	// New step — fetch columns and extract first column ID
            	.exec(
            	    http("Get Project Columns")
            	        .get(session -> "/api/projects/" + session.getString("projectId") + "/columns")
            	        .header("Authorization", session -> "Bearer " + session.getString("token"))
            	        .check(status().is(200))
            	        .check(jsonPath("$[0]").saveAs("columnId"))
            	) .pause(1)
            .exec(
                http("Get Project")
                    .get(session -> "/api/projects/" + session.getString("projectId"))
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            )
            .pause(1)
            .exec(
                http("Create Task")
                    // Use the extracted columnId, NOT hardcoded 1
                    .post(session -> "/api/tasks/column/" + session.getString("columnId"))
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .body(StringBody(session -> {
                        String unique = System.currentTimeMillis() + "_" + session.userId();
                        return "{\"title\":\"Task " + unique + "\"," +
                               "\"description\":\"Load test task\"," +
                               "\"priority\":\"HIGH\"," +
                               "\"assigneeEmail\":\"testuser@collabspace.com\"}";
                    }))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.id").saveAs("taskId"))
            )
            .pause(1)
            .exec(
                http("Get Tasks in Column")
                    .get(session -> "/api/tasks/column/" + session.getString("columnId"))
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            )
            .pause(1)
            .exec(
                http("Update Task")
                    .patch(session -> "/api/tasks/" + session.getString("taskId"))
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .body(StringBody("{\"title\":\"Updated Task\",\"priority\":\"MEDIUM\"}"))
                    .asJson()
                    .check(status().is(200))
            )
            .pause(1)
            .exec(
                http("Get Task Activity")
                    .get(session -> "/api/tasks/" + session.getString("taskId") + "/activity")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            );
    }

    public static ScenarioBuilder readOnlyTaskFlow() {
        return scenario("Read Only Task Flow")
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
                // Can't use column/1 blindly — get workspaces first then drill down,
                // or use a known seeded column. For now, get workspaces as a safe read.
                http("Get My Workspaces")
                    .get("/api/workspaces")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
            );
    }
}