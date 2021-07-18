'use strict';

$(document).ready(() => {

    $("#datepicker").change();
    $("#alert_guardado").hide();
    let tabArribos = $("#tab-arribos");
    let tabSalidas = $("#tab-salidas");
   // console.log($(".row").find("#tab-salidas"));
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

});

function confirmarBorrarVuelo(id, nroVuelo, idBoton) {
    // ABAJO OK
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
            
            console.log(idBoton);
            $("#borrarModal").modal("hide");
            idBoton.closest('tr').remove();
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
       $("#alert_guardado").fadeIn();
       
      window.setTimeout(() => { 
         $("#alert_guardado").fadeOut(); 
      }, 2000); 
    });
}

function confirmarBorrarUltimoImport() {
    $('#borrarImportModal').modal('show');
}

