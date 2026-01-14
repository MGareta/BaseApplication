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

/**
 * `DynamicLookupSerializer` is a custom [KSerializer] that handles the serialization of objects of unknown types at compile time.
 * It dynamically determines the appropriate serializer for the given object during runtime.
 *
 * This serializer is particularly useful when dealing with heterogeneous collections or objects where the exact type
 * is not known until runtime.
 *
 * **Key Features:**
 *
 * 1.  **Runtime Type Resolution:** It leverages the `serializersModule` of the [Encoder] to find a suitable contextual
 *     serializer based on the runtime type of the object being serialized. If a contextual serializer isn't found, it
 *     falls back to the default serializer for that type.
 * 2.  **List Handling:** It provides special handling for `ArrayList` instances, recursively applying
 *     `DynamicLookupSerializer` to the elements within the list.
 * 3.  **Contextual Serialization:** It uses the `ContextualSerializer` as its base descriptor to indicate that it's
 *     handling contextual serialization.
 * 4. **Serialization Only** The deserialization function is not implemented
 *
 * **Usage:**
 *
 * This serializer is typically used in conjunction with a `serializersModule` that provides contextual serializers
 * for various types. When an object is encountered during serialization, the serializer checks the `serializersModule`
 * for a matching contextual serializer. If one is found, it's used; otherwise, the default serializer for the type is used.
 *
 * **Example:**
 *
 * ```kotlin
 * import kotlinx.serialization.json.Json
 * import kotlinx.serialization.modules.SerializersModule
 * import kotlinx.serialization.modules.contextual
 * import kotlinx.serialization.Serializable
 *
 * @Serializable
 * data class MyData(val name: String, val value: Int)
 *
 * val module = SerializersModule {
 *     contextual(MyData::class, MyData.serializer())
 * }
 *
 * val json = Json { serializersModule = module */
@OptIn(InternalSerializationApi::class)
@ExperimentalSerializationApi
class DynamicLookupSerializer : KSerializer<Any> {
    /**
     * The serial descriptor for this serializer.
     *
     * This descriptor is obtained from a [ContextualSerializer] instance configured to handle
     * serialization of objects of any type (`Any::class`). It indicates that this serializer
     * is responsible for contextual serialization, where the actual serializer used depends
     * on the runtime type of the object being serialized.
     *
     * Since this serializer is designed for dynamic lookup and handles a wide range of types,
     * the descriptor is based on `Any::class` with no specific fallback serializer or type arguments.
     *
     * @see ContextualSerializer
     */
    override val descriptor: SerialDescriptor = ContextualSerializer(
        serializableClass = Any::class,
        fallbackSerializer = null,
        typeArgumentsSerializers = emptyArray(),
    ).descriptor

    /**
     * Serializes the given value using a dynamically determined serializer.
     *
     * This function handles the serialization of objects of unknown types at compile time.
     * It first checks if the value is an `ArrayList`. If so, it uses a `ListSerializer` with
     * `DynamicLookupSerializer` as the element serializer to serialize the list elements recursively.
     *
     * If the value is not an `ArrayList`, it attempts to find a contextual serializer for the value's type
     * using the `serializersModule` of the encoder. If a contextual serializer is found, it is used to
     * serialize the value. Otherwise, the default serializer for the value's type is used.
     *
     * @param encoder The encoder to use for serialization.
     * @param value The value to serialize.
     *
     * @see DynamicLookupSerializer
     * @see ListSerializer
     */
    override fun serialize(encoder: Encoder, value: Any) {
        if (value is ArrayList<*>) {
            encoder.encodeSerializableValue(
                serializer = ListSerializer(DynamicLookupSerializer()),
                value = value,
            )

            return
        }

        val actualSerializer = encoder.serializersModule.getContextual(
            kClass = value::class,
        ) ?: value::class.serializer()

        @Suppress("UNCHECKED_CAST")
        encoder.encodeSerializableValue(
            serializer = actualSerializer as KSerializer<Any>,
            value = value,
        )
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}
