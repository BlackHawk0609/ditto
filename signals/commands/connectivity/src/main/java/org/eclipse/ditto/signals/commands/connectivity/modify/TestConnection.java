/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.signals.commands.connectivity.modify;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.json.JsonCollectors;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.connectivity.Connection;
import org.eclipse.ditto.model.connectivity.ConnectivityModelFactory;
import org.eclipse.ditto.model.connectivity.MappingContext;
import org.eclipse.ditto.signals.commands.base.AbstractCommand;
import org.eclipse.ditto.signals.commands.base.CommandJsonDeserializer;

/**
 * Command which test whether a {@link Connection} can successfully be established (e.g. by connecting to the endpoint,
 * configuring the MessageMappers, etc.).
 */
@Immutable
public final class TestConnection extends AbstractCommand<TestConnection>
        implements ConnectivityModifyCommand<TestConnection> {

    /**
     * Name of this command.
     */
    public static final String NAME = "testConnection";

    /**
     * Type of this command.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    static final JsonFieldDefinition<JsonObject> JSON_CONNECTION =
            JsonFactory.newJsonObjectFieldDefinition("connection", FieldType.REGULAR, JsonSchemaVersion.V_1,
                    JsonSchemaVersion.V_2);

    static final JsonFieldDefinition<JsonArray> JSON_MAPPING_CONTEXTS =
            JsonFactory.newJsonArrayFieldDefinition("mappingContexts", FieldType.REGULAR, JsonSchemaVersion.V_1,
                    JsonSchemaVersion.V_2);

    private final Connection connection;
    private final List<MappingContext> mappingContexts;

    private TestConnection(final Connection connection, final List<MappingContext> mappingContexts,
            final DittoHeaders dittoHeaders) {
        super(TYPE, dittoHeaders);
        this.connection = connection;
        this.mappingContexts = Collections.unmodifiableList(new ArrayList<>(mappingContexts));
    }

    /**
     * Returns a new instance of {@code TestConnection}.
     *
     * @param connection the connection to be created.
     * @param dittoHeaders the headers of the request.
     * @return a new TestConnection command.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static TestConnection of(final Connection connection, final DittoHeaders dittoHeaders) {
        return of(connection, Collections.emptyList(), dittoHeaders);
    }

    /**
     * Returns a new instance of {@code TestConnection}.
     *
     * @param connection the connection to be created.
     * @param mappingContexts the mapping contexts to apply for different content-types.
     * @param dittoHeaders the headers of the request.
     * @return a new TestConnection command.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static TestConnection of(final Connection connection, final List<MappingContext> mappingContexts,
            final DittoHeaders dittoHeaders) {
        checkNotNull(connection, "Connection");
        checkNotNull(mappingContexts, "mapping Contexts");
        return new TestConnection(connection, mappingContexts, dittoHeaders);
    }

    /**
     * Creates a new {@code TestConnection} from a JSON string.
     *
     * @param jsonString the JSON string of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonString} is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static TestConnection fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        return fromJson(JsonFactory.newObject(jsonString), dittoHeaders);
    }

    /**
     * Creates a new {@code TestConnection} from a JSON object.
     *
     * @param jsonObject the JSON object of which the command is to be created.
     * @param dittoHeaders the headers of the command.
     * @return the command.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static TestConnection fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new CommandJsonDeserializer<TestConnection>(TYPE, jsonObject).deserialize(() -> {
            final JsonObject jsonConnection = jsonObject.getValueOrThrow(JSON_CONNECTION);
            final Connection readConnection = ConnectivityModelFactory.connectionFromJson(jsonConnection);
            final JsonArray mappingContexts = jsonObject.getValue(JSON_MAPPING_CONTEXTS)
                    .orElse(JsonFactory.newArray());
            final List<MappingContext> readMappingContexts = mappingContexts.stream()
                    .filter(JsonValue::isObject)
                    .map(JsonValue::asObject)
                    .map(ConnectivityModelFactory::mappingContextFromJson)
                    .collect(Collectors.toList());

            return of(readConnection, readMappingContexts, dittoHeaders);
        });
    }

    /**
     * @return the {@code Connection} to be created.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @return the configured {@link MappingContext}s of the connection to be created.
     */
    public List<MappingContext> getMappingContexts() {
        return mappingContexts;
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(JSON_CONNECTION, connection.toJson(schemaVersion, thePredicate), predicate);
        jsonObjectBuilder.set(JSON_MAPPING_CONTEXTS, mappingContexts.stream()
                        .map(ms -> ms.toJson(schemaVersion, thePredicate))
                        .collect(JsonCollectors.valuesToArray()), predicate);
    }

    @Override
    public String getConnectionId() {
        return connection.getId();
    }

    @Override
    public TestConnection setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(connection, mappingContexts, dittoHeaders);
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return (other instanceof TestConnection);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final TestConnection that = (TestConnection) o;
        return Objects.equals(connection, that.connection) && Objects.equals(
                mappingContexts, that.mappingContexts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), connection, mappingContexts);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                super.toString() +
                ", connection=" + connection +
                ", mappingContexts=" + mappingContexts +
                "]";
    }

}
