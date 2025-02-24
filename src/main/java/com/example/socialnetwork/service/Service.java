package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.exceptions.ServiceException;
import com.example.socialnetwork.exceptions.ValidationException;
import com.example.socialnetwork.repository.database.FriendhipReqDBRepo;
import com.example.socialnetwork.repository.database.MessageDBRepo;
import com.example.socialnetwork.repository.database.PrietenieDBRepo;
import com.example.socialnetwork.repository.database.UtilizatorDBRepo;
import com.example.socialnetwork.utils.events.*;
import com.example.socialnetwork.utils.observers.Observable;
import com.example.socialnetwork.utils.observers.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements Observable<Event> {
    private List<Observer<Event>> observers=new ArrayList<>();
    private final FriendhipReqDBRepo repoFR;
    private final UtilizatorDBRepo repoU;
    private final PrietenieDBRepo repoP;
    private final MessageDBRepo repoM;
    public Service(UtilizatorDBRepo repoU,PrietenieDBRepo repoP,MessageDBRepo repoM,FriendhipReqDBRepo repoFR ) {
        this.repoU=repoU;
        this.repoP=repoP;
        this.repoM=repoM;
        this.repoFR=repoFR;
    }

    public void addUser(String firstname, String lastname, String email, String password, String username) {
        if( password == null || password.length() < 4 ) {
            throw new ServiceException("Parola este prea slaba");
        }
        try {
            String hashedPassword = Utilizator.hashPassword(password);
            Utilizator u = new Utilizator(firstname, lastname, hashedPassword, email, username);
            repoU.save(u);
            notifyObservers(new UtilizatorEvent(ChangeTypeEvent.ADD, u));
        }catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }

    }
    public Iterable<Prietenie> getPrietenieu(Long id) {
        return repoP.findAllp(id);
    }

    public Iterable<Prietenie> getPrietenie() {
        return repoP.findAll();
    }


    public Utilizator findbyUsername(String username) {
        return StreamSupport.stream(repoU.findAll().spliterator(),false)
                .filter(user->user.getUsername().equals(username)).findFirst().orElse(null);
    }


    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<Event> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.forEach(observer -> observer.update(t));
    }

    public List<Utilizator> findUsersByPartialName(String searchText) {
        Iterable<Utilizator>utilizatori=findAllUtilizatori();
        List<Utilizator>utilizatorList= StreamSupport.stream(utilizatori.spliterator(),false)
                .toList();
        return utilizatorList.stream()
                .filter(user -> (user.getUsername() +" "+ user.getFirstName()+" "+ user.getLastName()).toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    private Iterable<Utilizator> findAllUtilizatori() {
        return repoU.findAll();
    }

    public boolean areFriends(Long id, Long id1) {
        Prietenie prietenie = repoP.findOnep(id,id1).orElse(null);
        if (prietenie != null && "Accepted".equals(prietenie.getStatus())) {
            return true;

        }
        return false;
    }
    public boolean requestExists(Long id, Long id1) {
        FriendRequest prietenie = repoFR.findOne(id,id1).orElse(null);
        if (prietenie != null && prietenie.getStatus().equals(FriendshipStatus.PENDING)) {
            return true;
        }
        return false;
    }

    public Utilizator findUtilizator(long id) {
        return repoU.findOne(id).orElseThrow(() -> new ValidationException("Utilizator nu exista"));
    }

    public Prietenie findPrietenie(Long id1,Long id2) {
        return repoP.findOnep(id1,id2).orElseThrow(() -> new ValidationException("Prietenia nu exista"));
    }
    public long pendingreq(long user){
        return repoFR.findAllpending(user);
    }

    public FriendRequest sendFriendRequest(Long idSender, Long idReceiver) {
        FriendRequest prietenie = new FriendRequest(idSender, idReceiver, FriendshipStatus.PENDING,LocalDateTime.now());
        String message = "Ai primit o cerere de prietenie de la " + findUtilizator(idSender).getFirstName() + " " + findUtilizator(idSender).getLastName();
        repoFR.save(prietenie);
        FrEvent event = new FrEvent(ChangeTypeEvent.ADD, prietenie);
        notifyObservers(event);
        return prietenie;
        //notifyObservers(new NotificationEvent(message));


    }

    public void acceptFriendRequest(Long idSender, Long idReceiver) {
        FriendRequest prietenie = repoFR.findOnepending(idSender, idReceiver,FriendshipStatus.PENDING ).orElse(null);
        if (prietenie != null ) {
            prietenie.setStatus(FriendshipStatus.ACCEPTED);
            repoFR.update(prietenie);
            repoP.save(new Prietenie(idSender,idReceiver,"Accepted"));
            FrEvent event = new FrEvent(ChangeTypeEvent.UPDATE, prietenie);
            PrietenieEvent pevent= new PrietenieEvent(ChangeTypeEvent.ADD,new Prietenie(idSender,idReceiver,"Accepted"));
            notifyObservers(event);
            notifyObservers(pevent);

        }
    }

    public void rejectFriendRequest(Long idSender, Long idReceiver) {
        FriendRequest prietenie = repoFR.findOne(idSender, idReceiver).orElse(null);
        if (prietenie != null && prietenie.getStatus().equals(FriendshipStatus.PENDING)) {
            prietenie.setStatus(FriendshipStatus.REJECTED);
            repoFR.update(prietenie);
            FrEvent event = new FrEvent(ChangeTypeEvent.UPDATE, prietenie);
            notifyObservers(event);

        }
    }
    public Optional<Utilizator> removeUtilizator(Long id) {
        Optional<Utilizator> u = repoU.findOne(id);
        if (u.isEmpty()) {
            throw new IllegalArgumentException("Utilizator nu exista");
        }
        List<Long> prieteniiDeSters = new ArrayList<>();


        getPrietenie().forEach(p->{
            if (p.getIdUser1().equals(id)|| p.getIdUser2().equals(id)) {
                prieteniiDeSters.add(p.getId());
            }
        });


        prieteniiDeSters.forEach(this::stergePrietenie);



//        u.get().getPrieteni().forEach(u1-> u1.removeFriend(u.get()));
        if (u.isPresent()) {
            return repoU.delete(id);
        }
        return Optional.empty();


    }

    public void stergePrietenie(Long idPrietenie) {


        Optional<Prietenie> prietenie = repoP.findOne(idPrietenie);

        if (prietenie.isEmpty()) {
            throw new IllegalArgumentException("Prietenia nu exista");
        }
        Long idUser1 = prietenie.get().getIdUser1();
        Long idUser2 = prietenie.get().getIdUser2();

        Optional<Utilizator> u1 =repoU.findOne(idUser1);
        Optional<Utilizator> u2 = repoU.findOne(idUser2);
        repoP.delete(idPrietenie);
        repoFR.delete(idUser1,idUser2);
        PrietenieEvent event=new PrietenieEvent(ChangeTypeEvent.DELETE,prietenie.get());
        notifyObservers(event);

    }

    public List<Message>getMessagesBetween(Utilizator u1, Utilizator u2) {
        if(u1==null||u2==null){
            throw new IllegalArgumentException("utilizatori nu pot fi nuli");
        }
        Iterable<Message>messages=repoM.findAll();
        List<Message>messageList=StreamSupport.stream(messages.spliterator(),false).toList();

        return messageList.stream().filter(m->m.getFrom().equals(u1)&&m.getTo().equals(List.of(u2)) ||
                m.getFrom().equals(u2)&&m.getTo().equals(List.of(u1)) ).toList();



    }

    public ReplyMessage replyMessage(Utilizator from, List<Utilizator> to, String message, Message repliedMessage) {
        ReplyMessage reply = new ReplyMessage(from, to, message, LocalDateTime.now(), repliedMessage);
        repoM.save(reply);
        return reply;
    }




    public List<FriendRequest> getReceivedFriendRequests(Long id) {
        Iterable<FriendRequest>prieteni=getFriendR();
        List<FriendRequest>prietenieList=StreamSupport.stream(prieteni.spliterator(),false).toList();
        return prietenieList.stream()
                .filter(p ->((p.getFromUserId().equals(id)) ||(p.getToUserId().equals(id))) && (p.getStatus().equals(FriendshipStatus.PENDING)))
                .collect(Collectors.toList());
    }

    private Iterable<FriendRequest> getFriendR() {
        return repoFR.findAll();
    }

    public Message sendMessage(Utilizator from, List<Utilizator> to, String messageText, Message reply) {
        LocalDateTime sentAt = LocalDateTime.now();
        if(reply==null) {
            Message newMessage = new Message(from, to, messageText, sentAt);
            Optional<Message> savedMessage = repoM.save(newMessage);
            MessageEvent event = new MessageEvent(newMessage);
            notifyObservers(event);
            return savedMessage.orElse(null);
        }else{
            ReplyMessage newMessage=new ReplyMessage( from, to, messageText, sentAt, reply);
            Optional<Message> savedMessage = repoM.save(newMessage);
            MessageEvent event = new MessageEvent(newMessage);
            notifyObservers(event);
            return savedMessage.orElse(null);
        }

    }
}
