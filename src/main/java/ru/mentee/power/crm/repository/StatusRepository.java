package ru.mentee.power.crm.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Repository
public class StatusRepository {
    HashSet<String> statuces =  new HashSet<>();

    public void addStatus(String status) {
        statuces.add(status);
    }

    public void removeStatus(String status) {
        statuces.remove(status);
    }

    public boolean containsStatus(String status) {
        return statuces.contains(status);
    }

    public List<String> findAll() {
        return new ArrayList<String>(statuces);
    }
}
