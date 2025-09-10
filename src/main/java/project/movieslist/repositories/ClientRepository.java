package project.movieslist.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import project.movieslist.model.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
    public Optional<Client> findByUsername(String username);
    public Client findByEmail(String email);
}
