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

    @Get("/redirect/pass")
    HttpResponse redirectPass() {
        final override = Map.of(
                'Location', 'http://foo.com',  // <-- the location can be relative to the origin host, override it to always return a fully qualified URI
                'Content-Length', '0',  // <-- make sure to set content length to zero, some services return some content even with the redirect header that's discarded by this response
                'Connection', 'close' ) // <-- make sure to return connection: close header otherwise docker hangs
        return HttpResponse
                .status(HttpStatus.valueOf(307))
                .headers(toMutableHeaders([:], override))
    }

    @Get("/redirect/fail")
    CompletableFuture<MutableHttpResponse<?>> redirectFail() {
        final override = Map.of(
                'Location', 'http://foo.com',  // <-- the location can be relative to the origin host, override it to always return a fully qualified URI
                'Content-Length', '0',  // <-- make sure to set content length to zero, some services return some content even with the redirect header that's discarded by this response
                'Connection', 'close' ) // <-- make sure to return connection: close header otherwise docker hangs
        def ret =  HttpResponse
                .status(HttpStatus.valueOf(307))
                .headers(toMutableHeaders([:], override))

        CompletableFuture.completedFuture(ret)
    }

    static protected Consumer<MutableHttpHeaders> toMutableHeaders(Map<String,List<String>> headers, Map<String,String> override=Collections.emptyMap()) {
        new Consumer<MutableHttpHeaders>() {
            @Override
            void accept(MutableHttpHeaders mutableHttpHeaders) {
                for( Map.Entry<String,List<String>> entry : headers ) {
                    for( String value : entry.value )
                        mutableHttpHeaders.add(entry.key, value)
                }
                // override headers with specified value
                for( Map.Entry<String,String> entry : override ) {
                    mutableHttpHeaders.putAt(entry.key, entry.value)
                }
            }
        }
    }
}
