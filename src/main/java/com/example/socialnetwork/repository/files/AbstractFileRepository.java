package com.example.socialnetwork.repository.files;

import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename = fileName;
        loadData();
    }

    private void loadData() {
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                E entity=createEntity(line);
                super.save(entity);

            }
        }catch(IOException e){
            e.printStackTrace();

        }

    }


    public abstract E createEntity(String line);

    public abstract String saveEntity(E entity);

    @Override
    public Optional<E> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (e.isEmpty())
            writeToFile();
        return e;
    }

    private void writeToFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (E entity : entities.values()) {
                String ent = saveEntity(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<E> delete(ID id) {

        Optional<E> deleted = super.delete(id);
        if (deleted.isPresent()) {
            writeToFile();
        }
        return deleted;

    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> updated = super.update(entity);
        if (updated.isEmpty())
            writeToFile();
        return updated;

    }
}

