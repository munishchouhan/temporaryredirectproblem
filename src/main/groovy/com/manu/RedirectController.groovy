package com.manu

import groovy.transform.CompileStatic
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpHeaders
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

@CompileStatic
@Controller("/")
class RedirectController {

    MutableHttpResponse createResponse() {
        final override = Map.of(
                'Location', 'http://foo.com',
                'Content-Length', '0',
                'Connection', 'close' )
        return HttpResponse
                .status(HttpStatus.valueOf(307))
                .headers(toMutableHeaders(override))
    }

    @Get("/redirect/pass")
    MutableHttpResponse redirectPass() {
        createResponse()
    }

    @Get("/redirect/fail")
    CompletableFuture<MutableHttpResponse> redirectFail() {
        CompletableFuture.completedFuture(createResponse())
    }

    static protected Consumer<MutableHttpHeaders> toMutableHeaders(Map<String,String> override) {
        new Consumer<MutableHttpHeaders>() {
            @Override
            void accept(MutableHttpHeaders mutableHttpHeaders) {
                for( Map.Entry<String,String> entry : override ) {
                    mutableHttpHeaders.putAt(entry.key, entry.value)
                }
            }
        }
    }
}
