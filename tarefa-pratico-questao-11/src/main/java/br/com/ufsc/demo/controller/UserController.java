package br.com.ufsc.demo.controller;

import br.com.ufsc.demo.model.User;
import br.com.ufsc.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody User user) {
        try {
            if (this.userService.find(user) != null) {
                return new ResponseEntity<>("Usuário autenticado", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Nenhum usuário encontrado",HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insert(@RequestBody User user) {
        try {
            if (user != null) {
                this.userService.insert(user);
                return new ResponseEntity<>("Usuário criado", HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("Parâmetros inválidos", HttpStatus.BAD_REQUEST);
            }
        }catch (DuplicateKeyException ex){
            return new ResponseEntity<>("Já existe usuário com este username", HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody User user) {
        try {
            if (user != null && id != null) {
                this.userService.update(id, user);
                return new ResponseEntity<>("Usuário atualizado", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Parâmetros inválidos", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            if (id != null) {
                this.userService.delete(id);
                return new ResponseEntity<>("Usuário deletado", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Parâmetros inválidos", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
