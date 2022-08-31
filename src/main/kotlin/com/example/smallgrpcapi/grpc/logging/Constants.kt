package com.example.smallgrpcapi.grpc.logging

import io.grpc.Metadata

class Constants {
    object Constants {
        val REQUEST_ID_METADATA_KEY: Metadata.Key<String> =
            Metadata.Key.of("requestId", Metadata.ASCII_STRING_MARSHALLER)
    }
}