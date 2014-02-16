package com.btmatthews.dojomanager.service;

import com.btmatthews.dojomanager.domain.Dojo;
import com.btmatthews.dojomanager.domain.Mentor;
import com.btmatthews.dojomanager.domain.Session;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Period;

import java.util.List;

public interface DojoService {

    List<Dojo> listDojos(int offset,
                         int limit);

    Dojo newDojo(String name,
                 String mentorName,
                 String mentorEmail);

    Dojo getDojo(String id);

    void deleteDojo(Dojo dojo);

    Mentor addMentor(Dojo dojo,
                     String mentorName,
                     String mentorEmail);

    void removeMentor(Dojo dojo,
                      Mentor mentor);

    Session addSession(Dojo dojo,
                       String name,
                       String description,
                       DateTime time,
                       Period duration);

    void removeSession(Dojo dojo,
                       Session session);
}
