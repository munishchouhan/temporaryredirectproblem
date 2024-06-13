package com.manu

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class RedirectControllerTest extends Specification {

    @Inject
    @Client("/")
    HttpClient client

    def 'Successful test: should redirect'() {
        when:
        def request = HttpRequest.GET("/redirect/pass")
        def response = client.toBlocking().exchange(request)

        then:
        response.status.code == 307
        response.headers.get("Location") == "http://foo.com"
    }

    def 'failing test: should redirect'() {
        when:
        def request = HttpRequest.GET("/redirect/fail")
        def response = client.toBlocking().exchange(request)

        then:
        response.status.code == 307
        response.headers.get("Location") == "http://foo.com"
    }
}
