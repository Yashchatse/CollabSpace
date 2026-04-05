package com.collabspace.loadtest.scenarios;

import io.gatling.javaapi.core.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class AuthScenario {

    public static ScenarioBuilder registerAndLogin() {
        return scenario("Register and Login Flow")
            .exec(session -> {
                // Unique per-user ID combining time + session userId
                String unique = System.currentTimeMillis() + "_" + session.userId();
                return session.set("unique", unique);
            })
            .exec(
                http("Register User")
                    .post("/api/auth/register")
                    .body(StringBody(session ->
                        "{\"name\":\"Load User " + session.getString("unique") + "\"," +
                        "\"email\":\"loadtest_" + session.getString("unique") + "@test.com\"," +
                        "\"password\":\"Test@1234\"}"
                    ))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.accessToken").saveAs("token"))
                    .check(jsonPath("$.email").saveAs("email"))
            )
            .pause(1)
            .exec(
                http("Login with same user")
                    .post("/api/auth/login")
                    .body(StringBody(session ->
                        "{\"email\":\"" + session.getString("email") + "\"," +
                        "\"password\":\"Test@1234\"}"
                    ))
                    .asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.accessToken").saveAs("token"))
            )
            .pause(1)
            .exec(
                http("Get My Profile")
                    .get("/api/users/me")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .check(status().is(200))
                    .check(jsonPath("$.email").exists())
            )
            .pause(1)
            .exec(
                http("Update Profile")
                    .put("/api/users/me")
                    .header("Authorization", session -> "Bearer " + session.getString("token"))
                    .body(StringBody("{\"name\":\"Updated Load User\",\"bio\":\"Load tested\"}"))
                    .asJson()
                    .check(status().is(200))
            );
    }

    public static ScenarioBuilder loginOnly() {
        return scenario("Login Only")
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
            );
    }
}