package com.example.socialnetwork.repository.memory;

import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.exceptions.ValidationException;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    protected Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if(id==null){
            throw new IllegalArgumentException("ID este invalid");
        }
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws ValidationException {

        /**
         *
         * @param entity
         *         entity must be not null
         * @return null- if the given entity is saved
         *         otherwise returns the entity (id already exists)
         * @throws ValidationException
         *            if the entity is not valid
         * @throws IllegalArgumentException
         *             if the given entity is null.     *
         */

        if(entity==null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        validator.validate(entity);
        if(entities.containsKey(entity.getId()))
            return Optional.of(entity);
        else{
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }


    }

    @Override
    public Optional<E> delete(ID id) {
        if(id==null){
            throw new IllegalArgumentException("ID CANNOT BE NULL");
        }
        return Optional.ofNullable(entities.remove(id));

    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        if (entities.containsKey(entity.getId())) {
            validator.validate(entity);
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
        return Optional.of(entity);

    }

}

