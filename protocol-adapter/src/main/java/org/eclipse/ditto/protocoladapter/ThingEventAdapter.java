/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.protocoladapter;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.eclipse.ditto.json.JsonMissingFieldException;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.signals.events.things.AclEntryCreated;
import org.eclipse.ditto.signals.events.things.AclEntryDeleted;
import org.eclipse.ditto.signals.events.things.AclEntryModified;
import org.eclipse.ditto.signals.events.things.AclModified;
import org.eclipse.ditto.signals.events.things.AttributeCreated;
import org.eclipse.ditto.signals.events.things.AttributeDeleted;
import org.eclipse.ditto.signals.events.things.AttributeModified;
import org.eclipse.ditto.signals.events.things.AttributesCreated;
import org.eclipse.ditto.signals.events.things.AttributesDeleted;
import org.eclipse.ditto.signals.events.things.AttributesModified;
import org.eclipse.ditto.signals.events.things.FeatureCreated;
import org.eclipse.ditto.signals.events.things.FeatureDefinitionCreated;
import org.eclipse.ditto.signals.events.things.FeatureDefinitionDeleted;
import org.eclipse.ditto.signals.events.things.FeatureDefinitionModified;
import org.eclipse.ditto.signals.events.things.FeatureDeleted;
import org.eclipse.ditto.signals.events.things.FeatureModified;
import org.eclipse.ditto.signals.events.things.FeaturePropertiesCreated;
import org.eclipse.ditto.signals.events.things.FeaturePropertiesDeleted;
import org.eclipse.ditto.signals.events.things.FeaturePropertiesModified;
import org.eclipse.ditto.signals.events.things.FeaturePropertyCreated;
import org.eclipse.ditto.signals.events.things.FeaturePropertyDeleted;
import org.eclipse.ditto.signals.events.things.FeaturePropertyModified;
import org.eclipse.ditto.signals.events.things.FeaturesCreated;
import org.eclipse.ditto.signals.events.things.FeaturesDeleted;
import org.eclipse.ditto.signals.events.things.FeaturesModified;
import org.eclipse.ditto.signals.events.things.PolicyIdCreated;
import org.eclipse.ditto.signals.events.things.PolicyIdModified;
import org.eclipse.ditto.signals.events.things.ThingCreated;
import org.eclipse.ditto.signals.events.things.ThingDefinitionCreated;
import org.eclipse.ditto.signals.events.things.ThingDefinitionDeleted;
import org.eclipse.ditto.signals.events.things.ThingDefinitionModified;
import org.eclipse.ditto.signals.events.things.ThingDeleted;
import org.eclipse.ditto.signals.events.things.ThingEvent;
import org.eclipse.ditto.signals.events.things.ThingModified;

/**
 * Adapter for mapping a {@link ThingEvent} to and from an {@link Adaptable}.
 */
final class ThingEventAdapter extends AbstractThingAdapter<ThingEvent<?>> {

    private ThingEventAdapter(
            final Map<String, JsonifiableMapper<ThingEvent<?>>> mappingStrategies,
            final HeaderTranslator headerTranslator) {
        super(mappingStrategies, headerTranslator);
    }

    /**
     * Returns a new ThingEventAdapter.
     *
     * @param headerTranslator translator between external and Ditto headers.
     * @return the adapter.
     */
    public static ThingEventAdapter of(final HeaderTranslator headerTranslator) {
        return new ThingEventAdapter(mappingStrategies(), requireNonNull(headerTranslator));
    }

    private static Map<String, JsonifiableMapper<ThingEvent<?>>> mappingStrategies() {
        final Map<String, JsonifiableMapper<ThingEvent<?>>> mappingStrategies = new HashMap<>();

        mappingStrategies.put(ThingCreated.TYPE,
                adaptable -> ThingCreated.of(thingFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(ThingModified.TYPE,
                adaptable -> ThingModified.of(thingFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(ThingDeleted.TYPE,
                adaptable -> ThingDeleted.of(thingIdFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));

        mappingStrategies.put(AclModified.TYPE,
                adaptable -> AclModified.of(thingIdFrom(adaptable), aclFrom(adaptable), revisionFrom(adaptable),
                        timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(AclEntryCreated.TYPE,
                adaptable -> AclEntryCreated.of(thingIdFrom(adaptable), aclEntryFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(AclEntryModified.TYPE,
                adaptable -> AclEntryModified.of(thingIdFrom(adaptable), aclEntryFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(AclEntryDeleted.TYPE,
                adaptable -> AclEntryDeleted.of(thingIdFrom(adaptable), authorizationSubjectFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(AttributesCreated.TYPE,
                adaptable -> AttributesCreated.of(thingIdFrom(adaptable), attributesFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(AttributesModified.TYPE,
                adaptable -> AttributesModified.of(thingIdFrom(adaptable), attributesFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(AttributesDeleted.TYPE,
                adaptable -> AttributesDeleted.of(thingIdFrom(adaptable), revisionFrom(adaptable),
                        timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(AttributeCreated.TYPE,
                adaptable -> AttributeCreated.of(thingIdFrom(adaptable), attributePointerFrom(adaptable),
                        attributeValueFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(AttributeModified.TYPE,
                adaptable -> AttributeModified.of(thingIdFrom(adaptable), attributePointerFrom(adaptable),
                        attributeValueFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(AttributeDeleted.TYPE,
                adaptable -> AttributeDeleted.of(thingIdFrom(adaptable), attributePointerFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(ThingDefinitionCreated.TYPE,
                adaptable -> ThingDefinitionCreated.of(thingIdFrom(adaptable), thingDefinitionFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(ThingDefinitionModified.TYPE,
                adaptable -> ThingDefinitionModified.of(thingIdFrom(adaptable), thingDefinitionFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(ThingDefinitionDeleted.TYPE,
                adaptable -> ThingDefinitionDeleted.of(thingIdFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(FeaturesCreated.TYPE,
                adaptable -> FeaturesCreated.of(thingIdFrom(adaptable), featuresFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeaturesModified.TYPE,
                adaptable -> FeaturesModified.of(thingIdFrom(adaptable), featuresFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeaturesDeleted.TYPE,
                adaptable -> FeaturesDeleted.of(thingIdFrom(adaptable), revisionFrom(adaptable),
                        timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(FeatureCreated.TYPE,
                adaptable -> FeatureCreated.of(thingIdFrom(adaptable), featureFrom(adaptable), revisionFrom(adaptable),
                        timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeatureModified.TYPE,
                adaptable -> FeatureModified.of(thingIdFrom(adaptable), featureFrom(adaptable), revisionFrom(adaptable),
                        timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeatureDeleted.TYPE,
                adaptable -> FeatureDeleted.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(FeatureDefinitionCreated.TYPE,
                adaptable -> FeatureDefinitionCreated.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featureDefinitionFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeatureDefinitionModified.TYPE,
                adaptable -> FeatureDefinitionModified.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featureDefinitionFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeatureDefinitionDeleted.TYPE,
                adaptable -> FeatureDefinitionDeleted.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(FeaturePropertiesCreated.TYPE,
                adaptable -> FeaturePropertiesCreated.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featurePropertiesFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeaturePropertiesModified.TYPE,
                adaptable -> FeaturePropertiesModified.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featurePropertiesFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeaturePropertiesDeleted.TYPE,
                adaptable -> FeaturePropertiesDeleted.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        mappingStrategies.put(FeaturePropertyCreated.TYPE,
                adaptable -> FeaturePropertyCreated.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featurePropertyPointerFrom(adaptable), featurePropertyValueFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeaturePropertyModified.TYPE,
                adaptable -> FeaturePropertyModified.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featurePropertyPointerFrom(adaptable), featurePropertyValueFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(FeaturePropertyDeleted.TYPE,
                adaptable -> FeaturePropertyDeleted.of(thingIdFrom(adaptable), featureIdFrom(adaptable),
                        featurePropertyPointerFrom(adaptable), revisionFrom(adaptable), timestampFrom(adaptable),
                        dittoHeadersFrom(adaptable)));

        mappingStrategies.put(PolicyIdCreated.TYPE,
                adaptable -> PolicyIdCreated.of(thingIdFrom(adaptable), policyIdFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));
        mappingStrategies.put(PolicyIdModified.TYPE,
                adaptable -> PolicyIdModified.of(thingIdFrom(adaptable), policyIdFrom(adaptable),
                        revisionFrom(adaptable), timestampFrom(adaptable), dittoHeadersFrom(adaptable)));

        return mappingStrategies;
    }

    private static long revisionFrom(final Adaptable adaptable) {
        return adaptable.getPayload().getRevision().orElseThrow(() -> JsonMissingFieldException.newBuilder()
                .fieldName(Payload.JsonFields.REVISION.getPointer().toString()).build());
    }

    @Nullable
    private static Instant timestampFrom(final Adaptable adaptable) {
        return adaptable.getPayload().getTimestamp().orElse(null);
    }

    private static String getActionNameWithFirstLetterUpperCase(final TopicPath topicPath) {
        return topicPath.getAction()
                .map(TopicPath.Action::toString)
                .map(AbstractAdapter::upperCaseFirst)
                .orElseThrow(() -> new NullPointerException("TopicPath did not contain an Action!"));
    }

    @Override
    protected String getType(final Adaptable adaptable) {
        final TopicPath topicPath = adaptable.getTopicPath();
        final JsonPointer path = adaptable.getPayload().getPath();
        final String eventName = pathMatcher.match(path) + getActionNameWithFirstLetterUpperCase(topicPath);
        return topicPath.getGroup() + "." + topicPath.getCriterion() + ":" + eventName;
    }

    @Override
    public Adaptable constructAdaptable(final ThingEvent<?> event, final TopicPath.Channel channel) {
        final TopicPathBuilder topicPathBuilder = ProtocolFactory.newTopicPathBuilder(event.getThingEntityId());

        final EventsTopicPathBuilder eventsTopicPathBuilder;
        if (channel == TopicPath.Channel.TWIN) {
            eventsTopicPathBuilder = topicPathBuilder.twin().events();
        } else if (channel == TopicPath.Channel.LIVE) {
            eventsTopicPathBuilder = topicPathBuilder.live().events();
        } else {
            throw new IllegalArgumentException("Unknown Channel '" + channel + "'");
        }

        final String eventName = event.getClass().getSimpleName().toLowerCase();
        if (eventName.contains(TopicPath.Action.CREATED.toString())) {
            eventsTopicPathBuilder.created();
        } else if (eventName.contains(TopicPath.Action.MODIFIED.toString())) {
            eventsTopicPathBuilder.modified();
        } else if (eventName.contains(TopicPath.Action.DELETED.toString())) {
            eventsTopicPathBuilder.deleted();
        } else {
            throw UnknownEventException.newBuilder(eventName).build();
        }

        final PayloadBuilder payloadBuilder = Payload.newBuilder(event.getResourcePath())
                .withRevision(event.getRevision());
        event.getTimestamp().ifPresent(payloadBuilder::withTimestamp);

        final Optional<JsonValue> value =
                event.getEntity(event.getDittoHeaders().getSchemaVersion().orElse(event.getLatestSchemaVersion()));
        value.ifPresent(payloadBuilder::withValue);

        return Adaptable.newBuilder(eventsTopicPathBuilder.build())
                .withPayload(payloadBuilder.build())
                .withHeaders(ProtocolFactory.newHeadersWithDittoContentType(event.getDittoHeaders()))
                .build();
    }

}
