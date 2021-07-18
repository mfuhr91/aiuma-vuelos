'use strict';
$(document).ready(() => {

    $("#datepicker").datepicker({
        monthNames: ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
        dayNamesMin: ["Do", "Lu", "Ma", "Me", "Ju", "Vi", "Sa"],
        dateFormat: "dd/mm/yy",
        firstDay: 1,
    });
 
    $("#datepicker").change(() => {
        var fecha = $("#datepicker").val();
        
        buscarVuelos(fecha);

        console.log(fecha);
    })
});


function buscarVuelos(fecha) {
    $("#resultArribos").load('/vuelos/buscarArribos', { fecha });
    $("#resultSalidas").load('/vuelos/buscarSalidas', { fecha });
}
