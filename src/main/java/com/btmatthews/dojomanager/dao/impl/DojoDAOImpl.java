package com.btmatthews.dojomanager.dao.impl;

import com.btmatthews.dojomanager.dao.DojoDAO;
import com.btmatthews.dojomanager.domain.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableSet;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Period;

import java.util.Date;
import java.util.List;

public final class DojoDAOImpl implements DojoDAO {

    private static final String DOJO_ENTITY = "Dojo";

    private static final String MENTOR_ENTITY = "Mentor";

    private static final String SESSION_ENTITY = "Session";

    private static final String MENTOR_AVAILABILITY_ENTITY = "MentorAvailability";

    private static final String ID_PROPERTY = "id";
    private static final String NAME_PROPERTY = "name";
    private static final String EMAIL_PROPERTY = "email";
    private static final String DESCRIPTION_PROPERTY = "description";
    private static final String TIME_PROPERTY = "time";
    private static final String DURATION_PROPERTY = "duration";
    private static final String MENTOR_PROPERTY = "mentor";
    private static final String AVAILABILITY_PROPERTY = "availability";

    @Override
    public List<Dojo> findAll(final int offset, final int limit) {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(DOJO_ENTITY).addSort(NAME_PROPERTY, Query.SortDirection.ASCENDING);
        final List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withOffset(offset).limit(limit));
        return Lists.transform(entities, new Function<Entity, Dojo>() {
            @Override
            public Dojo apply(final Entity dojoEntity) {
                return readFromDatastore(datastore, dojoEntity);
            }
        });
    }

    @Override
    public void create(final Dojo dojo) {
        writeToDataStore(dojo);
    }

    @Override
    public Dojo read(final String id) {
        try {
            final Key key = KeyFactory.createKey(DOJO_ENTITY, id);
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            final Entity dojoEntity = datastore.get(key);
            return readFromDatastore(datastore, dojoEntity);
        } catch (final EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public void update(final Dojo dojo) {
        writeToDataStore(dojo);
    }

    @Override
    public void destroy(final Dojo dojo) {
        final Key key = KeyFactory.createKey(DOJO_ENTITY, dojo.getId());
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.delete(key);
    }

    private Dojo readFromDatastore(final DatastoreService datastore,
                                   final Entity dojoEntity) {
        final String id = (String) dojoEntity.getProperty(ID_PROPERTY);
        final String name = (String) dojoEntity.getProperty(NAME_PROPERTY);
        final ImmutableSet<Mentor> mentors = readMentorsFromDataStore(datastore, dojoEntity);
        final ImmutableList<Session> sessions = readSessionsFromDataStore(datastore, dojoEntity);
        return new Dojo(id, name, mentors, sessions);
    }

    private ImmutableSet<Mentor> readMentorsFromDataStore(final DatastoreService datastore,
                                                          final Entity dojoEntity) {
        final Query mentorsQuery = new Query(MENTOR_ENTITY);
        mentorsQuery.setAncestor(dojoEntity.getKey());
        final List<Entity> mentorEntities = datastore.prepare(mentorsQuery).asList(
                FetchOptions.Builder.withDefaults());
        return ImmutableSet.copyOf(Lists.transform(mentorEntities, new Function<Entity, Mentor>() {
            @Override
            public Mentor apply(final Entity mentorEntity) {
                final String id = (String) mentorEntity.getProperty(ID_PROPERTY);
                final String name = (String) mentorEntity.getProperty(NAME_PROPERTY);
                final String email = (String) mentorEntity.getProperty(EMAIL_PROPERTY);
                return new Mentor(id, name, email);
            }
        }));
    }

    private ImmutableList<Session> readSessionsFromDataStore(final DatastoreService datastore,
                                                             final Entity dojoEntity) {
        final Query mentorsQuery = new Query(SESSION_ENTITY);
        mentorsQuery.setAncestor(dojoEntity.getKey());
        final List<Entity> mentorEntities = datastore.prepare(mentorsQuery).asList(
                FetchOptions.Builder.withDefaults());
        return ImmutableList.copyOf(Lists.transform(mentorEntities, new Function<Entity, Session>() {
            @Override
            public Session apply(final Entity sessionEntity) {
                final String id = (String) sessionEntity.getProperty(ID_PROPERTY);
                final String name = (String) sessionEntity.getProperty(NAME_PROPERTY);
                final String description = (String) sessionEntity.getProperty(DESCRIPTION_PROPERTY);
                final Date time = (Date) sessionEntity.getProperty(TIME_PROPERTY);
                final Integer duration = (Integer) sessionEntity.getProperty(DURATION_PROPERTY);
                final ImmutableSet<MentorAvailability> mentorAvailabilities = readMentorAvailabilitiesFromDataStore(datastore,
                        sessionEntity);
                return new Session(id, name, description, new DateTime(time), new Period(duration), mentorAvailabilities);
            }
        }));
    }

    private ImmutableSet<MentorAvailability> readMentorAvailabilitiesFromDataStore(final DatastoreService datastore,
                                                                                   final Entity dojoEntity) {
        final Query mentorsQuery = new Query(MENTOR_ENTITY);
        mentorsQuery.setAncestor(dojoEntity.getKey());
        final List<Entity> mentorEntities = datastore.prepare(mentorsQuery).asList(
                FetchOptions.Builder.withDefaults());
        return ImmutableSet.copyOf(Lists.transform(mentorEntities, new Function<Entity, MentorAvailability>() {
            @Override
            public MentorAvailability apply(final Entity mentorAvailabilityEntity) {
                final Mentor mentor = null;
                final String availability = (String) mentorAvailabilityEntity.getProperty("id");
                return new MentorAvailability(mentor, Availability.valueOf(availability));
            }
        }));
    }

    private void writeToDataStore(final Dojo dojo) {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Transaction transaction = datastore.beginTransaction();
        try {
            final Key dojoKey = KeyFactory.createKey(DOJO_ENTITY, dojo.getId());
            final Entity dojoEntity = new Entity(DOJO_ENTITY, dojoKey);
            dojoEntity.setProperty(ID_PROPERTY, dojo.getId());
            dojoEntity.setProperty(NAME_PROPERTY, dojo.getName());
            datastore.put(transaction, dojoEntity);
            for (final Mentor mentor : dojo.getMentors()) {
                writeToDataStore(dojoKey, mentor, datastore, transaction);
            }
            for (final Session session : dojo.getSessions()) {
                writeToDataStore(dojoKey, session, datastore, transaction);
            }
            transaction.commit();
        } catch (final Throwable e) {
            transaction.rollbackAsync();
        }
    }

    private void writeToDataStore(final Key dojoKey,
                                  final Mentor mentor,
                                  final DatastoreService datastore,
                                  final Transaction transaction) {
        final Key mentorKey = KeyFactory.createKey(dojoKey, MENTOR_ENTITY, mentor.getId());
        final Entity mentorEntity = new Entity(MENTOR_ENTITY, mentorKey);
        mentorEntity.setProperty(ID_PROPERTY, mentor.getId());
        mentorEntity.setProperty(NAME_PROPERTY, mentor.getName());
        mentorEntity.setProperty(EMAIL_PROPERTY, mentor.getEmail());
        datastore.put(transaction, mentorEntity);
    }

    private void writeToDataStore(final Key dojoKey,
                                  final Session session,
                                  final DatastoreService datastore,
                                  final Transaction transaction) {

        final Key sessionKey = KeyFactory.createKey(dojoKey, SESSION_ENTITY, session.getId());
        final Entity sessionEntity = new Entity(SESSION_ENTITY, sessionKey);
        sessionEntity.setProperty(ID_PROPERTY, session.getId());
        sessionEntity.setProperty(NAME_PROPERTY, session.getName());
        sessionEntity.setProperty(DESCRIPTION_PROPERTY, session.getDescription());
        sessionEntity.setProperty(TIME_PROPERTY, session.getTime().toDate());
        sessionEntity.setProperty(DURATION_PROPERTY, session.getDuration().getMinutes());
        datastore.put(transaction, sessionEntity);
        for (final MentorAvailability mentorAvailability : session.getMentorAvailabilities()) {
            writeToDataStore(dojoKey, sessionKey, mentorAvailability, datastore, transaction);
        }
    }

    private void writeToDataStore(final Key dojoKey,
                                  final Key sessionKey,
                                  final MentorAvailability mentorAvailability,
                                  final DatastoreService datastore,
                                  final Transaction transaction) {

        final Key mentorKey = KeyFactory.createKey(dojoKey, MENTOR_ENTITY, mentorAvailability.getMentor().getId());
        final Key mentorAvailabilityKey = KeyFactory.createKey(sessionKey, MENTOR_AVAILABILITY_ENTITY,
                mentorAvailability.getMentor().getId());
        final Entity mentorAvailabilityEntity = new Entity(MENTOR_AVAILABILITY_ENTITY, mentorAvailabilityKey);
        mentorAvailabilityEntity.setProperty(MENTOR_PROPERTY, mentorKey);
        mentorAvailabilityEntity.setProperty(AVAILABILITY_PROPERTY, mentorAvailability.getAvailability().toString());
        datastore.put(transaction, mentorAvailabilityEntity);
    }
}
