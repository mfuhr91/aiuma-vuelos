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
    $("#resultArribos").load('/vuelos/buscarArribos', { fecha });
    $("#resultSalidas").load('/vuelos/buscarSalidas', { fecha });
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