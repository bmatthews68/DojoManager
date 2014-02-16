package com.btmatthews.dojomanager.domain;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableSet;

public final class Dojo {

    private final String id;

    private final String name;

    private final ImmutableSet<Mentor> mentors;

    private final ImmutableList<Session> sessions;

    public Dojo(final String id,
                final String name,
                final ImmutableSet<Mentor> mentors,
                final ImmutableList<Session> sessions) {
        this.id = id;
        this.name = name;
        this.mentors = mentors;
        this.sessions = sessions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<Mentor> getMentors() {
        return mentors;
    }

    public ImmutableList<Session> getSessions() {
        return sessions;
    }
}
