package com.mfuhr.vuelos.controllers;

import java.util.List;

import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.services.VueloService;
import com.mfuhr.vuelos.utils.Aviso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class HomeController {

    private final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    VueloService vueloService;
    
    @GetMapping({"","/","/home","/index","/inicio"})
    public String home(Model model){
        
        List<Vuelo> vuelos = this.vueloService.buscarArribosDelMesActual();
        List<Aviso> avisos = this.vueloService.comprobarVuelos(vuelos);
        System.out.println("HOMECONTROLLER - TOTAL VUELOS: " + vuelos.size());
        model.addAttribute("avisos", avisos);
        return "inicio";
    }
}
