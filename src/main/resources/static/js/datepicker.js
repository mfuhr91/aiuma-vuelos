'use strict';
$(document).ready(() => {

    $("#datepicker").datepicker({
        monthNames: ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
        dayNamesMin: ["Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa"],
        dateFormat: "dd/mm/yy",
        firstDay: 1,
    });
 
    $("#datepicker").change(() => {
        var fecha = $("#datepicker").val();       
        buscarVuelos(fecha);
        enviarFecha(fecha);
        contarVuelos(fecha);

    })

});


function buscarVuelos(fecha) {

   /*  $.get('/vuelos/buscarArribos', { fecha })
        .done((data) => {
        console.log("ARRIBOS:     "+data);
        $("#resultArribos").html(data);
    });
 */
    $.ajax({
        type: 'GET',
        url: '/vuelos/buscarArribos',
        contentType: "application/x-www-form-urlencoded",
        data: 'fecha='+fecha,
        timeout: 600000,
        success: function (data) {
            console.log("ARRIBOS:     "+data);
            $("#resultArribos").html(data);
        },
        error: function (e) {
            console.log("LALALA: " + e);
        }
    });
   /*  $("#resultArribos").load('/vuelos/buscarArribos', { fecha }, (data) => {

        console.log("ARRIBOS:     "+data);
    });
    $("#resultSalidas").load('/vuelos/buscarSalidas', { fecha }, (data) => {

        console.log("SALIDAS:     " +data);
    }); */
}
function enviarFecha(fechaString){
    $("#resultAvisos").load('fechaSeleccionada', {fechaString});
}

function contarVuelos(fechaString){
    $.get("/vuelos/totalDiarios", {fechaString})
        .done((data) => {
            $("#total-vuelos").text(data);
        });
}