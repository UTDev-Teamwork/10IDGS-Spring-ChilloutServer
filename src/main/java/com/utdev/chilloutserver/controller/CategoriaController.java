package com.utdev.chilloutserver.controller;

import com.utdev.chilloutserver.model.Categoria;
import com.utdev.chilloutserver.model.Proveedor;
import com.utdev.chilloutserver.service.interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*" , methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@RequestMapping("api/categoria")
public class CategoriaController {

    @Autowired
    private ICategoriaService service;

    // http://localhost:7373/api/categoria/hello
    @GetMapping("/hello")
    public ResponseEntity<?> getHelloWorld()
    {
        return ResponseEntity.ok("Hello, world!");
    }

    // http://localhost:7373/api/categoria
    @GetMapping("/")
    public ResponseEntity<?> findCategorias(){
        return new ResponseEntity<>(service.findAllCategorias(), HttpStatus.OK);
    }

    // http://localhost:7373/api/categoria/{nombre}
    @GetMapping("{nombre}")
    public ResponseEntity<?> findByNombre(@PathVariable String nombre){
        return new ResponseEntity<>(service.findByNombre(nombre), HttpStatus.OK);
    }

    // http://localhost:7373/api/categoria/
    @PostMapping("/")
    public ResponseEntity<?> saveCategoria(@RequestBody Categoria categoria){
        Categoria newCategoria = service.saveCategoria(categoria);
        if (newCategoria != null)
            return new ResponseEntity<>(newCategoria, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // http://localhost:7373/api/categoria/danger/{id}
    @DeleteMapping("/danger/{id}")
    public ResponseEntity<?> deleteCategoria(@PathVariable int id){
        try{
            service.deleteByID(id);
            return ResponseEntity.ok("Registro eliminado permanentemente");
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
