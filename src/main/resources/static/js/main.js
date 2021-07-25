'use strict';

$(document).ready(() => {
    $("#datepicker").change();
   
    $("#alert_actualizado").hide();
    $("#alert_borrado").hide();
    let tabArribos = $("#tab-arribos");
    let tabSalidas = $("#tab-salidas");
    tabArribos.click(() => {
        tabArribos.addClass("active");
        tabSalidas.removeClass("active");
        $("#resultArribos").show();
        $("#resultSalidas").hide();

    });

    tabSalidas.click(() => {
        tabSalidas.addClass("active");
        tabArribos.removeClass("active");
        $("#resultSalidas").show();
        $("#resultArribos").hide();

    });

    if (tabArribos.hasClass("active")) {
        $("#resultSalidas").hide();
    }

    window.setTimeout(() => {
        $(".alerta-guardadogit s").fadeOut();
    }, 2000);
});

// formulario carga individual
function optionSelect(obj){
    let select = $(obj);
    let botones = $("#usados").children().text();
    select.children().each(function(){
       
        if($(this).prop("selected")){
            let cod = $(this).text().substr(0,3);
            console.log(botones);
            console.log(cod);

            if(!botones.includes(cod)){
                $("#usados").children().removeClass("bg-primary text-light");
            } else {
                $("#usados").children().siblings().removeClass("bg-primary text-light");
                $("#usados").children("button:contains(" + cod + ")").addClass("bg-primary text-light");
            }
           
        }
    });

}

function clickBtn(obj){

    let boton = $(obj);
    boton.addClass("bg-primary text-light");
    boton.siblings().removeClass("bg-primary text-light");
    console.log(boton.text());

    $("#origen option").each(function(){
        if($( this ).text().includes(boton.text())){
            $(this).siblings().removeAttr("selected");
            $(this).attr("selected","true");
        }
    });
}

function confirmarBorrarVuelo(id, nroVuelo, idBoton) {
    let fechaVuelo = $("#datepicker").val();
    $('#borrarModal').modal('show');
    $('#borrarBoton').attr("onclick","borrarVuelo('"+ id +"','"+ idBoton + "')");
    $('.nroVuelo').text(nroVuelo);
    $('.fechaVuelo').text(fechaVuelo);
}

function borrarVuelo(id, idBoton) {

    idBoton = $("#result").find($(document.getElementById(idBoton)));
    $.ajax({
        type: "POST",
        url: "/vuelos/eliminar",
        
        contentType: "application/x-www-form-urlencoded",
        data: "id="+ id,
        timeout: 600000,
        success: function (data) {
            $("#borrarModal").modal("hide");
            idBoton.closest('tr').remove();
            $("#alert_borrado").fadeIn();
            window.setTimeout(() => {
                $("#alert_borrado").fadeOut();
            }, 2000);
        },
        error: function (e) {
            console.log("No se pudo borrar el vuelo, ERROR: " + e);
        }
    });

}
function guardarVuelo(id, nroVuelo, idBoton) {

    idBoton = $(document.getElementById(idBoton));
    let pos = idBoton.closest('td').prev('td').children().val();
    console.log(id);
    console.log(nroVuelo);
    console.log(pos);
    let vuelo = {
                    id: id,
                    nroVuelo: nroVuelo,
                    pos: pos,
                }
    $.ajax({
        type: "POST",
        url: "/vuelos/actualizar",
        contentType: "application/json",
        data: JSON.stringify(vuelo),
        dataType: 'json',
        timeout: 600000,
    }).always(() => {
       $("#alert_actualizado").fadeIn();
       
      window.setTimeout(() => { 
         $("#alert_actualizado").fadeOut(); 
      }, 2000); 
    });
}



function confirmarBorrarUltimoImport() {
    $('#borrarImportModal').modal('show');
}

