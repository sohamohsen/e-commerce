package com.task.ecommerce.service;

import com.task.ecommerce.auth.dto.RegistrationRequest;
import com.task.ecommerce.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private static final String REALM = "ecommerce";
    private static final String CUSTOMER_ROLE = "CUSTOMER";

    private final Keycloak keycloak;

    public String createCustomer(
            RegistrationRequest request
    ) {

        UserRepresentation user = new UserRepresentation();

        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        user.singleAttribute("phone", request.getPhone());

        CredentialRepresentation credential =
                new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        try (Response response =
                     keycloak
                             .realm(REALM)
                             .users()
                             .create(user)) {

            int status = response.getStatus();

            String body = response.hasEntity()
                    ? response.readEntity(String.class)
                    : "";

            System.out.println(
                    "KEYCLOAK CREATE USER STATUS = " + status
            );

            System.out.println(
                    "KEYCLOAK CREATE USER BODY = " + body
            );

            System.out.println(
                    "KEYCLOAK LOCATION = "
                            + response.getHeaderString("Location")
            );

            if (status == Response.Status.CONFLICT.getStatusCode()) {

                throw new UserAlreadyExistsException(
                        "User already exists with this email."
                );
            }

            if (status != Response.Status.CREATED.getStatusCode()) {

                throw new RuntimeException(
                        "Keycloak create user failed. " +
                                "Status=" + status +
                                ", body=" + body
                );
            }

            String location =
                    response.getHeaderString("Location");

            String keycloakUserId =
                    location.substring(
                            location.lastIndexOf("/") + 1
                    );

            assignCustomerRole(keycloakUserId);

            return keycloakUserId;
        }
    }

    private void assignCustomerRole(String userId) {

        RoleRepresentation customerRole =
                keycloak
                        .realm(REALM)
                        .roles()
                        .get(CUSTOMER_ROLE)
                        .toRepresentation();

        keycloak
                .realm(REALM)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(customerRole));
    }

    public void deleteUser(String keycloakUserId) {

        try (Response response = keycloak
                .realm(REALM)
                .users()
                .delete(keycloakUserId)) {

            int status = response.getStatus();

            if (status != 204 && status != 404) {

                String body = response.hasEntity()
                        ? response.readEntity(String.class)
                        : "";

                throw new RuntimeException(
                        "Failed to delete Keycloak user. " +
                                "Status=" + status +
                                ", body=" + body
                );
            }
        }
    }
}