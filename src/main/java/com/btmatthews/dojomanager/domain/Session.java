package com.btmatthews.dojomanager.domain;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableSet;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Period;

public final class Session {

    private final String id;

    private final String name;

    private final String description;

    private final DateTime time;

    private final Period duration;

    private final ImmutableSet<MentorAvailability> mentorAvailabilities;

    public Session(final String id,
                   final String name,
                   final String description,
                   final DateTime time,
                   final Period duration,
                   final ImmutableSet<MentorAvailability> mentorAvailabilities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.duration = duration;
        this.mentorAvailabilities = mentorAvailabilities;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getTime() {
        return time;
    }

    public Period getDuration() {
        return duration;
    }

    public ImmutableSet<MentorAvailability> getMentorAvailabilities() {
        return mentorAvailabilities;
    }
}
