package com.example.socialnetwork.repository.files;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.domain.validators.Validator;

public class UtilizatorFileRepository extends AbstractFileRepository<Long, Utilizator>{
    public UtilizatorFileRepository(Validator<Utilizator> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Utilizator createEntity(String line) {
        String[] splited = line.split(";");
        Utilizator u = new Utilizator(splited[1], splited[2],splited[3],splited[4],splited[5]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String saveEntity(Utilizator entity) {
        String s = entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
        return s;
    }
}
