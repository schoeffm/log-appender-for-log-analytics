package de.bender.logging.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8787, 8888})
class HttpLogTransportTest {

    final String PAYLOAD = "[{\"this\":\"is a test\"}]";
    final String WORKSPACE_ID = "log-analytics-workspace-id";
    final String LOGGER_NAME = "LoggerName";

    @Test
    void send_willSendRequestAsExpected(MockServerClient client) throws Exception {
        // given
        HttpLogTransport underTest = new HttpLogTransport(LOGGER_NAME, () -> "http://127.0.0.1:" + client.getPort());
        client.when(request().withMethod("POST")).respond(response().withStatusCode(200));
        Signature signature = new Signature(PAYLOAD, WORKSPACE_ID, "SECRET");

        // when
        underTest.send(signature);

        // then
        client.verify(
                request()
                        .withMethod("POST")
                        .withHeader("Log-Type", "LoggerName")
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Authorization", "SharedKey " + signature.getWorkspaceId() + ":" + signature.getSignatureAsString())
                        .withHeader("Log-Type", "LoggerName")
                        .withBody(PAYLOAD)
                , exactly(1)
        );
    }
}