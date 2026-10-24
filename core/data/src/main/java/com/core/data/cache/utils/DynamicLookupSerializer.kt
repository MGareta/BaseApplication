package com.core.data.cache.utils

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
@ExperimentalSerializationApi
class DynamicLookupSerializer : KSerializer<Any> {

    override val descriptor: SerialDescriptor = ContextualSerializer(
        serializableClass = Any::class,
        fallbackSerializer = null,
        typeArgumentsSerializers = emptyArray()
    ).descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        if (value is ArrayList<*>) {
            encoder.encodeSerializableValue(
                serializer = ListSerializer(
                    elementSerializer = DynamicLookupSerializer()
                ),
                value = value
            )

            return
        }

        val actualSerializer = encoder.serializersModule.getContextual(
            kClass = value::class
        ) ?: value::class.serializer()

        @Suppress("UNCHECKED_CAST")
        encoder.encodeSerializableValue(
            serializer = actualSerializer as KSerializer<Any>,
            value = value
        )
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}
