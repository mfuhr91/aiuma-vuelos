'use strict';

$(document).ready(() => {
    $("#print-pdf").hide();
    $("#print-xls").hide();
    $("#datepicker").change();
   
    $("#alert_actualizado").hide();
    $("#alert_borrado").hide();
    $("#alert_import_borrado").hide();
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
        $(".alerta").fadeOut();
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

function confirmarBorrarImport(id, fechaImport,nombreArchivo, idBoton) {
    $('#borrarImportModal').modal('show');
    $('#borrarBoton').attr("onclick","borrarImport('"+ id +"','"+ idBoton + "')");
    fechaImport = moment(fechaImport).format("DD/MM/YYYY");
    $('.fechaImport').text(fechaImport)
    $('.nombreArchivo').text(nombreArchivo);
}
function borrarImport(id, idBoton) {

    idBoton = $(".lista-imports").find($(document.getElementById(idBoton)));
    $.ajax({
        type: "POST",
        url: "/vuelos/eliminarImport",
        
        contentType: "application/x-www-form-urlencoded",
        data: "id="+ id,
        timeout: 600000,
        success: function (data) {
            $("#borrarImportModal").modal("hide");
            idBoton.closest('tr').remove();
            $("#alert_import_borrado").fadeIn();
            window.setTimeout(() => {
                $("#alert_import_borrado").fadeOut();
            }, 2000);
        },
        error: function (e) {
            console.log("No se pudo borrar la importación, ERROR: " + e);
        }
    });

}

function guardarVuelo(id, nroVuelo, idBoton) {

    idBoton = $(document.getElementById(idBoton));
    let pos = idBoton.closest('td').prev('td').children().val();
    let fila = idBoton.closest('tr');
    let columnaGranPorte = fila.find('td:eq(5)');
    let granPorte = columnaGranPorte.find('input').is(':checked');
    pos = pos == '' ? 0 : pos;
    let vuelo = {
                    id: id,
                    nroVuelo: nroVuelo,
                    pos: pos,
                    granPorte: granPorte,
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

function asignarFechas(){
    let botonXLSdisabled= $("#print-xls-disabled");
    let botonPDFdisabled= $("#print-pdf-disabled");
    let botonXLS = $("#print-xls")
    let botonPDF = $("#print-pdf");
    let fechaDesde = $("#inputFechaDesde").val();
    let fechaHasta = $("#inputFechaHasta").val();
    console.log(fechaHasta);
    if(fechaDesde != "" && fechaHasta == "") {
        fechaHasta = $("#inputFechaHasta").val($("#inputFechaDesde").val());
    }
    if(fechaDesde == "" && fechaHasta != ""){
        fechaDesde = $("#inputFechaDesde").val($("#inputFechaHasta").val());
    }
    if(fechaDesde != "" && fechaHasta != ""){
        
        fechaDesde = moment($("#inputFechaDesde").val()).format("DD-MM-YYYY");
        fechaHasta = moment($("#inputFechaHasta").val()).format("DD-MM-YYYY");

        botonXLSdisabled.hide();
        botonPDFdisabled.hide();
        botonXLS.show();
        botonPDF.show();

        botonXLS.attr("href","/vuelos/imprimirProgramacion/xls/" + fechaDesde + '/' + fechaHasta + "?format=pdf");
        botonPDF.attr("href","/vuelos/imprimirProgramacion/pdf/" + fechaDesde + '/' + fechaHasta + "?format=pdf");

    }

}

