package com.btmatthews.dojomanager.service.impl;

import com.btmatthews.dojomanager.dao.DojoDAO;
import com.btmatthews.dojomanager.domain.Dojo;
import com.btmatthews.dojomanager.domain.Mentor;
import com.btmatthews.dojomanager.domain.Session;
import com.btmatthews.dojomanager.service.DojoService;
import com.google.appengine.repackaged.com.google.common.base.Predicate;
import com.google.appengine.repackaged.com.google.common.collect.Collections2;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableSet;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Period;

import java.util.List;
import java.util.UUID;

public final class DojoServiceImpl implements DojoService {

    private DojoDAO dao;

    public DojoServiceImpl(final DojoDAO dao) {
        this.dao = dao;
    }

    public List<Dojo> listDojos(final int offset,
                                final int limit) {
        return dao.findAll(offset, limit);
    }

    public Dojo newDojo(final String name,
                        final String mentorName,
                        final String mentorEmail) {
        final Mentor mentor = new Mentor(generateId(), mentorName, mentorEmail);
        final ImmutableSet<Mentor> mentors = ImmutableSet.of(mentor);
        final Dojo dojo = new Dojo(generateId(), name, mentors, ImmutableList.<Session>of());
        dao.create(dojo);
        return dojo;
    }

    public Dojo getDojo(final String id) {
        return dao.read(id);
    }

    public void deleteDojo(final Dojo dojo) {
        dao.destroy(dojo);
    }

    public Mentor addMentor(final Dojo dojo,
                            final String mentorName,
                            final String mentorEmail) {
        final Mentor mentor = new Mentor(generateId(), mentorName, mentorEmail);
        final ImmutableSet<Mentor> mentors = ImmutableSet
                .<Mentor>builder()
                .addAll(dojo.getMentors())
                .add(mentor)
                .build();
        final Dojo updatedDojo = new Dojo(dojo.getId(), dojo.getName(), mentors, dojo.getSessions());
        dao.update(updatedDojo);
        return mentor;
    }

    public void removeMentor(final Dojo dojo,
                             final Mentor mentor) {
        final ImmutableSet<Mentor> mentors = ImmutableSet.<Mentor>builder().addAll(
                Collections2.filter(
                        dojo.getMentors(),
                        new Predicate<Mentor>() {
                            @Override
                            public boolean apply(final Mentor item) {
                                return item.getId().equals(mentor.getId());
                            }
                        })).build();
        final Dojo updateDojo = new Dojo(dojo.getId(), dojo.getName(), mentors, dojo.getSessions());
        dao.update(updateDojo);
    }

    public Session addSession(final Dojo dojo,
                              final String name,
                              final String description,
                              final DateTime time,
                              final Period duration) {
        final Session session = new Session(generateId(), name, description, time, duration, ImmutableSet.<Mentor>of());
        final ImmutableList<Session> sessions = ImmutableList
                .<Session>builder()
                .addAll(dojo.getSessions())
                .add(session)
                .build();
        final Dojo updatedDojo = new Dojo(dojo.getId(), dojo.getName(), dojo.getMentors(), sessions);
        dao.update(updatedDojo);
        return session;
    }

    public void removeSession(final Dojo dojo,
                              final Session session) {
        final ImmutableList<Session> sessions = ImmutableList.<Session>builder().addAll(
                Collections2.filter(
                        dojo.getSessions(),
                        new Predicate<Session>() {
                            @Override
                            public boolean apply(final Session item) {
                                return item.getId().equals(session.getId());
                            }
                        })).build();
        final Dojo updatedDojo = new Dojo(dojo.getId(), dojo.getName(), dojo.getMentors(), sessions);
        dao.update(updatedDojo);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
