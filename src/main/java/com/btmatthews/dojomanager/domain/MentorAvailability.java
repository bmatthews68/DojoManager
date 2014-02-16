package com.btmatthews.dojomanager.domain;

public final class MentorAvailability {

    private final Mentor mentor;

    private final Availability availability;

    public MentorAvailability(final Mentor mentor,
                              final Availability availability) {
        this.mentor = mentor;
        this.availability = availability;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public Availability getAvailability() {
        return availability;
    }
}
