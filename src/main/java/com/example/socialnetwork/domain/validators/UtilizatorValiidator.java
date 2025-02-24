package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.exceptions.ValidationException;

public class UtilizatorValiidator implements Validator <Utilizator>{
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if(entity.getFirstName().equals(""))
            throw new ValidationException("FirstName nu poate fi nul");
        if(entity.getLastName().equals(""))
            throw new ValidationException("LastName nu poate fi nul");
        if(entity.getEmail().equals(""))
            throw new ValidationException("Email nu poate fi nul");
        if(!entity.getEmail().contains("@gmail.com") && !entity.getEmail().contains("@yahoo.com"))
            throw new ValidationException("Email invalid");
        if(entity.getEmail().length()<11)
            throw new ValidationException("Email invalid");
        if(entity.getUsername().equals(""))
            throw new ValidationException("Username nu poate fi nul");
        if(entity.getUsername().length()<4)
            throw new ValidationException("Username prea scurt, trebuie minim 4 caractere");
        if(entity.getPassword().equals(""))
            throw new ValidationException("Parola nu poate fi nul");
        if(entity.getPassword().length()<4)
            throw new ValidationException("Parola foarte slaba");


    }
}
