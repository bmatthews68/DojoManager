package com.btmatthews.dojomanager.dao;

import com.btmatthews.dojomanager.domain.Dojo;

import java.util.List;

public interface DojoDAO {

    List<Dojo> findAll(int offset, int limit);

    void create(Dojo dojo);

    Dojo read(String id);

    void update(Dojo dojo);

    void destroy(Dojo dojo);
}
